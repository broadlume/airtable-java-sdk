/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Sybit GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 */
package com.sybit.airtable;

import java.util.Objects;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybit.airtable.internal.http.AirtableHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.proxy.ProxyServer;

public class Airtable {

    private final Configuration config;
    private final AirtableHttpClient airtableHttpClient;
    private final ObjectMapper objectMapper;

    public static AirtableBuilder builder() {
        return new AirtableBuilder();
    }

    private Airtable(Configuration config, AirtableHttpClient airtableHttpClient, ObjectMapper objectMapper) {
        this.config = Objects.requireNonNull(config, "config cannot be null");
        this.airtableHttpClient = Objects.requireNonNull(airtableHttpClient, "airtableHttpClient cannot be null");
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper cannot be null");
    }

    /**
     * Build a new asynchronous client for accessing an Airtable table
     * @param baseName the name of the base containing the table
     * @param tableId the ID of the table (not the same as the table name)
     * @param clazz the class the table row data should be mapped to
     * @param <T> the type for {@code clazz}
     * @return the async table client
     */
    public <T> AsyncTable<T> buildAsyncTable(String baseName, String tableId, Class<T> clazz) {
        String tableUrl = config.getEndpointUrl() + "/" + baseName + "/" + tableId;
        return new AsyncTable<>(tableUrl, config.getApiKey(), clazz, airtableHttpClient, objectMapper);
    }

    /**
     * Build a new synchronous client for accessing an Airtable table
     * @param baseName the name of the base containing the table
     * @param tableId the ID of the table (not the same as the table name)
     * @param clazz the class the table row data should be mapped to
     * @param <T> the type for {@code clazz}
     * @return the sync table client
     */
    public <T> SyncTable<T> buildSyncTable(String baseName, String tableId, Class<T> clazz) {
        return new SyncTable<>(buildAsyncTable(baseName, tableId, clazz));
    }

    public static class AirtableBuilder {

        private Configuration config;
        private AirtableHttpClient airtableHttpClient;
        private ObjectMapper objectMapper;

        /** @param config the configuration for accessing Airtable, must be set before calling {@link #build()} */
        public AirtableBuilder config(Configuration config) {
            this.config = config;
            return this;
        }

        /** @param airtableHttpClient the client to use to access Airtable, will use a default if not set */
        public AirtableBuilder airtableHttpClient(AirtableHttpClient airtableHttpClient) {
            this.airtableHttpClient = airtableHttpClient;
            return this;
        }

        /** @param objectMapper the mapper to use to serialize and deserialize JSON, will use a default if not set */
        public AirtableBuilder objectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        public Airtable build() {
            Objects.requireNonNull(config, "config cannot be null");
            ObjectMapper mapper = objectMapper;
            AirtableHttpClient client = airtableHttpClient;
            if (mapper == null)
                mapper = new ObjectMapper();
            if (client == null)
                client = buildHttpClient(config, objectMapper);
            return new Airtable(config, client, mapper);
        }

        private AirtableHttpClient buildHttpClient(Configuration config, ObjectMapper objectMapper) {
            DefaultAsyncHttpClientConfig.Builder builder = new DefaultAsyncHttpClientConfig.Builder();
            if (config.getTimeout() != null) {
                builder.setRequestTimeout(config.getTimeout());
                builder.setReadTimeout(config.getTimeout());
            }
            if (config.getProxy() != null)
                builder.setProxyServer(new ProxyServer.Builder(config.getProxy().getHost(), config.getProxy().getPort()).build());

            return new AirtableHttpClient(Dsl.asyncHttpClient(builder), objectMapper);
        }
    }
}
