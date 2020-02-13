package com.sybit.airtable.internal.http;

import java.util.List;
import com.sybit.airtable.Query;
import com.sybit.airtable.Sort;
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
