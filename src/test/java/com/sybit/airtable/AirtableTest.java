package com.sybit.airtable;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AirtableTest {

    @Test
    public void baseTest() {
        Configuration configuration = Configuration.builder()
                .apiKey("12345")
                .endpointUrl("http://test.com")
                .build();
        Airtable airtable = new Airtable(configuration);
        assertNotNull(airtable.base("test-base"));
    }

    @Test
    public void getConfigurationTest() {
        Configuration configuration = Configuration.builder()
                .apiKey("12345")
                .endpointUrl("http://test.com")
                .build();
        Airtable airtable = new Airtable(configuration);
        assertEquals(configuration, airtable.getConfig());
    }
}
