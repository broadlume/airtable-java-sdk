/*
 * The MIT License (MIT)
 * Copyright (c) 2017 Sybit GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 */
package com.tryadhawk.airtable;

import java.util.List;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Query {

    /* Fields to be retrieved, if not set all fields will be retrieved */
    @Singular
    private final List<String> fields;
    /* Number of records per page, default is 100, max is 100 */
    @Nullable
    private final Integer pageSize;
    /* Max number of rows to retrieve */
    @Nullable
    private final Integer maxRecords;
    /* Name or ID of a view in the table. If set, only records in that view will be returned */
    @Nullable
    private final String view;
    /* Sorting of result set */
    @Singular
    private final List<Sort> sorts;
    /* Define a filter formula. see https://support.airtable.com/hc/en-us/articles/203255215-Formula-Field-Reference */
    @Nullable
    private final String filterByFormula;
    /* Offset to start at for pagination */
    @Nullable
    private final String offset;
}
