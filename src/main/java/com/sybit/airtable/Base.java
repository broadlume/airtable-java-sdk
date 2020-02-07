/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Sybit GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 */
package com.sybit.airtable;

import com.sybit.airtable.vo.Records;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Representation Class of Airtable Base.
 *
 * @since 0.1
 */
public class Base {

    private static final Logger LOG = LoggerFactory.getLogger( Base.class );

    private final Map<TableKey, Table<?>> tableMap = new HashMap<>();

    private final String baseName;

    private final Airtable parent;


    /**
     * Create Airtable Base with given baseName ID.
     *
     * @param name base ID could be found at https://airtable.com if you select your current baseName.
     * @param airtable parent airtable object
     */
    public Base(String name, Airtable airtable) {
        this.baseName = Objects.requireNonNull(name);
        this.parent = Objects.requireNonNull(airtable);
    }
    
    /**
     * Get Airtable object as parent.
     * @return
     */
    public Airtable airtable() {
        return parent;
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
        Table<T> table = (Table<T>) tableMap.get(key);
        if(table == null) {
            LOG.debug("Create new instance for table [" + name + "]");
            table = new Table<>(name, clazz);
            table.setParent(this);
            tableMap.put(key, table);
        }

        return table;
    }

    /**
     * Get baseName id of baseName.
     * @return baseName id
     */
    public String name() {
        return baseName;
    }

    private static class TableKey {
        private final String name;
        private final Class<?> clazz;

        public TableKey(String name, Class<?> clazz) {
            this.name = Objects.requireNonNull(name);
            this.clazz = Objects.requireNonNull(clazz);
        }

        public String getName() {
            return name;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        @Override
        public String toString() {
            return "TableKey{" +
                    "name='" + name + '\'' +
                    ", clazz=" + clazz +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TableKey tableKey = (TableKey) o;
            return Objects.equals(name, tableKey.name) &&
                    Objects.equals(clazz, tableKey.clazz);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, clazz);
        }
    }
}
