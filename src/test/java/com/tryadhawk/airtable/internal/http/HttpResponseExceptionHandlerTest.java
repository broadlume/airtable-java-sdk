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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tryadhawk.airtable.exception.AirtableServerException;
import com.tryadhawk.airtable.v0.Error;
import com.tryadhawk.airtable.v0.ErrorWrapper;
import org.asynchttpclient.Response;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpResponseExceptionHandlerTest {

    private ObjectMapper objectMapper = new ObjectMapper();
    private HttpResponseExceptionHandler handler = new HttpResponseExceptionHandler(objectMapper);

    /**
     * Should wrap any exception in an AirtableServerException and treat it as a 500 status
     */
    @Test
    public void handleErrorTest() throws InterruptedException {
        handler.handleError(new RuntimeException("test"))
                .test().await()
                .assertError(e -> e instanceof AirtableServerException &&
                        500 == ((AirtableServerException) e).getStatusCode() &&
                        "Internal server error".equals(((AirtableServerException) e).getStatusText()) &&
                        null == ((AirtableServerException) e).getError());
    }

    /**
     * Should return the response if the response does not contain an error
     */
    @Test
    public void checkResponseSuccessTest() throws InterruptedException {
        Response response = mock(Response.class);
        when(response.getStatusCode()).thenReturn(200);

        handler.checkResponse(response).test().await().assertResult(response);
    }

    /**
     * Should parse an error from the response body and create an AirtableServerException with the status code,
     * status text, and error from the response
     */
    @Test
    public void checkResponseErrorTest() throws JsonProcessingException, InterruptedException {
        Response response = mock(Response.class);
        when(response.getStatusCode()).thenReturn(500);
        when(response.getStatusText()).thenReturn("Internal server error");
        Error body = new Error("test-type", "test-message");
        when(response.getResponseBody()).thenReturn(objectMapper.writeValueAsString(
                new ErrorWrapper(body)));

        handler.checkResponse(response).test().await().assertError(e ->
                e instanceof AirtableServerException &&
                        ((AirtableServerException) e).getStatusCode() == 500 &&
                        "Internal server error".equals(((AirtableServerException) e).getStatusText()) &&
                        body.equals(((AirtableServerException) e).getError()));
    }

    /**
     * Should create a default error with the response body text if unable to parse the response body
     */
    @Test
    public void checkResponseErrorBodyTest() throws InterruptedException {
        Response response = mock(Response.class);
        when(response.getStatusCode()).thenReturn(500);
        when(response.getStatusText()).thenReturn("Internal server error");
        when(response.getResponseBody()).thenReturn("whatever");

        handler.checkResponse(response).test().await().assertError(e ->
                e instanceof AirtableServerException &&
                        ((AirtableServerException) e).getStatusCode() == 500 &&
                        "Internal server error".equals(((AirtableServerException) e).getStatusText()) &&
                        new Error("UNDEFINED_ERROR", "whatever").equals(((AirtableServerException) e).getError()));
    }
}
