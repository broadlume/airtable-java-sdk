/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Sybit GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 */
package com.sybit.airtable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.sybit.airtable.converter.ListConverter;
import com.sybit.airtable.converter.MapConverter;
import com.sybit.airtable.vo.Attachment;
import com.sybit.airtable.vo.Thumbnail;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.DateTimeConverter;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Representation Class of Airtable. It is the entry class to access Airtable
 * data.
 * @since 0.1
 */
public class Airtable {

    private static final Logger LOG = LoggerFactory.getLogger(Airtable.class);

    private final Configuration config;

    public Airtable(Configuration config) {
        this(config, new GsonObjectMapper());
    }

    public Airtable(Configuration config, ObjectMapper objectMapper) {
        this.config = Objects.requireNonNull(config, "configuration cannot be null");

        if (config.getTimeout() != null) {
            LOG.info("Set connection timeout to: " + config.getTimeout() + "ms.");
            Unirest.setTimeouts(config.getTimeout(), config.getTimeout());
        }

        configureProxy();

        // Only one time
        Unirest.setObjectMapper(objectMapper);

        // Add specific Converter for Date
        DateTimeConverter dtConverter = new DateConverter();
        dtConverter.setPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        ConvertUtils.register(dtConverter, Date.class);

        ListConverter lConverter = new ListConverter();
        lConverter.setListClass(Attachment.class);
        ConvertUtils.register(lConverter, List.class);

        MapConverter thConverter = new MapConverter();
        thConverter.setMapClass(Thumbnail.class);
        ConvertUtils.register(thConverter, Map.class);
    }

    /**
     * Set Proxy environment from Configuration.
     *
     * Proxy will be ignored for endpointUrls containing <code>localhost</code>
     * or <code>127.0.0.1</code>.
     */
    private void configureProxy() {
        String endpointUrl = config.getEndpointUrl();
        if ((endpointUrl.contains("127.0.0.1") || endpointUrl.contains("localhost"))) {
            LOG.info("Use Proxy: ignored for 'localhost' and '127.0.0.1'");
            Unirest.setProxy(null);
        } else if (config.getProxy() != null) {
            Unirest.setProxy(HttpHost.create(config.getProxy()));
        }
    }

    /**
     * Builder method to create base of given base id.
     * @param base the base id.
     * @return the base
     */
    public Base base(String base) {
        return new Base(base, this);
    }

    public Configuration getConfig() {
        return config;
    }
}
