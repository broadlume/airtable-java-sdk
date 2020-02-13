/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Sybit GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 */
package com.sybit.airtable;

import java.util.List;
import java.util.Objects;
import com.sybit.airtable.exception.AirtableException;
import com.sybit.airtable.v0.Record;
import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Client for synchronously accessing an Airtable table
 * @param <T> the type table row data will be mapped to
 */
public class SyncTable<T> {

    private final AsyncTable<T> asyncTable;

    public SyncTable(AsyncTable<T> asyncTable) {
        this.asyncTable = Objects.requireNonNull(asyncTable, "asyncTable cannot be null");
    }

    /**
     * Select all rows of table.
     * @return List of all items.
     * @throws AirtableException if an error occurs
     */
    public List<Record<T>> select() throws AirtableException {
        return Flowable.fromPublisher(asyncTable.select()).toList().blockingGet();
    }

    /**
     * Select List of data of table with defined Query Parameters.
     * @param query defined query
     * @return list of table items
     * @throws AirtableException if an error occurs
     */
    public List<Record<T>> select(Query query) throws AirtableException {
        return Flowable.fromPublisher(asyncTable.select(query)).toList().blockingGet();
    }

    /**
     * Find record by given id.
     * @param id id of record.
     * @return searched record.
     * @throws AirtableException if an error occurs
     */
    public Record<T> find(String id) throws AirtableException {
        // TODO use maybe instead?
        return Single.fromPublisher(asyncTable.find(id)).blockingGet();
    }

    /**
     * Create Record of given Item.
     * @param item the item to be created
     * @return the created item
     * @throws AirtableException if an error occurs
     */
    public Record<T> create(final T item) throws AirtableException {
        return Single.fromPublisher(asyncTable.create(item)).blockingGet();
    }

    /**
     * Update given <code>item</code> in storage.
     * @param item Item to update.
     * @return updated <code>item</code> returned by airtable.
     * @throws AirtableException if an error occurs
     */
    public Record<T> update(String id, T item) throws AirtableException {
        return Single.fromPublisher(asyncTable.update(id, item)).blockingGet();
    }

    /**
     * Delete Record by given id
     * @param id Id of the row to delete.
     * @return true if success.
     * @throws AirtableException if an error occurs
     */
    public boolean delete(String id) throws AirtableException {
        return Single.fromPublisher(asyncTable.delete(id)).blockingGet();
    }
}
