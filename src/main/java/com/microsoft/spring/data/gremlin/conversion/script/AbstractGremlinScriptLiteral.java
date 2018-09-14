/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.script;

import com.microsoft.spring.data.gremlin.common.Constants;
import com.microsoft.spring.data.gremlin.common.GremlinUtils;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedEntityTypeException;
import org.apache.tinkerpop.shaded.jackson.core.JsonProcessingException;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.microsoft.spring.data.gremlin.common.Constants.*;

public abstract class AbstractGremlinScriptLiteral {

    private static String generateProperty(@NonNull String name, @NonNull String value) {
        return String.format(GREMLIN_PRIMITIVE_PROPERTY_STRING, name, value);
    }

    private static String generateProperty(@NonNull String name, @NonNull Integer value) {
        return String.format(GREMLIN_PRIMITIVE_PROPERTY_NUMBER, name, value);
    }

    private static String generateProperty(@NonNull String name, @NonNull Double value) {
        return String.format(GREMLIN_PRIMITIVE_PROPERTY_DOUBLE, name, value);
    }

    private static String generateProperty(@NonNull String name, @NonNull Boolean value) {
        return String.format(GREMLIN_PRIMITIVE_PROPERTY_BOOLEAN, name, value);
    }

    private static String generateProperty(@NonNull String name, @NonNull Long value) {
        return String.format(GREMLIN_PRIMITIVE_PROPERTY_NUMBER, name, value);
    }

    private static String generateUpdateProperty(@NonNull String name, @NonNull String value) {
        return String.format(GREMLIN_PRIMITIVE_UPDATE_PROPERTY_STRING, name, value);
    }

    private static String generateUpdateProperty(@NonNull String name, @NonNull Integer value) {
        return String.format(GREMLIN_PRIMITIVE_UPDATE_PROPERTY_NUMBER, name, value);
    }

    private static String generateUpdateProperty(@NonNull String name, @NonNull Double value) {
        return String.format(GREMLIN_PRIMITIVE_UPDATE_PROPERTY_DOUBLE, name, value);
    }

    private static String generateUpdateProperty(@NonNull String name, @NonNull Boolean value) {
        return String.format(GREMLIN_PRIMITIVE_UPDATE_PROPERTY_BOOLEAN, name, value);
    }

    private static String generateUpdateProperty(@NonNull String name, @NonNull Long value) {
        return String.format(GREMLIN_PRIMITIVE_UPDATE_PROPERTY_NUMBER, name, value);
    }

    protected String generateProperty(@NonNull String name, @NonNull Object value) {

        if (value instanceof Integer) {
            return generateProperty(name, (Integer) value);
        } else if (value instanceof Boolean) {
            return generateProperty(name, (Boolean) value);
        } else if (value instanceof String) {
            return generateProperty(name, (String) value);
        } else if (value instanceof Double) {
            return generateProperty(name, (Double) value);
        } else if (value instanceof Date) {
            return generateProperty(name, GremlinUtils.timeToMilliSeconds(value));
        } else {
            final String propertyScript;

            try {
                propertyScript = generateProperty(name, GremlinUtils.getObjectMapper().writeValueAsString(value));
            } catch (JsonProcessingException e) {
                throw new GremlinUnexpectedEntityTypeException("Failed to write object to String", e);
            }

            return propertyScript;
        }
    }


    protected String generateUpdateProperty(@NonNull String name, @NonNull Object value) {

        if (value instanceof Integer) {
            return generateUpdateProperty(name, (Integer) value);
        } else if (value instanceof Boolean) {
            return generateUpdateProperty(name, (Boolean) value);
        } else if (value instanceof String) {
            return generateUpdateProperty(name, (String) value);
        } else if (value instanceof Double) {
            return generateUpdateProperty(name, (Double) value);
        } else if (value instanceof Date) {
            return generateUpdateProperty(name, GremlinUtils.timeToMilliSeconds(value));
        } else {
            final String propertyScript;

            try {
                propertyScript = generateUpdateProperty(name, GremlinUtils.getObjectMapper().writeValueAsString(value));
            } catch (JsonProcessingException e) {
                throw new GremlinUnexpectedEntityTypeException("Failed to write object to String", e);
            }

            return propertyScript;
        }
    }

    public List<String> generateProperties(@NonNull final Map<String, Object> properties) {
        final List<String> scripts = new ArrayList<>();

        properties.forEach((name, value) -> scripts.add(generateProperty(name, value)));

        return scripts;
    }

    public List<String> generateUpdateProperties(@NonNull final Map<String, Object> properties) {
        final List<String> scripts = new ArrayList<>();

        properties.forEach((name, value) -> scripts.add(generateUpdateProperty(name, value)));

        return scripts;
    }

    private static String generateHas(@NonNull String name, @NonNull Integer value) {
        return String.format(GREMLIN_PRIMITIVE_HAS_NUMBER, name, value);
    }

    private static String generateHas(@NonNull String name, @NonNull Double value) {
        return String.format(GREMLIN_PRIMITIVE_HAS_DOUBLE, name, value);
    }

    private static String generateHas(@NonNull String name, @NonNull Boolean value) {
        return String.format(GREMLIN_PRIMITIVE_HAS_BOOLEAN, name, value);
    }

    private static String generateHas(@NonNull String name, @NonNull String value) {
        return String.format(GREMLIN_PRIMITIVE_HAS_STRING, name, value);
    }

    private static String generateHas(@NonNull String name, @NonNull Long value) {
        return String.format(GREMLIN_PRIMITIVE_HAS_NUMBER, name, value);
    }

    public static String generateHas(@NonNull String name, @NonNull Object value) {

        if (value instanceof Integer) {
            return generateHas(name, (Integer) value);
        } else if (value instanceof Boolean) {
            return generateHas(name, (Boolean) value);
        } else if (value instanceof String) {
            return generateHas(name, (String) value);
        } else if (value instanceof Double) {
            return generateHas(name, (Double) value);
        } else if (value instanceof Date) {
            return generateHas(name, GremlinUtils.timeToMilliSeconds(value));
        } else {
            final String hasScript;

            try {
                hasScript = generateHas(name, GremlinUtils.getObjectMapper().writeValueAsString(value));
            } catch (JsonProcessingException e) {
                throw new GremlinUnexpectedEntityTypeException("Failed to write object to String", e);
            }

            return hasScript;
        }
    }
}
