/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Sybit GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 */
package com.sybit.airtable.v0;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Record<T> {

    private String id;
    private T fields;
    private String createdTime;

    public static <T> Record<T> of(T fields) {
        return new Record<>(null, fields, null);
    }

    public static <T> Record<T> of(String id, T fields, String createdTime) {
        return new Record<>(id, fields, createdTime);
    }
}
