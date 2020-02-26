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
