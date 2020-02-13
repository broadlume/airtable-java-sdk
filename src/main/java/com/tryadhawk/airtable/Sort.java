/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Sybit GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 */
package com.tryadhawk.airtable;

import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Sorting of query results.
 *
 * @since 0.1
 */
@Value
@AllArgsConstructor
public class Sort {

    public enum Direction {asc, desc}

    @Nonnull
    private final String field;
    @Nonnull
    private final Direction direction;

    /**
     * Sort ascending given field.
     * @param field name of field
     */
    public Sort(String field) {
        this(field, Direction.asc);
    }
}
