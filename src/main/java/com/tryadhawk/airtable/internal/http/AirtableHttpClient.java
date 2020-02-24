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

package com.tryadhawk.airtable.internal.http;

import java.util.Objects;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tryadhawk.airtable.exception.AirtableServerException;
import com.tryadhawk.airtable.internal.reactive.ReactiveUtils;
import com.tryadhawk.airtable.internal.reactive.RetryWithDelay;
import io.reactivex.rxjava3.core.Single;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;

/**
 * Wrapper around AsyncHttpClient that transforms exceptions and failure response codes to
 * {@link AirtableServerException AirtableServerExceptions} and automatically retries up to 5 times after a delay for
 * 429 response status codes
 */
public class AirtableHttpClient {

    private final AsyncHttpClient httpClient;
    private final HttpResponseExceptionHandler exceptionHandler;
    private final int waitMin;
    private final int waitMax;

    public AirtableHttpClient(AsyncHttpClient httpClient, ObjectMapper objectMapper) {
        this(httpClient, objectMapper, new HttpResponseExceptionHandler(objectMapper));
    }

    public AirtableHttpClient(AsyncHttpClient httpClient, ObjectMapper objectMapper,
                              HttpResponseExceptionHandler exceptionHandler) {
        this(httpClient, objectMapper, exceptionHandler, 30, 36);
    }

    AirtableHttpClient(AsyncHttpClient httpClient, ObjectMapper objectMapper,
                       HttpResponseExceptionHandler exceptionHandler, int waitMin, int waitMax) {
        if (waitMin < 1 || waitMax < 1)
            throw new IllegalArgumentException("waitMin and waitMax must be greater than 0");
        if (waitMin >= waitMax)
            throw new IllegalArgumentException("waitMin must be less than or equal to waitMax");
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient cannot be null");
        Objects.requireNonNull(objectMapper, "objectMapper cannot be null");
        this.exceptionHandler = Objects.requireNonNull(exceptionHandler, "exceptionHandler cannot be null");
        this.waitMin = waitMin;
        this.waitMax = waitMax;
    }

    /**
     * Execute a request
     * @param request the request to execute
     * @return a Single containing the Response or an AirtableServerException if an error occurs
     */
    public Single<Response> execute(Request request) {
        return ReactiveUtils.fromFuture(() -> httpClient.executeRequest(request))
                .onErrorResumeNext(e -> exceptionHandler.handleError(e))
                .flatMap(exceptionHandler::checkResponse)
                .retryWhen(RetryWithDelay.builder()
                        .waitMin(waitMin)
                        .waitMax(waitMax)
                        .predicate(e -> e instanceof AirtableServerException && ((AirtableServerException) e).getStatusCode() == 429)
                        .build());
    }
}
