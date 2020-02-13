package com.sybit.airtable.internal.http;

import com.sybit.airtable.Query;
import com.sybit.airtable.Sort;
import org.asynchttpclient.Request;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class QueryRequestBuilderTest {

    private QueryRequestBuilder builder = new QueryRequestBuilder();

    /**
     * Should build the correct URL when all the fields in the query are empty
     */
    @Test
    public void buildRequestForQueryBlankTest() {
        Request arg = builder.buildRequestForQuery(Query.builder().build(), "https://localhost/base/table").build();
        assertEquals("https://localhost/base/table", arg.getUrl());
        assertEquals("GET", arg.getMethod());
    }

    /**
     * Should build the correct URL when every field in the query is set
     */
    @Test
    public void buildRequestForQueryFullTest() {
        Query query = Query.builder()
                .field("field1").field("field2")
                .sort(new Sort("field1")).sort(new Sort("field2", Sort.Direction.desc))
                .view("test-view")
                .maxRecords(200)
                .offset("a")
                .pageSize(5)
                .filterByFormula("abc")
                .build();
        String url = "https://localhost/base/table?fields%5B%5D=field1&fields%5B%5D=field2&maxRecords=200&view=test-view&" +
                "filterByFormula=abc&pageSize=5&sort%5B0%5D%5Bfield%5D=field1&sort%5B0%5D%5Bdirection%5D=asc&" +
                "sort%5B1%5D%5Bfield%5D=field2&sort%5B1%5D%5Bdirection%5D=desc&offset=a";

        Request arg = builder.buildRequestForQuery(query, "https://localhost/base/table").build();
        assertEquals(url, arg.getUrl());
        assertEquals("GET", arg.getMethod());
    }
}
