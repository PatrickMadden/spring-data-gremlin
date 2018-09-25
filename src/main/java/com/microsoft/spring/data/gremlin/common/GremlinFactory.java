/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.common;

import com.microsoft.spring.data.gremlin.exception.GremlinIllegalConfigurationException;
import com.microsoft.spring.data.gremlin.telemetry.TelemetryTracker;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.ser.Serializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;


public class GremlinFactory {

    private Cluster gremlinCluster;

    private GremlinConfig gremlinConfig;

    @Autowired
    private TelemetryTracker telemetryTracker;

    public GremlinFactory(@NonNull GremlinConfig gremlinConfig) {
        final int port = gremlinConfig.getPort();
        if (port <= 0 || port > 65535) {
            gremlinConfig.setPort(Constants.DEFAULT_ENDPOINT_PORT);
        }

        this.gremlinConfig = gremlinConfig;
    }

    private void trackTelemetryCustomEvent() {
        this.telemetryTracker.trackEvent(getClass().getSimpleName());
    }

    protected Cluster createGremlinCluster() throws GremlinIllegalConfigurationException {
        final Cluster cluster;

        try {
            final String serializersName = this.gremlinConfig.getSerializersName();

            Serializers serializers;

            if (serializersName != null) {
                serializers = Serializers.valueOf(serializersName);
            } else {
                serializers = Constants.DEFAULT_SERIALIZERS;
            }

            cluster = Cluster.build(this.gremlinConfig.getEndpoint())
                    .serializer(serializers)
                    .credentials(this.gremlinConfig.getUsername(), this.gremlinConfig.getPassword())
                    .enableSsl(this.gremlinConfig.isSslEnabled())
                    .port(this.gremlinConfig.getPort())
                    .maxContentLength(this.gremlinConfig.getMaxContentLength())
                    .maxWaitForConnection(this.gremlinConfig.getMaxWaitForConnection())
                    .create();
        } catch (IllegalArgumentException e) {
            throw new GremlinIllegalConfigurationException("Invalid configuration of Gremlin", e);
        }

        trackTelemetryCustomEvent();

        return cluster;
    }

    public Client getGremlinClient() {

        if (this.gremlinCluster == null) {
            this.gremlinCluster = this.createGremlinCluster();
        }

        return this.gremlinCluster.connect();
    }
}
