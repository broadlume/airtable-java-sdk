/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Sybit GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 */
package com.tryadhawk.airtable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Value;

/**
 * Configuration settings for Airtable.
 * Used by class <code>Airtable</code> to configure basic settings.
 *
 * @since 0.1
 */
@Value
@Builder(toBuilder = true)
public class Configuration {

    public static final String ENDPOINT_URL = "https://api.airtable.com/v0";

    @Builder.Default
    @Nonnull
    private final String endpointUrl = ENDPOINT_URL;
    @Nonnull
    private final String apiKey;
    @Nullable
    private final ProxyConfiguration proxy;
    @Nullable
    private final Integer timeout;

    @Value
    @Builder(toBuilder = true)
    public static class ProxyConfiguration {

        @Nonnull
        private final String host;
        @Nonnull
        private final Integer port;
    }
}
