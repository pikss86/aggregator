/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.rest.action;

import org.opensearch.action.ActionListener;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.rest.RestChannel;
import org.opensearch.search.aggregations.Aggregation;

public abstract class MyActionListener implements ActionListener<SearchResponse> {


    private final String functionName;
    private final String indexName;
    private final String fiindexName;

    public MyActionListener(String functionName, String indexName, String fieldName) {
        this.functionName = functionName;
        this.indexName = indexName;
        this.fiindexName = fieldName;
    }

}
