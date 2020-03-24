/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.source;

import com.microsoft.spring.data.gremlin.common.Constants;
import com.microsoft.spring.data.gremlin.common.GremlinUtils;
import com.microsoft.spring.data.gremlin.conversion.MappingGremlinConverter;
import com.microsoft.spring.data.gremlin.exception.GremlinReadPropertyException;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedSourceTypeException;
import com.microsoft.spring.data.gremlin.mapping.GremlinPersistentEntity;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.data.annotation.Id;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;
import org.springframework.lang.NonNull;

import java.lang.reflect.Field;

@NoArgsConstructor
public class GremlinSourceVertexReader extends AbstractGremlinSourceReader implements GremlinSourceReader {

    @Override
    public <T extends Object> T read(@NonNull Class<T> domainClass, @NonNull MappingGremlinConverter converter,
                                     @NonNull GremlinSource<T> source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be instance of GremlinSourceVertex");
        }

        final T domain = GremlinUtils.createInstance(domainClass);
        final ConvertingPropertyAccessor accessor = converter.getPropertyAccessor(domain);
        final GremlinPersistentEntity persistentEntity = converter.getPersistentEntity(domainClass);

        for (final Field field : converter.getAllFields(domainClass)) {
            final PersistentProperty property = persistentEntity.getPersistentProperty(field.getName());

            if (property != null) {
                if (field.getName().equals(Constants.PROPERTY_ID) ||
                    field.getAnnotation(Id.class) != null) {
                    accessor.setProperty(property, super.getGremlinSourceId(source));
                } else {
                    final Object sourceValue =
                        source.getProperties().get(field.getName());
                    try {
                        accessor.setProperty(property,
                            sourceValue == null ||
                                "null".equalsIgnoreCase(sourceValue.toString()) ? null :
                                super.readProperty(property, sourceValue));
                    } catch (ConversionFailedException cfe) {
                        throw new GremlinReadPropertyException(
                            domainClass,
                            property,
                            sourceValue,
                            cfe);
                    }
                }
            }
        }

        return domain;
    }
}

