/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.source;


import com.microsoft.spring.data.gremlin.common.Constants;
import com.microsoft.spring.data.gremlin.conversion.MappingGremlinConverter;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedSourceTypeException;
import com.microsoft.spring.data.gremlin.mapping.GremlinPersistentEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;
import org.springframework.lang.NonNull;
import java.lang.reflect.Field;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GremlinSourceVertexWriter implements GremlinSourceWriter {

    @Override
    public void write(@NonNull Object domain, @NonNull MappingGremlinConverter converter,
                      @NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        source.setId(converter.getIdFieldValue(domain));

        final GremlinPersistentEntity<?> persistentEntity = converter.getPersistentEntity(domain.getClass());
        final ConvertingPropertyAccessor accessor = converter.getPropertyAccessor(domain);

        for (final Field field : converter.getAllFields(domain.getClass())) {
            final PersistentProperty property = persistentEntity.getPersistentProperty(field.getName());

            // If we add transient checks when building, it is possible for persistent property to be null.
            if (property != null) {
                //Assert.notNull(property, field.getName() + " persistence property should not be null");

                if (field.getName().equals(Constants.PROPERTY_ID) ||
                    field.getAnnotation(Id.class) != null) {
                    continue;
                }

                source.setProperty(field.getName(), accessor.getProperty(property));
            }
        }
    }
}

