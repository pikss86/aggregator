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
import org.opensearch.search.aggregations.metrics.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InternalMyAvg extends InternalNumericMetricsAggregation.SingleValue implements MyAvg {
    private static final Logger LOGGER = LogManager.getLogger(InternalMyAvg.class);
    private final double sum;
    private final long count;

    public InternalMyAvg(String name, double sum, long count, DocValueFormat format, Map<String, Object> metadata) {
        super(name, metadata);
        LOGGER.info("InternalMyAvg");
        this.sum = sum;
        this.count = count;
        this.format = format;
    }

    /**
     * Read from a stream.
     */
    public InternalMyAvg(StreamInput in) throws IOException {
        super(in);
        LOGGER.info("InternalMyAvg");
        format = in.readNamedWriteable(DocValueFormat.class);
        sum = in.readDouble();
        count = in.readVLong();
    }

    @Override
    protected void doWriteTo(StreamOutput out) throws IOException {
        LOGGER.info("doWriteTo");
        out.writeNamedWriteable(format);
        out.writeDouble(sum);
        out.writeVLong(count);
        LOGGER.info("doWriteTo end");
    }

    @Override
    public double value() {
        LOGGER.info("value");
        return getValue();
    }

    @Override
    public double getValue() {
        LOGGER.info("myvalue " + (sum / count));
        return sum / count;
    }

    double getSum() {
        return sum;
    }

    long getCount() {
        return count;
    }

    DocValueFormat getFormatter() {
        return format;
    }

    @Override
    public String getWriteableName() {
        LOGGER.info("getWriteableName");
        return MyAvgAggregationBuilder.NAME;
    }

    @Override
    public InternalMyAvg reduce(List<InternalAggregation> aggregations, ReduceContext reduceContext) {
        LOGGER.info("reduce");
        CompensatedSum kahanSummation = new CompensatedSum(0, 0);
        long count = 0;
        // Compute the sum of double values with Kahan summation algorithm which is more
        // accurate than naive summation.
        for (InternalAggregation aggregation : aggregations) {
            InternalMyAvg avg = (InternalMyAvg) aggregation;
            count += avg.count;
            kahanSummation.add(avg.sum);
        }
        return new InternalMyAvg(getName(), kahanSummation.value(), count, format, getMetadata());
    }

    @Override
    public XContentBuilder doXContentBody(XContentBuilder builder, Params params) throws IOException {
        LOGGER.info("doXContentBody");
        builder.field(CommonFields.VALUE.getPreferredName(), count != 0 ? getValue() : null);
        if (count != 0 && format != DocValueFormat.RAW) {
            builder.field(CommonFields.VALUE_AS_STRING.getPreferredName(), format.format(getValue()).toString());
        }
        return builder;
    }

    @Override
    public int hashCode() {
        LOGGER.info("hashCode");
        return Objects.hash(super.hashCode(), sum, count, format.getWriteableName());
    }

    @Override
    public boolean equals(Object obj) {
        LOGGER.info("equals");
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (super.equals(obj) == false) return false;
        InternalMyAvg other = (InternalMyAvg) obj;
        return Objects.equals(sum, other.sum)
                && Objects.equals(count, other.count)
                && Objects.equals(format.getWriteableName(), other.format.getWriteableName());
    }
}
