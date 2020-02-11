/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Sybit GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 */
package com.sybit.airtable.exception;

/**
 * General Exception of API.
 *
 * @since 0.1
 */
public class AirtableException extends Exception {

    /**
     * Constructs a new exception with the specified detail message.
     * @param message Detail message. 
     */
    public AirtableException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause.
     * @param cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).
     */
    public AirtableException(Throwable cause) {
        super(cause);
    }

    /**
     * Default Exception similar to AirtableError of JavaScript Library.
     * @param error Error code.
     * @param message Detail message. 
     * @param status HTTP Status Code.
     */
    public AirtableException(String error, String message, Integer status) {
        super(message + " (" + error + ")" + ((status != null) ? " [Http code " + status + "]": ""));
    }
}
