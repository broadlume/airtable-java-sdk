package com.sybit.airtable.internal.http;

import java.util.Objects;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybit.airtable.exception.AirtableServerException;
import com.sybit.airtable.internal.reactive.ReactiveUtils;
import com.sybit.airtable.internal.reactive.RetryWithDelay;
import io.reactivex.Single;
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
