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
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.ScoreMode;
import org.opensearch.common.lease.Releasables;
import org.opensearch.common.util.BigArrays;
import org.opensearch.common.util.DoubleArray;
import org.opensearch.common.util.LongArray;
import org.opensearch.common.xcontent.XContentBuilder;
import org.opensearch.index.fielddata.SortedNumericDoubleValues;
import org.opensearch.search.DocValueFormat;
import org.opensearch.search.aggregations.Aggregator;
import org.opensearch.search.aggregations.InternalAggregation;
import org.opensearch.search.aggregations.LeafBucketCollector;
import org.opensearch.search.aggregations.LeafBucketCollectorBase;
import org.opensearch.search.aggregations.metrics.CompensatedSum;
import org.opensearch.search.aggregations.metrics.NumericMetricsAggregator;
import org.opensearch.search.aggregations.support.ValuesSource;
import org.opensearch.search.aggregations.support.ValuesSourceConfig;
import org.opensearch.search.internal.SearchContext;

import java.io.IOException;
import java.util.Map;


/**
 * Aggregate all docs into an average
 *
 * @opensearch.internal
 */
class MyAvgAggregator extends NumericMetricsAggregator.SingleValue implements MyAvg {

    private static final Logger LOGGER = LogManager.getLogger(MyAvgAggregator.class);
    final ValuesSource.Numeric valuesSource;

    LongArray counts;
    DoubleArray sums;
    DoubleArray compensations;
    DocValueFormat format;

    MyAvgAggregator(
            String name,
            ValuesSourceConfig valuesSourceConfig,
            SearchContext context,
            Aggregator parent,
            Map<String, Object> metadata
    ) throws IOException {
        super(name, context, parent, metadata);
        this.valuesSource = valuesSourceConfig.hasValues() ? (ValuesSource.Numeric) valuesSourceConfig.getValuesSource() : null;
        this.format = valuesSourceConfig.format();
        if (valuesSource != null) {
            final BigArrays bigArrays = context.bigArrays();
            counts = bigArrays.newLongArray(1, true);
            sums = bigArrays.newDoubleArray(1, true);
            compensations = bigArrays.newDoubleArray(1, true);
        }
    }

    @Override
    public ScoreMode scoreMode() {
        return valuesSource != null && valuesSource.needsScores() ? ScoreMode.COMPLETE : ScoreMode.COMPLETE_NO_SCORES;
    }

    @Override
    public LeafBucketCollector getLeafCollector(LeafReaderContext ctx, final LeafBucketCollector sub) throws IOException {
        if (valuesSource == null) {
            return LeafBucketCollector.NO_OP_COLLECTOR;
        }
        final BigArrays bigArrays = context.bigArrays();
        final SortedNumericDoubleValues values = valuesSource.doubleValues(ctx);
        final CompensatedSum kahanSummation = new CompensatedSum(0, 0);

        return new LeafBucketCollectorBase(sub, values) {
            @Override
            public void collect(int doc, long bucket) throws IOException {
                counts = bigArrays.grow(counts, bucket + 1);
                sums = bigArrays.grow(sums, bucket + 1);
                compensations = bigArrays.grow(compensations, bucket + 1);

                if (values.advanceExact(doc)) {
                    final int valueCount = values.docValueCount();
                    counts.increment(bucket, valueCount);
                    // Compute the sum of double values with Kahan summation algorithm which is more
                    // accurate than naive summation.
                    double sum = sums.get(bucket);
                    double compensation = compensations.get(bucket);

                    kahanSummation.reset(sum, compensation);

                    for (int i = 0; i < valueCount; i++) {
                        double value = values.nextValue();
                        kahanSummation.add(value);
                    }

                    sums.set(bucket, kahanSummation.value());
                    compensations.set(bucket, kahanSummation.delta());
                }
            }
        };
    }

    @Override
    public double metric(long owningBucketOrd) {
        if (valuesSource == null || owningBucketOrd >= sums.size()) {
            return Double.NaN;
        }
        return sums.get(owningBucketOrd) / counts.get(owningBucketOrd);
    }

    @Override
    public InternalAggregation buildAggregation(long bucket) {
        if (valuesSource == null || bucket >= sums.size()) {
            return buildEmptyAggregation();
        }
        return new InternalMyAvg(name, sums.get(bucket), counts.get(bucket), format, metadata());
    }

    @Override
    public InternalAggregation buildEmptyAggregation() {
        return new InternalMyAvg(name, 0.0, 0L, format, metadata());
    }

    @Override
    public void doClose() {
        Releasables.close(counts, sums, compensations);
    }

    @Override
    public double getValue() {
        return sums.size();
    }

    @Override
    public double value() {
        return 0;
    }

    @Override
    public String getValueAsString() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return null;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        return null;
    }
}
