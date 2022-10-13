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
import org.opensearch.common.io.stream.StreamInput;
import org.opensearch.common.io.stream.StreamOutput;
import org.opensearch.common.xcontent.ObjectParser;
import org.opensearch.common.xcontent.XContentBuilder;
import org.opensearch.index.query.QueryShardContext;
import org.opensearch.search.aggregations.AggregationBuilder;
import org.opensearch.search.aggregations.AggregatorFactories;
import org.opensearch.search.aggregations.AggregatorFactory;
import org.opensearch.search.aggregations.support.*;

import java.io.IOException;
import java.util.Map;

public class MyValuesAggregationBuilder extends ValuesSourceAggregationBuilder<MyValuesAggregationBuilder> {

    private static final Logger LOGGER = LogManager.getLogger(MyValuesAggregationBuilder.class);

    public static final String NAME = "myvalues";
    public static final ValuesSourceRegistry.RegistryKey<MyValuesAggregatorSupplier> REGISTRY_KEY = new ValuesSourceRegistry.RegistryKey<>(
            NAME,
            MyValuesAggregatorSupplier.class
    );
    public static final ObjectParser<MyValuesAggregationBuilder, String> PARSER = ObjectParser.fromBuilder(NAME, MyValuesAggregationBuilder::new);

    public static void registerAggregators(ValuesSourceRegistry.Builder builder) {
        LOGGER.info("registerAggregators");
        MyValuesAggregatorFactory.registerAggregators(builder);
    }

    protected MyValuesAggregationBuilder(String name) {
        super(name);
        LOGGER.info("MyValuesAggregationBuilder(String name) " + name);
    }

    protected MyValuesAggregationBuilder(ValuesSourceAggregationBuilder<MyValuesAggregationBuilder> clone, AggregatorFactories.Builder factoriesBuilder, Map<String, Object> metadata) {
        super(clone, factoriesBuilder, metadata);
        LOGGER.info("MyValuesAggregationBuilder");
    }

    protected MyValuesAggregationBuilder(StreamInput in) throws IOException {
        super(in);
        LOGGER.info("MyValuesAggregationBuilder(StreamInput in) ");
    }

    @Override
    protected void innerWriteTo(StreamOutput out) throws IOException {
        LOGGER.info("innerWriteTo");
    }

    @Override
    protected ValuesSourceRegistry.RegistryKey<?> getRegistryKey() {
        LOGGER.info("getRegistryKey");
        return REGISTRY_KEY;
    }

    @Override
    protected ValuesSourceType defaultValueSourceType() {
        LOGGER.info("defaultValueSourceType");
        return CoreValuesSourceType.BYTES;
    }

    @Override
    protected ValuesSourceAggregatorFactory innerBuild(
            QueryShardContext queryShardContext,
            ValuesSourceConfig config,
            AggregatorFactory parent,
            AggregatorFactories.Builder subFactoriesBuilder
    ) throws IOException {
        LOGGER.info("innerBuild");
        return new MyValuesAggregatorFactory(
                name,
                config,
                queryShardContext,
                parent,
                subFactoriesBuilder,
                metadata
        );
    }

    @Override
    protected XContentBuilder doXContentBody(XContentBuilder builder, Params params) throws IOException {
        LOGGER.info("doXContentBody");
        return builder;
    }

    @Override
    protected AggregationBuilder shallowCopy(AggregatorFactories.Builder factoriesBuilder, Map<String, Object> metadata) {
        LOGGER.info("shallowCopy");
        return new MyValuesAggregationBuilder(this, factoriesBuilder, metadata);
    }

    @Override
    public BucketCardinality bucketCardinality() {
        LOGGER.info("bucketCardinality");
        return null;
    }

    @Override
    public String getType() {
        LOGGER.info("getType");
        return NAME;
    }
}
