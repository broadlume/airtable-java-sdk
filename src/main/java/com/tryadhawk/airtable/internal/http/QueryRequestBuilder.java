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

package com.tryadhawk.airtable.internal.http;

import java.util.List;
import com.tryadhawk.airtable.Query;
import com.tryadhawk.airtable.Sort;
import org.asynchttpclient.RequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builds a {@link RequestBuilder} for a {@link Query}
 */
public class QueryRequestBuilder {

    private static final Logger logger = LoggerFactory.getLogger(QueryRequestBuilder.class);

    /**
     * Create a {@link RequestBuilder} initialized with the URL, method, and query params for a {@link Query}
     * @param query the Query
     * @param tableUrl the table endpoint URL
     * @return the RequestBuilder
     */
    public RequestBuilder buildRequestForQuery(Query query, String tableUrl) {
        RequestBuilder request = new RequestBuilder("GET")
                .setUrl(tableUrl);

        if (query.getFields() != null) {
            for (String field : query.getFields())
                request.addQueryParam("fields[]", field);
        }
        if (query.getMaxRecords() != null)
            request.addQueryParam("maxRecords", query.getMaxRecords().toString());
        if (query.getView() != null)
            request.addQueryParam("view", query.getView());
        if (query.getFilterByFormula() != null)
            request.addQueryParam("filterByFormula", query.getFilterByFormula());
        if (query.getPageSize() != null)
            handlePageSize(query.getPageSize(), request);
        if (query.getSorts() != null)
            handleSorting(query.getSorts(), request);
        if (query.getOffset()!= null)
            request.addQueryParam("offset", query.getOffset());

        logger.debug("Built query request: {}", request);

        return request;
    }

    /**
     * Add sorting query parameters to a RequestBuilder
     * @param sorting the list of sorting parameters
     * @param request the RequestBuilder
     */
    private void handleSorting(List<Sort> sorting, RequestBuilder request) {
        int i = 0;
        for (Sort sort : sorting) {
            request.addQueryParam("sort[" + i + "][field]", sort.getField());
            request.addQueryParam("sort[" + i + "][direction]", sort.getDirection().toString());
            ++i;
        }
    }

    /**
     * Add a page size query parameter to a RequestBuilder
     * @param pageSize the page size
     * @param request the RequestBuilder
     */
    private void handlePageSize(int pageSize, RequestBuilder request) {
        if (pageSize > 100) {
            logger.warn("Using max pageSize of 100 instead of {}", pageSize);
            pageSize = 100;
        }
        request.addQueryParam("pageSize", Integer.toString(pageSize));
    }
}
