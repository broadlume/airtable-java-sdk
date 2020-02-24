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
