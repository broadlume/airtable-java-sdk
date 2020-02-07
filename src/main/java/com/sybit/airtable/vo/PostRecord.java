/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Sybit GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 */
package com.sybit.airtable.vo;

/**
 *
 * @author fzr
 * @param <T>
 */
public class PostRecord<T> {
    
    private final T fields;

    public PostRecord(T fields) {
        this.fields = fields;
    }

    /**
     * @return the fields
     */
    public T getFields() {
        return fields;
    }
}
