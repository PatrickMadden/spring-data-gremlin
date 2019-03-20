/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.spring.data.gremlin.query;


import com.microsoft.spring.data.gremlin.common.GremlinEntityType;
import com.microsoft.spring.data.gremlin.conversion.MappingGremlinConverter;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import com.microsoft.spring.data.gremlin.query.query.GremlinQuery;
import com.microsoft.spring.data.gremlin.repository.support.GremlinEntityInformation;
import org.springframework.lang.NonNull;
import java.util.List;


/**
 * Provider interface for basic Operations with Gremlin
 */
public interface GremlinOperations
{

    void deleteAll();


    void deleteAll(GremlinEntityType type);


    <T> void deleteAll(GremlinSource<T> source);


    default <T> void deleteAll(Class<T> domainClass) {
        final GremlinSource<T> source =
            GremlinEntityInformation.get(domainClass).createGremlinSource();

        deleteAll(source);
    }


    <T> boolean isEmptyGraph(GremlinSource<T> source);


    <T> boolean existsById(Object id, GremlinSource<T> source);


    default <T> boolean existsById(Object id, Class<T> domainClass) {
        final GremlinSource<T> source =
            GremlinEntityInformation.get(domainClass).createGremlinSource();

        return existsById(id, source);
    }


    <T> void deleteById(Object id, GremlinSource<T> source);


    default <T> void deleteById(Object id, Class<T> domainClass) {
        final GremlinSource<T> source =
            GremlinEntityInformation.get(domainClass).createGremlinSource();

        deleteById(id, source);
    }


    <T> T insert(T object, GremlinSource<T> source);


    default <T> T insert(T object, Class<T> domainClass) {
        final GremlinEntityInformation entityInformation =
            GremlinEntityInformation.get(domainClass);
        final GremlinSource<T> source =
            entityInformation.createGremlinSource();
        source.setId(entityInformation.getId(object));

        return insert(object, source);
    }


    <T> T findById(Object id, GremlinSource<T> source);


    default <T> T findById(Object id, Class<T> domainClass) {
        final GremlinSource<T> source =
            GremlinEntityInformation.get(domainClass).createGremlinSource();

        return findById(id, source);
    }


    <T> T findVertexById(Object id, GremlinSource<T> source);


    default <T> T findVertexById(Object id, Class<T> domainClass) {
        final GremlinSource<T> source =
            GremlinEntityInformation.get(domainClass).createGremlinSource();

        return findVertexById(id, source);
    }


    <T> T findEdgeById(Object id, GremlinSource<T> source);


    default <T> T findEdgeById(Object id, Class<T> domainClass) {
        final GremlinEntityInformation entityInformation =
            GremlinEntityInformation.get(domainClass);
        final GremlinSource<T> source =
            entityInformation.createGremlinSource();

        return findEdgeById(id, source);
    }


    <T> T update(T object, GremlinSource<T> source);


    default <T> T update(T object, Class<T> domainClass) {
        final GremlinEntityInformation entityInformation =
            GremlinEntityInformation.get(domainClass);
        final GremlinSource<T> source =
            entityInformation.createGremlinSource();
        source.setId(entityInformation.getId(object));

        return update(object, source);
    }


    <T> T save(T object, GremlinSource<T> source);


    default <T> T save(T object) {

        return save(object, (Class<T>) object.getClass());
    }


    default <T> T save(T object, Class<T> domainClass) {
        final GremlinEntityInformation entityInformation =
            GremlinEntityInformation.get(domainClass);
        final GremlinSource<T> source =
            entityInformation.createGremlinSource();
        source.setId(entityInformation.getId(object));


        return save(object, source);
    }


    <T> List<T> findAll(GremlinSource<T> source);


    default <T> List<T> findAll(Class<T> domainClass) {
        final GremlinSource<T> source =
            GremlinEntityInformation.get(domainClass).createGremlinSource();

        return findAll(source);
    }


    long vertexCount();


    <T> long vertexCount(Class<T> domainClass);


    long edgeCount();


    <T> long edgeCount(Class<T> domainClass);


    <T> List<T> find(GremlinQuery query, GremlinSource<T> source);


    default <T> List<T> find(GremlinQuery query, Class<T> domainClass) {
        final GremlinSource<T> source =
            GremlinEntityInformation.get(domainClass).createGremlinSource();

        return find(query, source);
    }

    <T> List<T> find(@NonNull List<String> queryList, GremlinSource<T> source);


    default <T> List<T> find(@NonNull List<String> queryList, Class<T> domainClass) {
        final GremlinSource<T> source =
            GremlinEntityInformation.get(domainClass).createGremlinSource();

        return find(queryList, source);
    }


    <T> List<T> saveAll(List<T> objects);


    MappingGremlinConverter getMappingConverter();
}
