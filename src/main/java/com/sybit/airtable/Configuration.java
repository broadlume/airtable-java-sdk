/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Sybit GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 */
package com.sybit.airtable;

import javax.annotation.Nullable;
import com.google.auto.value.AutoValue;

/**
 * Configuration settings for Airtable.
 * Used by class <code>Airtable</code> to configure basic settings.
 *
 * @since 0.1
 */
@AutoValue
public abstract class Configuration {

    public static final String ENDPOINT_URL = "https://api.airtable.com/v0";

    public static Builder builder() {
        return new AutoValue_Configuration.Builder()
                .setEndpointUrl(ENDPOINT_URL);
    }

    public abstract String getEndpointUrl();
    public abstract String getApiKey();
    @Nullable
    public abstract String getProxy();
    @Nullable
    public abstract Long getTimeout();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setEndpointUrl(String endpointUrl);
        public abstract Builder setApiKey(String apiKey);
        public abstract Builder setProxy(String proxy);
        public abstract Builder setTimeout(Long timeout);

        public abstract Configuration build();
    }
}
