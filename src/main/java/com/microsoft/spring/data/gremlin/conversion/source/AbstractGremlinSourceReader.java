/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.source;


import com.microsoft.spring.data.gremlin.common.GremlinUtils;
import com.microsoft.spring.data.gremlin.exception.GremlinEntityInformationException;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedEntityTypeException;
import org.apache.tinkerpop.shaded.jackson.databind.JavaType;
import org.apache.tinkerpop.shaded.jackson.databind.type.TypeFactory;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import java.lang.reflect.Field;
import java.util.Date;

import lombok.NonNull;

public abstract class AbstractGremlinSourceReader {

    public static final int NULLHASH = "null".hashCode();

    protected Object readProperty(@NonNull PersistentProperty property, @Nullable Object value) {
        final Class<?> type = property.getTypeInformation().getType();
        final JavaType javaType = TypeFactory.defaultInstance().constructType(property.getType());

        if (value == null) {
            return null;
        } else if (type == int.class || type == Integer.class
                || type == Boolean.class || type == boolean.class
                || type == String.class) {
            return value;
        } else if (type == Date.class) {
            if (value instanceof Date) {
                return value;
            }

            Assert.isTrue(value instanceof Long || value instanceof Integer || value instanceof String,
                "Date store value must be instance of long or int. Data store value is " + value.getClass().getName());

            if (value instanceof Integer) {
                final Integer integerValue = (Integer) value;
                return new Date(integerValue.longValue());
            } else if (value instanceof Long) {
                return new Date((Long) value);
            } else {
                final String strValue = (String) value;

                if (NULLHASH == value.hashCode()) {
                    return null;
                } else {
                    try {
                        return new Date(Long.parseLong(strValue));
                    } catch (NumberFormatException nfe) {
                        throw new GremlinUnexpectedEntityTypeException(
                            "Failed to read String to Datae with value " + strValue);
                    }
                }
            }
        } else {
            final Object object;

            final String strValue = value.toString();
            try {
                object = (NULLHASH == strValue.hashCode()) ? null :
                    GremlinUtils.getObjectMapper().readValue(strValue, javaType);
            } catch (Throwable e) {
                throw new GremlinUnexpectedEntityTypeException(
                    "Failed to read String to Object for property " +
                    property.getName() +
                    " and value " +
                        strValue,
                    e);
            }

            return object;
        }
    }

    protected Object getGremlinSourceId(@NonNull GremlinSource source) {
        if (!source.getId().isPresent()) {
            return null;
        }

        final Object id = source.getId().get();
        final Field idField = source.getIdField();

        if (idField.getType() == String.class) {
            return id.toString();
        } else if (idField.getType() == Integer.class) {
            Assert.isTrue(id instanceof Integer, "source Id should be Integer.");
            return id;
        } else if (idField.getType() == Long.class && id instanceof Integer) {
            return Long.valueOf((Integer) id);
        } else if (idField.getType() == Long.class && id instanceof Long) {
            return id;
        }

        throw new GremlinEntityInformationException("unsupported id field type: " + id.getClass().getSimpleName());
    }
}

