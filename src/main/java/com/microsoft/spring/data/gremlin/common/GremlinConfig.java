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

    private boolean telemetryAllowed;

    private boolean sslEnabled = true;

    private int maxContentLength = Constants.DEFAULT_MAX_CONTENT_LENGTH;

    private int maxWaitForConnection = Constants.DEFAULT_MAX_WAIT_FOR_CONNECTION;

    private String serializersName;

    public static GremlinConfigBuilder builder(String endpoint, String username, String password) {
        return defaultBuilder()
                .endpoint(endpoint)
                .username(username)
                .password(password)
                .port(Constants.DEFAULT_ENDPOINT_PORT)
                .telemetryAllowed(true)
                .maxContentLength(Constants.DEFAULT_MAX_CONTENT_LENGTH)
                .maxWaitForConnection(Constants.DEFAULT_MAX_WAIT_FOR_CONNECTION)
                .serializersName(Constants.DEFAULT_SERIALIZERS.name());
    }
}
