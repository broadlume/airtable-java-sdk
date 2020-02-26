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
import com.tryadhawk.airtable.v0.Error;
import com.tryadhawk.airtable.v0.ErrorWrapper;
import io.reactivex.rxjava3.core.Single;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles exceptions and failure status codes returned from Airtable
 */
public class HttpResponseExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(HttpResponseExceptionHandler.class);

    private final ObjectMapper objectMapper;

    public HttpResponseExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper cannot be null");
    }

    /**
     * Handle an error that occurs when making a request to Airtable
     * @param t the error
     * @return a Single containing the error mapped to an {@link AirtableServerException}
     */
    public Single<Response> handleError(Throwable t) {
        return Single.error(new AirtableServerException(500, "Internal server error", null, t));
    }

    /**
     * Check an Airtable response for an error
     * @param response the response
     * @return a Single containing the response if no error or containing an {@link AirtableServerException} with
     * details about the error if the response contains an error
     */
    public Single<Response> checkResponse(Response response) {
        return isErrorResponse(response) ? Single.error(buildException(response)) : Single.just(response);
    }

    /**
     * Check if a response is an error. Any status code other than 200 is treated as an error
     * @param response the response
     * @return if it is an error
     */
    private boolean isErrorResponse(Response response) {
        return response.getStatusCode() != 200;
    }

    /**
     * Extract details about an error from a response and build an AirtableServerException with the details
     * @param response the response
     * @return the AirtableServerException
     */
    private AirtableServerException buildException(Response response) {
        int status = response.getStatusCode();
        String statusText = response.getStatusText();
        Error err = extractError(response);
        return new AirtableServerException(status, statusText, err);
    }

    /**
     * Extract an {@link Error} from an Airtable response or use default Error containing the response body if unable to
     * parse the response
     * @param response the response
     * @return the extracted Error
     */
    private Error extractError(Response response) {
        Error err;
        String body = response.getResponseBody();
        try {
            ErrorWrapper wrapper = objectMapper.readValue(body, ErrorWrapper.class);
            err = wrapper.getError();
        } catch (Exception e) {
            logger.warn("Failed to parse response body to error, body: {}", body, e);
            err = new Error("UNDEFINED_ERROR", body);
        }

        return err;
    }
}
