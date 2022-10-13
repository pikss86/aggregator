/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.rest.action;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.search.SearchAction;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.node.NodeClient;
import org.opensearch.common.Strings;
import org.opensearch.common.xcontent.XContentBuilder;
import org.opensearch.common.xcontent.json.JsonXContent;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.BytesRestResponse;
import org.opensearch.rest.RestRequest;
import org.opensearch.rest.RestStatus;
import org.opensearch.search.aggregations.Aggregation;
import org.opensearch.search.aggregations.bucket.terms.StringTerms;
import org.opensearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.opensearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.opensearch.rest.RestRequest.Method.GET;
import static org.opensearch.rest.RestRequest.Method.POST;

public class RestHelloWorldAction extends BaseRestHandler {
    private static final Logger LOGGER = LogManager.getLogger(RestHelloWorldAction.class);
    @Override
    public String getName() {
        return "rest_handler_hello_world";
    }

    @Override
    public List<Route> routes() {
        return unmodifiableList(asList(
                new Route(GET, "/_plugins/hello_world"),
                new Route(POST, "/_plugins/hello_world")));
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {
        LOGGER.info("prepareRequest");
        String paramFunctionName = null;
        String paramIndexName = null;
        String paramFieldName = null;
        String paramFieldSize = null;
        if (request.hasContent()) {
            Map<String, String> params = request.contentParser().mapStrings();
            paramFunctionName = params.get("functionName");
            paramIndexName = params.get("indexName");
            paramFieldName = params.get("fieldName");
            paramFieldSize = params.get("fieldSize");
        }
        final String functionName = paramFunctionName != null ? paramFunctionName : "myavg";
        final String indexName = paramIndexName != null ? paramIndexName : "opensearch_dashboards_sample_data_ecommerce";
        final String fieldName = paramFieldName != null ? paramFieldName : "taxful_total_price";
        final int fieldSize = paramFieldSize != null ? Integer.parseInt(paramFieldSize) : 10000;

        SearchRequest searchRequest = new SearchRequest();
        if (searchRequest.source() == null) {
            searchRequest.source(new SearchSourceBuilder());
        }
        searchRequest.indices(indexName);
        if ("myavg".equals(functionName)) {
            searchRequest.source().aggregation(
                    new MyAvgAggregationBuilder("testagg")
                            .field(fieldName)
            );
        } else if ("mymax".equals(functionName)) {
            searchRequest.source().aggregation(
                    new MyMaxAggregationBuilder("testagg")
                            .field(fieldName)
            );
        } else if ("myvalues".equals(functionName)) {
            LOGGER.info("request add begin");
            searchRequest.source().aggregation(
                    new TermsAggregationBuilder("testagg")
                            .size(fieldSize)
                            .field(fieldName)
            );
            LOGGER.info("request add end");
        }

        return channel -> {
            LOGGER.info("channel");
            RestCancellableNodeClient cancelClient = new RestCancellableNodeClient(client, request.getHttpChannel());

            cancelClient.execute(SearchAction.INSTANCE, searchRequest, new MyActionListener(
                    functionName, indexName, fieldName)
            {
                @Override
                public void onResponse(SearchResponse searchResponse) {
                    LOGGER.info("onResponse");
                    LOGGER.info("1");
                    List<Aggregation> aggsList = searchResponse.getAggregations().asList();
                    for (int i = 0; i < aggsList.size(); i++) {

                        LOGGER.info(aggsList.get(i).getName());
                    }
                    Aggregation agg = searchResponse.getAggregations().get("testagg");
                    LOGGER.info(agg + "");
                    LOGGER.info("2");
                    String type = agg.getType();
                    LOGGER.info("3");
                    if ("myavg".equals(functionName)) {
                        String value = ((MyAvg) agg).getValueAsString();
                        channel.sendResponse(new BytesRestResponse(RestStatus.OK, value));
                    } else if ("mymax".equals(functionName)) {
                        String value = ((MyMax) agg).getValueAsString();
                        channel.sendResponse(new BytesRestResponse(RestStatus.OK, value));
                    } else if ("myvalues".equals(functionName)) {
                        LOGGER.info("4");
                        List<StringTerms.Bucket> buckets = ((StringTerms)agg).getBuckets();
                        LOGGER.info("size = " + buckets.size());
                        try {
                            XContentBuilder xcb = JsonXContent.contentBuilder();
                            xcb.startArray();
                            for (StringTerms.Bucket bucket : buckets) {
                                xcb.value(bucket.getKeyAsString());
                            }
                            xcb.endArray();
                            channel.sendResponse(new BytesRestResponse(RestStatus.OK, Strings.toString(xcb)));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    LOGGER.info("onFailure");
                }
            });
        };
    }
}