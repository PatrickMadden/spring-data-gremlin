/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.common;


import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("gremlin")
public class TestGremlinProperties {
    private String endpoint;

    private int port;

    private String username;

    private String password;

    private boolean sslEnabled = true;

    private boolean telemetryAllowed = true;

    private int maxContentLength = Constants.DEFAULT_MAX_CONTENT_LENGTH;

    private int maxWaitForConnection = Constants.DEFAULT_MAX_WAIT_FOR_CONNECTION;

    private String serializer = Constants.DEFAULT_SERIALIZERS.name();
}
