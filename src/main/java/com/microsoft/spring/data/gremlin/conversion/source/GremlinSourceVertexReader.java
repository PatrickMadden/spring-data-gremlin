/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.source;


import com.microsoft.spring.data.gremlin.common.Constants;
import com.microsoft.spring.data.gremlin.common.GremlinUtils;
import com.microsoft.spring.data.gremlin.conversion.MappingGremlinConverter;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedSourceTypeException;
import com.microsoft.spring.data.gremlin.mapping.GremlinPersistentEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import java.lang.reflect.Field;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GremlinSourceVertexReader extends AbstractGremlinSourceReader implements GremlinSourceReader {

    @Override
    public <T extends Object> T read(@NonNull Class<T> type, @NonNull MappingGremlinConverter converter,
                                     @NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be instance of GremlinSourceVertex");
        }

        final T domain = GremlinUtils.createInstance(type);
        final ConvertingPropertyAccessor accessor = converter.getPropertyAccessor(domain);
        final GremlinPersistentEntity persistentEntity = converter.getPersistentEntity(type);

        for (final Field field : converter.getAllFields(type)) {
            final PersistentProperty property = persistentEntity.getPersistentProperty(field.getName());

            if (property != null) {
                Assert.notNull(property, "persistence property should not be null");

                if (field.getName().equals(Constants.PROPERTY_ID) ||
                    field.getAnnotation(Id.class) != null) {
                    accessor.setProperty(property, source.getId());
                } else {
                    final Object sourceValue = source.getProperties().get(field.getName());
                    accessor.setProperty(property,
                        sourceValue != null ? super.readProperty(property, sourceValue) : null);
                }
            }
        }

        return domain;
    }
}

