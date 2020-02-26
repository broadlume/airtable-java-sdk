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

package com.tryadhawk.airtable.exception;

import javax.annotation.Nullable;
import com.tryadhawk.airtable.v0.Error;

/**
 * Airtable exception for an error returned from an Airtable server
 */
public class AirtableServerException extends AirtableException {

    private final int statusCode;
    private final String statusText;
    private final Error error;

    public AirtableServerException(int statusCode) {
        this(statusCode, null, null, null);
    }

    public AirtableServerException(int statusCode, String statusText, Error error) {
        this(statusCode, statusText, error, null);
    }

    public AirtableServerException(int statusCode, String statusText, Error error, Throwable cause) {
        super(buildExceptionMessage(statusCode, statusText, error), cause);
        this.statusCode = statusCode;
        this.statusText = statusText == null ? "" : statusText;
        this.error = error;
    }

    /** @return the HTTP status code */
    public int getStatusCode() {
        return statusCode;
    }

    /** @return the HTTP status text */
    public String getStatusText() {
        return statusText;
    }

    /** @return the error returned from Airtable */
    @Nullable
    public Error getError() {
        return error;
    }

    private static String buildExceptionMessage(int statusCode, String statusText, Error error) {
        StringBuilder sb = new StringBuilder("Status code: ").append(statusCode);
        if (statusText != null)
            sb.append(", status text: ").append(statusText);
        if (error != null)
            sb.append(", error: ").append(error);
        return sb.toString();
    }
}
