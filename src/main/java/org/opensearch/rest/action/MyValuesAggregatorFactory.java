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
import org.opensearch.search.DocValueFormat;
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
import java.util.Arrays;
import java.util.Map;

public class MyValuesAggregatorFactory extends ValuesSourceAggregatorFactory {

    private static final Logger LOGGER = LogManager.getLogger(MyValuesAggregatorFactory.class);
    private final ValuesSourceConfig config;


    public MyValuesAggregatorFactory(
            String name,
            ValuesSourceConfig config,
            QueryShardContext queryShardContext,
            AggregatorFactory parent,
            AggregatorFactories.Builder subFactoriesBuilder,
            Map<String, Object> metadata
    ) throws IOException {
        super(name, config, queryShardContext, parent, subFactoriesBuilder, metadata);
        LOGGER.info("MyValuesAggregatorFactory(String name, ValuesSourceConfig config, QueryShardCon");
        this.config = config;
    }

    static void registerAggregators(ValuesSourceRegistry.Builder builder) {
        LOGGER.info("registerAggregators");
        builder.register(
                MyValuesAggregationBuilder.REGISTRY_KEY,
                Arrays.asList(CoreValuesSourceType.BYTES, CoreValuesSourceType.IP),
                bytesSupplier(),
                true
        );
    }

    private static MyValuesAggregatorSupplier bytesSupplier() {
        LOGGER.info("bytesSupplier");
        return new MyValuesAggregatorSupplier() {
            @Override
            public Aggregator build(
                    String name,
                    AggregatorFactories factories,
                    ValuesSourceConfig config,
                    Aggregator parent,
                    DocValueFormat format,
                    SearchContext context,
                    Map<String, Object> metadata
            ) throws IOException {
                LOGGER.info("public Aggregator build() { " + name);
                return new MyValuesAggregator(name, config, factories, context, parent, format, metadata);
            }
        };
    }

    @Override
    protected Aggregator createUnmapped(SearchContext searchContext, Aggregator parent, Map<String, Object> metadata) throws IOException {
        LOGGER.info("createUnmapped");
        return null;
    }

    @Override
    protected Aggregator doCreateInternal(SearchContext searchContext, Aggregator parent, CardinalityUpperBound cardinality, Map<String, Object> metadata) throws IOException {
        LOGGER.info("doCreateInternal");
        MyValuesAggregatorSupplier aggregatorSupplier = queryShardContext.getValuesSourceRegistry()
                .getAggregator(MyValuesAggregationBuilder.REGISTRY_KEY, config);
        return aggregatorSupplier.build(
                name,
                factories,
                config,
                parent,
                config.format(),
                searchContext,
                metadata
        );
    }
}
