package com.tryadhawk.airtable.exception;

/**
 * Base exception for exceptions thrown from Airtable
 */
public abstract class AirtableException extends RuntimeException {

    public AirtableException() {
    }

    public AirtableException(String message) {
        super(message);
    }

    public AirtableException(String message, Throwable cause) {
        super(message, cause);
    }

    public AirtableException(Throwable cause) {
        super(cause);
    }
}
