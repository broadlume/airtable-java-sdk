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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tryadhawk.airtable.internal.http.AirtableHttpClient;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class AirtableTest {

    private AirtableHttpClient httpClient = mock(AirtableHttpClient.class);
    private ObjectMapper objectMapper = mock(ObjectMapper.class);

    /**
     * Should create a default ObjectMapper and AirtableHttpClient if none are set in the builder
     */
    @Test
    public void builderDefaultsTest() {
        Configuration config = Configuration.builder().apiKey("abc123").endpointUrl("https://localhost").build();
        Airtable airtable = Airtable.builder().config(config).build();

        assertNotNull(airtable);
        assertNotNull(airtable.buildAsyncTable("base", "table", String.class));
        assertNotNull(airtable.buildSyncTable("base", "table", String.class));
    }

    /**
     * Should create an instance and allow async and sync clients to be created
     */
    @Test
    public void builderTest() {
        Configuration config = Configuration.builder().apiKey("abc123").endpointUrl("https://localhost").build();
        Airtable airtable = Airtable.builder().airtableHttpClient(httpClient).objectMapper(objectMapper)
                .config(config).build();

        assertNotNull(airtable);
        assertNotNull(airtable.buildAsyncTable("base", "table", String.class));
        assertNotNull(airtable.buildSyncTable("base", "table", String.class));
    }
}
