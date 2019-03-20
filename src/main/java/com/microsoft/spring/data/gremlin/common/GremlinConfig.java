/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.common;


import lombok.*;

@Getter
@Setter
@Builder(builderMethodName = "defaultBuilder")
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class GremlinConfig {
    private String endpoint;

    private int port;

    private String username;

    private String password;

    private boolean sslEnabled = true;

    private boolean telemetryAllowed;

    private String serializer;

    private int maxContentLength = Constants.DEFAULT_MAX_CONTENT_LENGTH;

    private int maxWaitForConnection = Constants.DEFAULT_MAX_WAIT_FOR_CONNECTION;

    private int minConnectionPoolSize = Constants.DEFAULT_MIN_CONNECTION_POOL_SIZE;

    private int maxConnectionPoolSize = Constants.DEFAULT_MAX_CONNECTION_POOL_SIZE;

    private int maxWaitForSessionClose = Constants.DEFAULT_MAX_WAIT_FOR_SESSION_CLOSE;

    private int minSimultaneousUsagePerConnection = Constants.DEFUAULT_MIN_SIMULTANEOUS_USAGE_PER_CONNECTION;

    private int maxSimultaneousUsagePerConnection = Constants.DEFUAULT_MAX_SIMULTANEOUS_USAGE_PER_CONNECTION;

    private int minInProcessPerConnection = Constants.DEFAULT_MIN_IN_PROCESS_PER_CONNECTION;

    private int maxInProcessPerConnection = Constants.DEFAULT_MAX_IN_PROCESS_PER_CONNECTION;

    private long keepAliveInterval = Constants.DEFAULT_KEEP_ALIVE_INTERVAL;

    private int workerPoolSize = Constants.DEFAULT_WORKER_POOL_SIZE;

    public static GremlinConfigBuilder builder(String endpoint, String username, String password) {
        return defaultBuilder()
            .endpoint(endpoint)
            .username(username)
            .password(password)
            .port(Constants.DEFAULT_ENDPOINT_PORT)
            .telemetryAllowed(true)
            .maxContentLength(Constants.DEFAULT_MAX_CONTENT_LENGTH)
            .maxWaitForConnection(Constants.DEFAULT_MAX_WAIT_FOR_CONNECTION)
            .serializer(Constants.DEFAULT_SERIALIZERS.name())
            .minConnectionPoolSize(Constants.DEFAULT_MIN_CONNECTION_POOL_SIZE)
            .maxConnectionPoolSize(Constants.DEFAULT_MAX_CONNECTION_POOL_SIZE)
            .maxWaitForSessionClose(Constants.DEFAULT_MAX_WAIT_FOR_SESSION_CLOSE)
            .minSimultaneousUsagePerConnection(Constants.DEFUAULT_MIN_SIMULTANEOUS_USAGE_PER_CONNECTION)
            .maxSimultaneousUsagePerConnection(Constants.DEFUAULT_MAX_SIMULTANEOUS_USAGE_PER_CONNECTION)
            .minInProcessPerConnection(Constants.DEFAULT_MIN_IN_PROCESS_PER_CONNECTION)
            .maxInProcessPerConnection(Constants.DEFAULT_MAX_IN_PROCESS_PER_CONNECTION)
            .keepAliveInterval(Constants.DEFAULT_KEEP_ALIVE_INTERVAL)
            .workerPoolSize(Constants.DEFAULT_WORKER_POOL_SIZE);
    }
}
