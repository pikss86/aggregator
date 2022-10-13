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
import org.opensearch.search.aggregations.metrics.InternalNumericMetricsAggregation;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InternalMyMax extends InternalNumericMetricsAggregation.SingleValue implements MyMax {
    private static final Logger LOGGER = LogManager.getLogger(InternalMyMax.class);
    private final double max;

    public InternalMyMax(String name, double max, DocValueFormat format, Map<String, Object> metadata) {
        super(name, metadata);
        LOGGER.info("create InternalMyMax " + max);
        this.max = max;
        this.format = format;
    }

    /**
     * Read from a stream.
     */
    public InternalMyMax(StreamInput in) throws IOException {
        super(in);
        LOGGER.info("create InternalMyMax");
        format = in.readNamedWriteable(DocValueFormat.class);
        max = in.readDouble();
    }

    @Override
    protected void doWriteTo(StreamOutput out) throws IOException {
        LOGGER.info("doWriteTo");
        out.writeNamedWriteable(format);
        out.writeDouble(max);
        LOGGER.info("doWriteTo end");
    }

    @Override
    public double value() {
        LOGGER.info("value");
        return getValue();
    }

    @Override
    public double getValue() {
        LOGGER.info("mymaxvalue " + max);
        return max;
    }

    double getMax() {
        return max;
    }

    DocValueFormat getFormatter() {
        return format;
    }

    @Override
    public String getWriteableName() {
        LOGGER.info("getWriteableName");
        return MyMaxAggregationBuilder.NAME;
    }

    @Override
    public InternalMyMax reduce(List<InternalAggregation> aggregations, ReduceContext reduceContext) {
        LOGGER.info("reduce");
        double max = Double.NEGATIVE_INFINITY;
        for (InternalAggregation aggregation : aggregations) {
            max = Math.max(max, ((InternalMyMax) aggregation).max);
        }
        return new InternalMyMax(name, max, format, getMetadata());
    }

    @Override
    public XContentBuilder doXContentBody(XContentBuilder builder, Params params) throws IOException {
        LOGGER.info("doXContentBody");
        builder.field(CommonFields.VALUE.getPreferredName(), max != Double.NEGATIVE_INFINITY ? getValue() : null);
        if (max != Double.NEGATIVE_INFINITY && format != DocValueFormat.RAW) {
            builder.field(CommonFields.VALUE_AS_STRING.getPreferredName(), format.format(getValue()).toString());
        }
        return builder;
    }

    @Override
    public int hashCode() {
        LOGGER.info("hashCode");
        return Objects.hash(super.hashCode(), max, format.getWriteableName());
    }

    @Override
    public boolean equals(Object obj) {
        LOGGER.info("equals");
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (super.equals(obj) == false) return false;
        InternalMyMax other = (InternalMyMax) obj;
        return Objects.equals(max, other.max)
                && Objects.equals(format.getWriteableName(), other.format.getWriteableName());
    }
}