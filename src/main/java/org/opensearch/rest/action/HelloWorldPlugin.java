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
import org.opensearch.cluster.metadata.IndexNameExpressionResolver;
import org.opensearch.cluster.node.DiscoveryNodes;
import org.opensearch.common.settings.ClusterSettings;
import org.opensearch.common.settings.IndexScopedSettings;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.settings.SettingsFilter;
import org.opensearch.plugins.ActionPlugin;
import org.opensearch.plugins.Plugin;
import org.opensearch.plugins.SearchPlugin;
import org.opensearch.rest.RestController;
import org.opensearch.rest.RestHandler;

import java.util.List;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;

public class HelloWorldPlugin extends Plugin implements ActionPlugin, SearchPlugin {

    private static final Logger LOGGER = LogManager.getLogger(HelloWorldPlugin.class);
    @Override
    public List<AggregationSpec> getAggregations() {
        return List.of(
                new AggregationSpec(MyAvgAggregationBuilder.NAME, MyAvgAggregationBuilder::new, MyAvgAggregationBuilder.PARSER)
                        .addResultReader(InternalMyAvg::new)
                        .setAggregatorRegistrar(MyAvgAggregationBuilder::registerAggregators),
                new AggregationSpec(MyMaxAggregationBuilder.NAME, MyMaxAggregationBuilder::new, MyMaxAggregationBuilder.PARSER)
                        .addResultReader(InternalMyMax::new)
                        .setAggregatorRegistrar(MyMaxAggregationBuilder::registerAggregators)
        );
    }

    @Override
    public List<RestHandler> getRestHandlers(final Settings settings,
                                             final RestController restController,
                                             final ClusterSettings clusterSettings,
                                             final IndexScopedSettings indexScopedSettings,
                                             final SettingsFilter settingsFilter,
                                             final IndexNameExpressionResolver indexNameExpressionResolver,
                                             final Supplier<DiscoveryNodes> nodesInCluster) {
        return singletonList(new RestHelloWorldAction());
    }
}