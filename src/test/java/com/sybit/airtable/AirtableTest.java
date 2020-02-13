package com.sybit.airtable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybit.airtable.internal.http.AirtableHttpClient;
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
