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
package com.tryadhawk.airtable;

import java.util.List;
import java.util.Objects;
import com.tryadhawk.airtable.exception.AirtableException;
import com.tryadhawk.airtable.v0.Record;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

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
     * Retrieve all rows from the table
     * @return all rows in the table
     * @throws AirtableException if an error occurs
     */
    public List<Record<T>> select() {
        return Flowable.fromPublisher(asyncTable.select()).toList().blockingGet();
    }

    /**
     * Retrieve rows from the table matching a {@link Query}
     * @param query the query
     * @return all matching rows in the table
     * @throws AirtableException if an error occurs
     */
    public List<Record<T>> select(Query query) {
        return Flowable.fromPublisher(asyncTable.select(query)).toList().blockingGet();
    }

    /**
     * Find a row in the table by its row ID
     * @param id the row ID
     * @return the matching row
     * @throws AirtableException if an error occurs
     */
    public Record<T> find(String id) {
        // TODO use maybe instead?
        return Single.fromPublisher(asyncTable.find(id)).blockingGet();
    }

    /**
     * Create a new row in the table
     * @param item the data for the row
     * @return the created row
     * @throws AirtableException if an error occurs
     */
    public Record<T> create(final T item) {
        return Single.fromPublisher(asyncTable.create(item)).blockingGet();
    }

    /**
     * Update an existing row in the table. Only non-null fields in {@code item} will be updated, all other fields will
     * be left as they were
     * @param id the row's row ID
     * @param item the data to update
     * @return the updated row
     * @throws AirtableException if an error occurs
     */
    public Record<T> update(String id, T item) {
        return Single.fromPublisher(asyncTable.update(id, item)).blockingGet();
    }

    /**
     * Delete a row by its row ID
     * @param id the row ID
     * @return whether the row was deleted
     * @throws AirtableException if an error occurs
     */
    public boolean delete(String id) {
        return Single.fromPublisher(asyncTable.delete(id)).blockingGet();
    }
}
