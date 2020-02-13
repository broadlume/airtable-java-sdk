package com.tryadhawk.airtable.exception;

/**
 * Airtable exception indicating something went wrong when serializing or deserializing a request or response body
 */
public class AirtableMappingException extends AirtableException {

    public AirtableMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
