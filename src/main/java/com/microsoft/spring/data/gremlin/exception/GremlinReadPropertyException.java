/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.exception;


import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.util.ObjectUtils;


/**
 * An exception to indicate there was a failure reading a property from the database.
 */
public class GremlinReadPropertyException extends NonTransientDataAccessException {
    public GremlinReadPropertyException(Class domainClass,
        PersistentProperty property,
        Object value,
        Throwable cause) {
        super("Failed to read property [" + property.getName() + "] with value [" +
            ObjectUtils.nullSafeToString(value) + "] from class [" +
            domainClass.getName() + "]",
            cause);
    }
}
