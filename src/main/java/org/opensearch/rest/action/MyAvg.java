/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.rest.action;

import org.opensearch.search.aggregations.metrics.NumericMetricsAggregation;

public interface MyAvg extends NumericMetricsAggregation.SingleValue {

    /**
     * The average value.
     */
    double getValue();
}
