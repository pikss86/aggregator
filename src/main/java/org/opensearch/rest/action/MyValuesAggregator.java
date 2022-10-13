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
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.search.ScoreMode;
import org.opensearch.common.io.stream.StreamOutput;
import org.opensearch.common.util.BigArrays;
import org.opensearch.common.xcontent.XContentBuilder;
import org.opensearch.index.fielddata.SortedBinaryDocValues;
import org.opensearch.index.fielddata.SortedNumericDoubleValues;
import org.opensearch.search.DocValueFormat;
import org.opensearch.search.aggregations.*;
import org.opensearch.search.aggregations.bucket.BucketsAggregator;
import org.opensearch.search.aggregations.bucket.DeferableBucketAggregator;
import org.opensearch.search.aggregations.bucket.terms.IncludeExclude;
import org.opensearch.search.aggregations.bucket.terms.TermsAggregator;
import org.opensearch.search.aggregations.support.ValuesSource;
import org.opensearch.search.aggregations.support.ValuesSourceConfig;
import org.opensearch.search.internal.SearchContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MyValuesAggregator extends BucketsAggregator {

    private static final Logger LOGGER = LogManager.getLogger(MyValuesAggregator.class);
    private final DocValueFormat format;
    private final Map<String, Object> metadata;
    private final ValuesSourceConfig config;

    public MyValuesAggregator(
            String name,
            ValuesSourceConfig config,
            AggregatorFactories factories,
            SearchContext context,
            Aggregator parent,
            DocValueFormat format,
            Map<String, Object> metadata
    ) throws IOException {
        super(name, factories, context, parent, CardinalityUpperBound.MANY, metadata);
        this.format = format;
        this.metadata = metadata;
        this.config = config;
    }

    @Override
    protected LeafBucketCollector getLeafCollector(LeafReaderContext ctx, LeafBucketCollector sub) throws IOException {
        LOGGER.info("getLeafCollector");
        LOGGER.info("getLeafCollector " + ctx);
        LOGGER.info("getLeafCollector " + sub);
        SortedBinaryDocValues docs = config.getValuesSource().bytesValues(ctx);
        LOGGER.info("getLeafCollector " + docs.docValueCount());
        return new LeafBucketCollector() {
            @Override
            public void collect(int doc, long owningBucketOrd) throws IOException {
                LOGGER.info("collect doc = " + doc + " owningBucketOrd = " + owningBucketOrd);
            }
        };
    }

    @Override
    public InternalAggregation[] buildAggregations(long[] owningBucketOrds) throws IOException {
        LOGGER.info("buildAggregations " + owningBucketOrds.length);
        InternalAggregation[] results = new InternalAggregation[owningBucketOrds.length];
        for (int ordIdx = 0; ordIdx < owningBucketOrds.length; ordIdx++) {
            results[ordIdx] = new InternalMyValues(name, format, metadata);
        }
        return results;
    }

    @Override
    public InternalAggregation buildEmptyAggregation() {
        LOGGER.info("buildEmptyAggregation");
        return new InternalMyValues(name, format, metadata);
    }

//
//
//    @Override
//    public InternalAggregation[] buildAggregations(long[] owningBucketOrds) throws IOException {
//        LOGGER.info("buildAggregations " + owningBucketOrds.length);
//        InternalAggregation[] results = new InternalAggregation[owningBucketOrds.length];
//        for (int ordIdx = 0; ordIdx < owningBucketOrds.length; ordIdx++) {
//            results[ordIdx] = new InternalMyValues(_name, _format, _metadata);
//        }
//        return results;
//    }
//


//    @Override
//    public void close() {
//        LOGGER.info("close");
//    }

//    @Override
//    public LeafBucketCollector getLeafCollector(LeafReaderContext ctx) throws IOException {
//        LOGGER.info("getLeafCollector");
//        LOGGER.info("valuesSource " + valuesSource);
//        if (valuesSource == null) {
//            return LeafBucketCollector.NO_OP_COLLECTOR;
//        }
//        final BigArrays bigArrays = _context.bigArrays();
//        final SortedBinaryDocValues values = valuesSource.bytesValues(ctx);
//        LOGGER.info("docValueCount " + values.docValueCount());
//        return new LeafBucketCollector() {
//            @Override
//            public void collect(int doc, long owningBucketOrd) throws IOException {
//                LOGGER.info("collect doc = " + doc + " owningBucketOrd = " + owningBucketOrd);
//            }
//        };
//    }
//
//    @Override
//    public ScoreMode scoreMode() {
//        LOGGER.info("scoreMode");
//        return ScoreMode.COMPLETE_NO_SCORES;
//    }
//
//    @Override
//    protected LeafBucketCollector getLeafCollector(LeafReaderContext ctx, LeafBucketCollector sub) throws IOException {
//        return null;
//    }

//    @Override
//    public void preCollection() throws IOException {
//        LOGGER.info("preCollection");
//    }

//    @Override
//    public void postCollection() throws IOException {
//        LOGGER.info("postCollection");
//    }

}
