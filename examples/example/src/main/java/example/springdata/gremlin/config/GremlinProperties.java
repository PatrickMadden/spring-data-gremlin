/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package example.springdata.gremlin.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.microsoft.spring.data.gremlin.common.Constants;
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

    private int maxContentLength = Constants.DEFAULT_MAX_CONTENT_LENGTH;

    private String serializersName = Constants.DEFAULT_SERIALIZERS.name();
}
