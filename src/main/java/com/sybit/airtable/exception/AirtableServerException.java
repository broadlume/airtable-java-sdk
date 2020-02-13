package com.sybit.airtable.exception;

import javax.annotation.Nullable;
import com.sybit.airtable.vo.Error;

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

    /** the HTTP status code */
    public int getStatusCode() {
        return statusCode;
    }

    /** the HTTP status text */
    public String getStatusText() {
        return statusText;
    }

    /** the error returned from Airtable */
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
