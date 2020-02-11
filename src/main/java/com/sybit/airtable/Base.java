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
    private final String name;
    private final String url;
    private final String apiKey;

    /**
     * Create Airtable Base with given baseName ID.
     * @param name base ID could be found at https://airtable.com if you select your current baseName.
     * @param airtableUrl the URL to access airtable
     * @param apiKey the API key
     */
    Base(String name, String airtableUrl, String apiKey) {
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.apiKey = Objects.requireNonNull(apiKey, "apiKey cannot be null");
        url = Objects.requireNonNull(airtableUrl, "airtableUrl cannot be null") + "/" + name;
    }

    /**
     * Get baseName id of baseName.
     * @return baseName id
     */
    public String getName() {
        return name;
    }

    /**
     * Get Table object of given table.
     * @param name Name of required table.
     * @return Object to access table.
     */
    public Table<Records> buildTable(String name) {
        return buildTable(name, Records.class);
    }

    /**
     * Get Table object of given table.
     * @param name Name of required table.
     * @param clazz Class representing row of resultsets
     * @return Object to access table.
     */
    @SuppressWarnings("unchecked")
    public <T> Table<T> buildTable(String name, Class<T> clazz) {
        TableKey key = new TableKey(name, clazz);
        return  (Table<T>) tableMap.computeIfAbsent(key, k -> createTable(name, clazz));
    }

    private <T> Table<T> createTable(String name, Class<T> clazz) {
        return new Table<>(name, url, apiKey, clazz);
    }

    @Value
    private static class TableKey {

        @Nonnull
        private final String name;
        @Nonnull
        private final Class<?> clazz;
    }
}
