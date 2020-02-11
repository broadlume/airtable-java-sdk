/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Sybit GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 */
package com.sybit.airtable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import com.sybit.airtable.vo.Records;
import lombok.Value;

/**
 * Representation Class of Airtable Base.
 * @since 0.1
 */
public class Base {

    private final Map<TableKey, Table<?>> tableMap = new ConcurrentHashMap<>();
    private final String baseName;
    private final Airtable parent;

    /**
     * Create Airtable Base with given baseName ID.
     * @param name base ID could be found at https://airtable.com if you select your current baseName.
     * @param airtable parent airtable object
     */
    public Base(String name, Airtable airtable) {
        this.baseName = Objects.requireNonNull(name, "name cannot be null");
        this.parent = Objects.requireNonNull(airtable, "parent cannot be null");
    }

    /**
     * Get Airtable object as parent.
     * @return the parent
     */
    public Airtable getAirtable() {
        return parent;
    }

    /**
     * Get baseName id of baseName.
     * @return baseName id
     */
    public String getName() {
        return baseName;
    }

    /**
     * Get Table object of given table.
     * @param name Name of required table.
     * @return Object to access table.
     */
    public Table<Records> table(String name) {
        return table(name, Records.class);
    }

    /**
     * Get Table object of given table.
     * @param name Name of required table.
     * @param clazz Class representing row of resultsets
     * @return Object to access table.
     */
    @SuppressWarnings("unchecked")
    public <T> Table<T> table(String name, Class<T> clazz) {
        TableKey key = new TableKey(name, clazz);
        return  (Table<T>) tableMap.computeIfAbsent(key, k -> new Table<>(name, clazz, this));
    }

    @Value
    private static class TableKey {

        @Nonnull
        private final String name;
        @Nonnull
        private final Class<?> clazz;
    }
}
