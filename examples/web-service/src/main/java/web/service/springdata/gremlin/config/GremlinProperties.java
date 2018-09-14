/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package web.service.springdata.gremlin.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("gremlin")
public class GremlinProperties {
    private String endpoint;

    private int port;

    private String username;

    private String password;

    private boolean telemetryAllowed = true;

    private boolean sslEnabled = true;

    private int maxContentLength = 65536;

    private String serializersName = Constants.DEFAULT_SERIALIZERS.name();
}
