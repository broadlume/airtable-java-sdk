/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Sybit GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 */
package com.sybit.airtable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default mapper based on GSON.
 *
 * @since 0.1
 * @author fzr
 */
class GsonObjectMapper implements ObjectMapper {

    private static final Logger LOG = LoggerFactory.getLogger( GsonObjectMapper.class.getName() );
    private final Gson gson;
                
    public GsonObjectMapper() {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
    }

    @Override
    public <T> T readValue(String value, Class<T> valueType) {
        LOG.debug("readValue: {}", value);
        return gson.fromJson(value, valueType);
    }

    @Override
    public String writeValue(Object value) {
        LOG.debug("writeValue: {}", value);
        return gson.toJson(value);
    }
}
