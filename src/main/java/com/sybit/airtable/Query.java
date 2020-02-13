/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Sybit GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 */
package com.sybit.airtable;

import java.util.List;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

/**
 * Interface for specific queries.
 *
 * @since 0.1
 */
@Value
@Builder(toBuilder = true)
public class Query {

    /** Fields to be loaded */
    @Singular
    private final List<String> fields;
    /** the number of records per page */
    @Nullable
    private final Integer pageSize;
    /** number of max rows to load. */
    @Nullable
    private final Integer maxRecords;
    /** Name of view to load. */
    @Nullable
    private final String view;
    /** sorting of result set. */
    @Singular
    private final List<Sort> sorts;
    /** Define a filter formula. see https://support.airtable.com/hc/en-us/articles/203255215-Formula-Field-Reference */
    @Nullable
    private final String filterByFormula;
    /** Offset to get more than 100 records. */
    @Nullable
    private final String offset;
}
