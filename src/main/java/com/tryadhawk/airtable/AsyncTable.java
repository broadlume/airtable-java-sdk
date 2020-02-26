/*
 * Copyright 2020, Airtable-java Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package com.tryadhawk.airtable;

import java.util.Objects;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tryadhawk.airtable.exception.AirtableException;
import com.tryadhawk.airtable.exception.AirtableMappingException;
import com.tryadhawk.airtable.internal.http.AirtableHttpClient;
import com.tryadhawk.airtable.internal.http.MimeType;
import com.tryadhawk.airtable.internal.http.QueryRequestBuilder;
import com.tryadhawk.airtable.v0.Delete;
import com.tryadhawk.airtable.v0.Record;
import com.tryadhawk.airtable.v0.Records;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.Response;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client for asynchronously accessing an Airtable table
 * @param <T> the type table row data will be mapped to
 */
public class AsyncTable<T> {

    private static final Logger logger = LoggerFactory.getLogger(AsyncTable.class);

    private final String url;
    private final String apiKey;
    private final Class<T> type;
    private final AirtableHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final QueryRequestBuilder queryRequestBuilder = new QueryRequestBuilder();

    /**
     * Create a new instance
     * @param url the endpoint URL of this table
     * @param apiKey the API key to use for authentication
     * @param type the type to map row data to
     * @param httpClient the client to use to make requests
     * @param objectMapper the mapper to use for mapping row data to {@code type}
     */
    AsyncTable(String url, String apiKey, Class<T> type, AirtableHttpClient httpClient, ObjectMapper objectMapper) {
        this.apiKey = Objects.requireNonNull(apiKey, "apiKey cannot be null");
        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.url = Objects.requireNonNull(url, "url cannot be null");
        this.httpClient = Objects.requireNonNull(httpClient);
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    /**
     * Retrieve all rows from the table
     * @return a {@link Publisher} containing all rows in the table or an {@link AirtableException} if an error occurs
     */
    public Publisher<Record<T>> select() {
        return select(Query.builder().build());
    }

    /**
     * Retrieve rows from the table matching a {@link Query}
     * @param query the query
     * @return a {@link Publisher} containing matching rows in the table or an {@link AirtableException} if an error
     * occurs
     */
    public Publisher<Record<T>> select(Query query) {
        return executeQuery(query)
                .flatMap(records -> Flowable.fromIterable(records.getRecords()));
    }

    /**
     * Find a row in the table by its row ID
     * @param id the row ID
     * @return a {@link Publisher} containing the matching row or an {@link AirtableException} if an error occurs
     */
    public Publisher<Record<T>> find(String id) {
        Request request = buildBaseRequest("GET", getTableUrl() + "/" + id).build();
        return Single.just(request)
                .flatMap(httpClient::execute)
                // TODO handle 404?
                .map(response -> parseResponseBodyAsRecord(response))
                .doOnError(e -> logger.warn("Failed to find id {}", id))
                .toFlowable();
    }

    /**
     * Create a new row in the table
     * @param item the data for the row
     * @return a {@link Publisher} containing the created row or an {@link AirtableException} if an error occurs
     */
    public Publisher<Record<T>> create(T item) {
        return Single.just(Record.of(item))
                .map(this::bodyToJson)
                .map(body -> buildBaseRequest("POST", getTableUrl())
                        .addHeader("Content-Type", MimeType.APPLICATION_JSON)
                        .setBody(body)
                        .build())
                .flatMap(httpClient::execute)
                .map(response -> parseResponseBodyAsRecord(response))
                .doOnError(e -> logger.warn("Failed to create item {}", item))
                .toFlowable();
    }

    // TODO support batch create/update/delete methods
    // TODO support PUT update method

    /**
     * Update an existing row in the table. Only non-null fields in {@code item} will be updated, all other fields will
     * be left as they were
     * @param id the row's row ID
     * @param item the data to update
     * @return a {@link Publisher} containing the updated row or an {@link AirtableException} if an error occurs
     */
    public Publisher<Record<T>> update(String id, T item) {
        return Single.just(Record.of(item))
                .map(this::bodyToJson)
                .map(body -> buildBaseRequest("PATCH", getTableUrl() + "/" + id)
                        .addHeader("Content-Type", MimeType.APPLICATION_JSON)
                        .setBody(body)
                        .build())
                .flatMap(httpClient::execute)
                .map(response -> parseResponseBodyAsRecord(response))
                .doOnError(e -> logger.warn("Failed to update id {}", id))
                .toFlowable();
    }

    /**
     * Delete a row by its row ID
     * @param id the row ID
     * @return a {@link Publisher} containing whether the row was deleted or an {@link AirtableException} if an error
     * occurs
     */
    public Publisher<Boolean> delete(String id) {
        Request request = buildBaseRequest("DELETE", getTableUrl() + "/" + id).build();
        return httpClient.execute(request)
                .map(response -> (Delete) parseResponseBody(response, objectMapper.constructType(Delete.class)))
                .map(delete -> delete.isDeleted())
                .doOnError(e -> logger.warn("Failed to delete id {}", id))
                .toFlowable();
    }

    /**
     * Execute a query and automatically fetch the next result set if there is a next set
     * @param query the query to execute
     * @return all result rows for the query
     */
    private Flowable<Records<T>> executeQuery(Query query) {
        return Single.just(query)
                .map(q -> queryRequestBuilder.buildRequestForQuery(q, getTableUrl())
                        .setHeader("Accept", MimeType.APPLICATION_JSON)
                        .setHeader("Authorization", getAuthenticationHeader())
                        .build())
                .flatMap(httpClient::execute)
                .map(response -> parseResponseBodyAsRecords(response))
                .doOnError(e -> logger.warn("Failed to execute query {}", query))
                .flatMapPublisher(records -> handleResponsePagination(records, query));
    }

    /**
     * Automatically fetch the next set of records after this set if there is a next set
     * @param response the current response
     * @param query the query used to fetch the current response
     * @return a Flowable of this set of records appended with the next set
     */
    private Flowable<Records<T>> handleResponsePagination(Records<T> response, Query query) {
        Flowable<Records<T>> f = Flowable.just(response);
        String offset = response.getOffset();
        if (offset != null) {
            logger.debug("Concatenating with next result set at offset {}", offset);
            f = f.concatWith(executeQuery(query.toBuilder().offset(offset).build()));
        }
        return f;
    }

    /**
     * Map an object to JSON
     * @param body the object to map
     * @return the mapped JSON
     * @throws AirtableMappingException if unable to map to JSON
     */
    private byte[] bodyToJson(Object body) {
        try {
            return objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException e) {
            throw new AirtableMappingException("Failed to map data to JSON", e);
        }
    }

    /**
     * Parse the body of a response as JSON into a {@link Records}
     * @param response the response
     * @return the parsed Records
     * @throws AirtableMappingException if unable to parse the JSON
     */
    private Records<T> parseResponseBodyAsRecords(Response response) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(Records.class, type);
        return parseResponseBody(response, javaType);
    }

    /**
     * Parse the body of a response as JSON into a {@link Record}
     * @param response the response
     * @return the parsed Record
     * @throws AirtableMappingException if unable to parse the JSON
     */
    private Record<T> parseResponseBodyAsRecord(Response response) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(Record.class, type);
        return parseResponseBody(response, javaType);
    }

    /**
     * Parse the body of a response as JSON into an instance of a specified type
     * @param <X> the type the response is parsed into
     * @param response the response
     * @param javaType the type to parse the response into
     * @return the parsed object
     * @throws AirtableMappingException if unable to parse the JSON
     */
    private <X> X parseResponseBody(Response response, JavaType javaType) {
        try {
            return objectMapper.readValue(response.getResponseBody(), javaType);
        } catch (JsonProcessingException e) {
            throw new AirtableMappingException("Failed to map data to JSON", e);
        }
    }

    /** @return the endpoint URL for this table */
    private String getTableUrl() {
        return url;
    }

    /** @return the bearer token header for authentication */
    private String getAuthenticationHeader() {
        return "Bearer " + apiKey;
    }

    /**
     * Build the base request used for all Airtable requests
     * @param method the HTTP method
     * @param url the target URL for the request
     * @return a RequestBuilder pre-populated with the method, URL, and Accept and Authorization headers
     */
    private RequestBuilder buildBaseRequest(String method, String url) {
        return new RequestBuilder(method)
                .setUrl(url)
                .setHeader("Accept", MimeType.APPLICATION_JSON)
                .setHeader("Authorization", getAuthenticationHeader());
    }
}
