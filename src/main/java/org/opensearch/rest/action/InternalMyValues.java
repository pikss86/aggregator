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
import org.opensearch.common.xcontent.XContentBuilder;
import org.opensearch.search.DocValueFormat;
import org.opensearch.search.aggregations.InternalAggregation;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class InternalMyValues extends InternalAggregation {

    private static final Logger LOGGER = LogManager.getLogger(InternalMyValues.class);

    private final DocValueFormat format;

    protected InternalMyValues(String name, DocValueFormat format, Map<String, Object> metadata) {
        super(name, metadata);
        LOGGER.info("InternalMyValues(String name, Map<Str");
        this.format = format;
    }

    protected InternalMyValues(StreamInput in) throws IOException {
        super(in);
        LOGGER.info("InternalMyValues(StreamInput in)");
        this.format = in.readNamedWriteable(DocValueFormat.class);
    }

    @Override
    protected void doWriteTo(StreamOutput out) throws IOException {
        LOGGER.info("doWriteTo");
        out.writeNamedWriteable(format);
        out.writeString("test test");
        LOGGER.info("doWriteTo end");
    }

    @Override
    public InternalAggregation reduce(List<InternalAggregation> aggregations, ReduceContext reduceContext) {
        LOGGER.info("reduce");
        return null;
    }

    @Override
    protected boolean mustReduceOnSingleInternalAgg() {
        LOGGER.info("mustReduceOnSingleInternalAgg");
        return false;
    }

    @Override
    public Object getProperty(List<String> path) {
        LOGGER.info("getProperty");
        return null;
    }

    @Override
    public XContentBuilder doXContentBody(XContentBuilder builder, Params params) throws IOException {
        LOGGER.info("doXContentBody");
        return null;
    }

    @Override
    public String getWriteableName() {
        LOGGER.info("getWriteableName");
        return MyValuesAggregationBuilder.NAME;
    }
}
