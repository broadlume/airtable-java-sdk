/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Sybit GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 */
package com.sybit.airtable;

import com.sybit.airtable.vo.Records;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class BaseTest {

    private Airtable airtable = new Airtable(Configuration.builder().apiKey("123").endpointUrl("http://localhost")
            .build());
    private Base base = airtable.buildBase("base");

    @Test
    public void getNameTest() {
        assertEquals("base", base.getName());
    }

    @Test
    public void buildRecordsTableTest() {
        Table<Records> table = base.buildTable("test-table");
        assertNotNull(table);
        assertSame(table, base.buildTable("test-table"));
        assertNotSame(table, base.buildTable("test-table-2"));
    }

    @Test
    public void buildTableTest() {
        Table<String> table = base.buildTable("test-table", String.class);
        assertNotNull(table);
        assertSame(table, base.buildTable("test-table", String.class));
        assertNotSame(table, base.buildTable("test-table", Integer.class));
        assertNotSame(table, base.buildTable("test-table-2", String.class));
    }
}
