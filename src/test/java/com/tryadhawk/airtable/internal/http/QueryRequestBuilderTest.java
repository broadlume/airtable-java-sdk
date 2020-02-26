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

package com.tryadhawk.airtable.internal.http;

import com.tryadhawk.airtable.Query;
import com.tryadhawk.airtable.Sort;
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
