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

import java.util.Arrays;
import com.tryadhawk.airtable.test.DummyRow;
import com.tryadhawk.airtable.v0.Record;
import com.tryadhawk.airtable.v0.RecordPage;
import io.reactivex.rxjava3.core.Flowable;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class SyncTableTest {

    private AsyncTable<DummyRow> asyncTable = mock(AsyncTable.class);
    private SyncTable<DummyRow> table = new SyncTable<>(asyncTable);

    /**
     * Should wait for the async client to complete and return the same response
     */
    @Test
    public void selectTest() {
        Record<DummyRow> record1 = Record.of("123", new DummyRow("1", "name", 12), "today");
        Record<DummyRow> record2 = Record.of("456", new DummyRow("2", "name2", 13), "tomorrow");
        when(asyncTable.select()).thenReturn(Flowable.just(record1, record2));

        Assertions.assertThat(table.select())
                .hasSize(2)
                .contains(record1)
                .contains(record2);
    }

    @Test
    public void selectQueryTest() {
        Query query = Query.builder().maxRecords(500).build();
        Record<DummyRow> record1 = Record.of("321", new DummyRow("1", "name", 12), "today");
        Record<DummyRow> record2 = Record.of("654", new DummyRow("2", "name2", 13), "tomorrow");
        when(asyncTable.select(query)).thenReturn(Flowable.just(record1, record2));

        Assertions.assertThat(table.select(query))
                .hasSize(2)
                .contains(record1)
                .contains(record2);
    }

    @Test
    public void selectPageTest() {
        Record<DummyRow> record1 = Record.of("123", new DummyRow("1", "name", 12), "today");
        Record<DummyRow> record2 = Record.of("456", new DummyRow("2", "name2", 13), "tomorrow");
        RecordPage<DummyRow> page = new RecordPage<>(Arrays.asList(record1, record2), "abc123");
        when(asyncTable.selectPage()).thenReturn(Flowable.just(page));

        assertEquals(page, table.selectPage());
    }

    @Test
    public void selectPageQueryTest() {
        Query query = Query.builder().maxRecords(500).build();
        Record<DummyRow> record1 = Record.of("123", new DummyRow("1", "name", 12), "today");
        Record<DummyRow> record2 = Record.of("456", new DummyRow("2", "name2", 13), "tomorrow");
        RecordPage<DummyRow> page = new RecordPage<>(Arrays.asList(record1, record2), "abc123");
        when(asyncTable.selectPage(query)).thenReturn(Flowable.just(page));

        assertEquals(page, table.selectPage(query));
    }

    @Test
    public void findTest() {
        Record<DummyRow> record = Record.of("321", new DummyRow("1", "name", 12), "today");
        when(asyncTable.find("1")).thenReturn(Flowable.just(record));

        Assertions.assertThat(table.find("1")).isEqualTo(record);
    }

    @Test
    public void createTest() {
        DummyRow row = new DummyRow("1", "name", 3);
        Record<DummyRow> response = Record.of("abc", row, "time");
        when(asyncTable.create(row)).thenReturn(Flowable.just(response));

        Assertions.assertThat(table.create(row)).isEqualTo(response);
    }

    @Test
    public void updateTest() {
        DummyRow row = new DummyRow("1", "name", 3);
        Record<DummyRow> response = Record.of("abc", row, "time");
        when(asyncTable.update("abc", row)).thenReturn(Flowable.just(response));

        Assertions.assertThat(table.update("abc", row)).isEqualTo(response);
    }

    @Test
    public void deleteTest() {
        when(asyncTable.delete("def")).thenReturn(Flowable.just(true));

        assertThat(table.delete("def")).isEqualTo(true);
    }
}
