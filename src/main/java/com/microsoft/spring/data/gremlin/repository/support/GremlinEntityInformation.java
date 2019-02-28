/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.repository.support;


import com.microsoft.spring.data.gremlin.annotation.Edge;
import com.microsoft.spring.data.gremlin.annotation.GeneratedValue;
import com.microsoft.spring.data.gremlin.annotation.Graph;
import com.microsoft.spring.data.gremlin.annotation.Vertex;
import com.microsoft.spring.data.gremlin.common.GremlinEntityType;
import com.microsoft.spring.data.gremlin.common.GremlinUtils;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceEdge;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceGraph;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceVertex;
import com.microsoft.spring.data.gremlin.exception.GremlinInvalidEntityIdFieldException;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedEntityTypeException;
import org.springframework.data.repository.core.support.AbstractEntityInformation;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;

public class GremlinEntityInformation<T, ID> extends AbstractEntityInformation<T, ID> {

    @Getter
    private final Field idField;

    @Getter
    private String entityLabel;

    @Getter
    private GremlinEntityType entityType;

    @Getter
    private GremlinSource<T> gremlinSource;

    private static Map<Class, GremlinEntityInformation> entityInformationMap =
        new ConcurrentHashMap<>();

    public static GremlinEntityInformation get(@NonNull Class domainClass) {
        GremlinEntityInformation gremlinEntityInformation =
            entityInformationMap.get(domainClass);

        if (gremlinEntityInformation == null) {
            gremlinEntityInformation = new GremlinEntityInformation(domainClass);

            entityInformationMap.put(domainClass, gremlinEntityInformation);
        }

        return gremlinEntityInformation;
    }

    protected GremlinEntityInformation(@NonNull Class<T> domainClass) {
        super(domainClass);

        this.idField = this.getIdField(domainClass);
        ReflectionUtils.makeAccessible(this.idField);

        this.entityType = this.getGremlinEntityType(domainClass); // The other fields getter may depend on type
        this.gremlinSource = this.createGremlinSource(domainClass, this.idField);
    }

    public boolean isEntityEdge() {
        return this.getEntityType() == GremlinEntityType.EDGE;
    }

    public boolean isEntityVertex() {
        return this.getEntityType() == GremlinEntityType.VERTEX;
    }

    public boolean isEntityGraph() {
        return this.getEntityType() == GremlinEntityType.GRAPH;
    }

    @Override
    @Nullable
    public ID getId(T entity) {
        final Field idField = this.idField;
        @SuppressWarnings("unchecked") final ID id = (ID) ReflectionUtils.getField(idField, entity);

        if (id == null && !(super.getJavaType().isAnnotationPresent(Graph.class))
                && !idField.isAnnotationPresent(GeneratedValue.class)) {
            throw new GremlinInvalidEntityIdFieldException("A non-generated id field cannot be null!");
        }
        return id;
    }

    @Override
    public Class<ID> getIdType() {
        @SuppressWarnings("unchecked") final Class<ID> idClass = (Class<ID>) this.idField.getType();

        return idClass;
    }

    @NonNull
    private Field getIdField(@NonNull Class<T> domainClass) {
        final Field idField = GremlinUtils.getIdField(domainClass);

        ReflectionUtils.makeAccessible(idField);

        return idField;
    }

    private GremlinEntityType getGremlinEntityType(@NonNull Class<?> domainClass) {
        final Vertex vertexAnnotation = domainClass.getAnnotation(Vertex.class);

        if (vertexAnnotation != null) {
            return GremlinEntityType.VERTEX;
        }

        final Edge edgeAnnotation = domainClass.getAnnotation(Edge.class);

        if (edgeAnnotation != null) {
            return GremlinEntityType.EDGE;
        }

        final Graph graphAnnotation = domainClass.getAnnotation(Graph.class);

        if (graphAnnotation != null) {
            return GremlinEntityType.GRAPH;
        }

        throw new GremlinUnexpectedEntityTypeException("Unexpected gremlin entity type\"");
    }


    private GremlinSource<T> createGremlinSource(@NonNull Class<T> domainClass, @NonNull Field idField) {
        final String domainClassName = domainClass.getSimpleName();
        final Vertex vertex = domainClass.getAnnotation(Vertex.class);
        final Edge edge = domainClass.getAnnotation(Edge.class);
        final Graph graph = domainClass.getAnnotation(Graph.class);
        final GremlinSource<T> source;

        if (vertex != null && edge == null && graph == null) {
            source = new GremlinSourceVertex<>(domainClass);
            entityLabel = vertex.label().isEmpty() ? domainClassName : vertex.label();
        } else if (edge != null && vertex == null && graph == null) {
            source = new GremlinSourceEdge<>(domainClass);
            entityLabel = edge.label().isEmpty() ? domainClassName : edge.label();
        } else if (graph != null && vertex == null && edge == null) {
            source = new GremlinSourceGraph<>(domainClass);
            entityLabel = "";
        } else {
            throw new GremlinUnexpectedEntityTypeException("Unexpected gremlin entity type");
        }

        source.setLabel(entityLabel);
        source.setIdField(idField);

        return source;
    }
}

