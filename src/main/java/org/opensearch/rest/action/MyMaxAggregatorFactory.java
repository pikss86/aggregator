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
import org.opensearch.index.query.QueryShardContext;
import org.opensearch.search.aggregations.Aggregator;
import org.opensearch.search.aggregations.AggregatorFactories;
import org.opensearch.search.aggregations.AggregatorFactory;
import org.opensearch.search.aggregations.CardinalityUpperBound;
import org.opensearch.search.aggregations.support.CoreValuesSourceType;
import org.opensearch.search.aggregations.support.ValuesSourceAggregatorFactory;
import org.opensearch.search.aggregations.support.ValuesSourceConfig;
import org.opensearch.search.aggregations.support.ValuesSourceRegistry;
import org.opensearch.search.internal.SearchContext;

import java.io.IOException;
import java.util.Map;

public class MyMaxAggregatorFactory extends ValuesSourceAggregatorFactory {

    private static final Logger LOGGER = LogManager.getLogger(MyMaxAggregatorFactory.class);

    static void registerAggregators(ValuesSourceRegistry.Builder builder) {
        builder.register(
                MyMaxAggregationBuilder.REGISTRY_KEY,
                org.opensearch.common.collect.List.of(CoreValuesSourceType.NUMERIC, CoreValuesSourceType.DATE, CoreValuesSourceType.BOOLEAN),
                MyMaxAggregator::new,
                true
        );
    }

    public MyMaxAggregatorFactory(String name, ValuesSourceConfig config, QueryShardContext queryShardContext, AggregatorFactory parent, AggregatorFactories.Builder subFactoriesBuilder, Map<String, Object> metadata) throws IOException {
        super(name, config, queryShardContext, parent, subFactoriesBuilder, metadata);
    }

    @Override
    protected Aggregator createUnmapped(SearchContext searchContext, Aggregator parent, Map<String, Object> metadata) throws IOException {
        return new MyMaxAggregator(name, config, searchContext, parent, metadata);
    }

    @Override
    protected Aggregator doCreateInternal(SearchContext searchContext, Aggregator parent, CardinalityUpperBound cardinality, Map<String, Object> metadata) throws IOException {
        return queryShardContext.getValuesSourceRegistry()
                .getAggregator(MyMaxAggregationBuilder.REGISTRY_KEY, config)
                .build(name, config, searchContext, parent, metadata);
    }
}
