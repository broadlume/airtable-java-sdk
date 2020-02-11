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

/**
 * Representation Class of Airtable. It is the entry class to access Airtable
 * data.
 * @since 0.1
 */
public class Airtable {

    private final Configuration config;

    public Airtable(Configuration config) {
        this(config, new GsonObjectMapper());
    }

    public Airtable(Configuration config, ObjectMapper objectMapper) {
        this.config = Objects.requireNonNull(config, "configuration cannot be null");

        if (config.getTimeout() != null)
            Unirest.setTimeouts(config.getTimeout(), config.getTimeout());
        if (config.getProxy() != null)
            Unirest.setProxy(HttpHost.create(config.getProxy()));

        Unirest.setObjectMapper(objectMapper);

        // Add specific Converter for Date
        DateTimeConverter dtConverter = new DateConverter();
        dtConverter.setPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        ConvertUtils.register(dtConverter, Date.class);

        ListConverter listConverter = new ListConverter();
        listConverter.setListClass(Attachment.class);
        ConvertUtils.register(listConverter, List.class);

        MapConverter mapConverter = new MapConverter();
        mapConverter.setMapClass(Thumbnail.class);
        ConvertUtils.register(mapConverter, Map.class);
    }

    public Configuration getConfig() {
        return config;
    }

    /**
     * Builder method to create base of given base id.
     * @param base the base id.
     * @return the base
     */
    public Base buildBase(String base) {
        return new Base(base, getConfig().getEndpointUrl() + "/" + base, getConfig().getApiKey());
    }
}
