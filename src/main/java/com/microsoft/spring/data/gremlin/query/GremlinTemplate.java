/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.spring.data.gremlin.query;


import com.microsoft.spring.data.gremlin.annotation.EdgeFrom;
import com.microsoft.spring.data.gremlin.annotation.EdgeTo;
import com.microsoft.spring.data.gremlin.annotation.GeneratedValue;
import com.microsoft.spring.data.gremlin.common.GremlinEntityType;
import com.microsoft.spring.data.gremlin.common.GremlinFactory;
import com.microsoft.spring.data.gremlin.common.GremlinUtils;
import com.microsoft.spring.data.gremlin.conversion.MappingGremlinConverter;
import com.microsoft.spring.data.gremlin.conversion.script.GremlinScriptLiteral;
import com.microsoft.spring.data.gremlin.conversion.script.GremlinScriptLiteralEdge;
import com.microsoft.spring.data.gremlin.conversion.script.GremlinScriptLiteralGraph;
import com.microsoft.spring.data.gremlin.conversion.script.GremlinScriptLiteralVertex;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceEdge;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceGraph;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceVertex;
import com.microsoft.spring.data.gremlin.exception.GremlinEntityInformationException;
import com.microsoft.spring.data.gremlin.exception.GremlinInvalidEntityIdFieldException;
import com.microsoft.spring.data.gremlin.exception.GremlinQueryException;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedEntityTypeException;
import com.microsoft.spring.data.gremlin.mapping.GremlinPersistentEntity;
import com.microsoft.spring.data.gremlin.query.query.GremlinQuery;
import com.microsoft.spring.data.gremlin.query.query.QueryFindScriptGenerator;
import com.microsoft.spring.data.gremlin.query.query.QueryScriptGenerator;
import com.microsoft.spring.data.gremlin.repository.support.GremlinEntityInformation;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.model.ConvertingPropertyAccessor;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


public class GremlinTemplate implements GremlinOperations, ApplicationContextAware {

    private final GremlinFactory factory;
    private final MappingGremlinConverter mappingConverter;

    private Client gremlinClient;
    private ApplicationContext context;

    //static final int numberOfVirtualCPUs = 4;
    static final ExecutorService executor = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors());

    static Logger logger = LoggerFactory.getLogger(GremlinTemplate.class);

    Map<Object, Object> idToDomainVertices = new ConcurrentHashMap<>();
    Map<Object, Object> idToDomainEdges = new ConcurrentHashMap<>();


    public GremlinTemplate(@NonNull GremlinFactory factory, @NonNull MappingGremlinConverter converter) {
        this.factory = factory;
        this.mappingConverter = converter;
    }

    @Override
    public MappingGremlinConverter getMappingConverter() {
        return this.mappingConverter;
    }

    public ApplicationContext getApplicationContext() {
        return this.context;
    }

    public void clearDomainCache() {
        this.idToDomainEdges.clear();
        this.idToDomainVertices.clear();
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext context) throws BeansException {
        this.context = context;
    }

    public Client getGremlinClient() {
        if (this.gremlinClient == null) {
            this.gremlinClient = this.factory.getGremlinClient();
        }

        return this.gremlinClient;
    }

    @NonNull
    private List<Result> executeQuery(@NonNull List<String> queries) {
        return executeQueryUsingExecutorService(queries);
//        final List<List<String>> parallelQueries = GremlinUtils.toParallelQueryList(queries);
//
//        return parallelQueries.stream().flatMap(q -> executeQueryExecutor(q).stream()).collect(Collectors.toList());
    }

//    @NonNull
//    private List<Result> executeQueryParallel(@NonNull List<String> queries) {
//        return queries.parallelStream()
//            .map(q -> getGremlinClient().submit(q).all())
//            .collect(toList()).parallelStream().flatMap(f -> {
//                try {
//                    return f.get().stream();
//                } catch (InterruptedException | ExecutionException e) {
//                    throw new GremlinQueryException("unable to complete query from gremlin", e);
//                }
//            })
//            .collect(toList());
//    }



    protected List<Result> executeQueryUsingExecutorService(@NonNull List<String> queries) {
        final List<CompletableFuture<List<Result>>> futures =
            queries.stream()
                .map(query -> CompletableFuture.supplyAsync(() ->
                {
                    logger.info("Thread: {}, Executing Query: {}",
                        Thread.currentThread().getName(),
                        query);
                    return this.getGremlinClient().submit(query).all().join();
                }, executor))
                .collect(Collectors.toList());

        final List<List<Result>> collect =
            futures.stream().map(CompletableFuture::join)
                .collect(Collectors.toList());

        final List<Result> results = new ArrayList<>(queries.size());
        collect.forEach(resultList -> results.addAll(resultList));

        return results;
    }

    /**
     * This method execuates a query expected to return a long value.
     * @param query The query such as a count query.
     * @return The long value of the {@link Result} returned from the query.
     */
    public long executeLongQuery(@NonNull String query) {
        try
        {
            final Result result = this.getGremlinClient().submit(query).one();
            return result.getLong();
        }
        catch (Throwable e)
        {
            throw new GremlinQueryException(String
                .format("unable to complete execute %s from gremlin", query), e);
        }
    }

    @Override
    public void deleteAll() {
        final GremlinScriptLiteral script = new GremlinScriptLiteralGraph();
        final List<String> queryList = script.generateDeleteAllScript();

        executeQuery(queryList);
    }

    @Override
    public void deleteAll(GremlinEntityType type) {
        final GremlinSource source = type.createGremlinSource();

        executeQuery(source.getGremlinScriptLiteral().generateDeleteAllScript());
    }

    @Override
    public <T> void deleteAll(GremlinSource<T> source) {
        executeQuery(source.getGremlinScriptLiteral().generateDeleteAllByClassScript(source));
    }

    public <T> void deleteAll(@NonNull Class<T> domainClass) {
        @SuppressWarnings("unchecked") final GremlinEntityInformation info = GremlinEntityInformation.get(domainClass);
        final GremlinSource source = info.getGremlinSource();
        final List<String> queryList = source.getGremlinScriptLiteral().generateDeleteAllByClassScript(source);

        this.executeQuery(queryList);
    }

    private <T> List<Result> insertInternal(@NonNull T object, @NonNull GremlinSource<T> source) {
        this.mappingConverter.write(object, source);

        return executeQuery(source.getGremlinScriptLiteral().generateInsertScript(source));
    }


    @Override
    public <T> T insert(@NonNull T object, GremlinSource<T> source) {
        final boolean entityGraph = source instanceof GremlinSourceGraph;

        if (!entityGraph && source.getIdField().isAnnotationPresent(GeneratedValue.class)
            && source.getId().isPresent()) {
            throw new GremlinInvalidEntityIdFieldException("The entity meant to be created has a non-null id "
                + "that is marked as @GeneratedValue");
        }

        // The current implementation doesn't support creating graphs that contain both edges
        // and vertices that have null (generated) ids. In this case, vertex and edge creation
        // need to be performed in two consecutive steps.
        // TODO(SOON) Add this verification in the GremlinSourceGraphWriter

        final List<Result> results = insertInternal(object, source);

        if (!results.isEmpty()) {
            if (entityGraph) {
                return recoverGraphDomain((GremlinSourceGraph<T>) source, results);
            } else {
                return recoverDomain(source, results);
            }
        }

        return null;
    }

    @Override
    public <T> T findVertexById(@NonNull Object id, GremlinSource<T> source) {
        if (source instanceof GremlinSourceVertex) {
            source.setId(id);
            return this.findByIdInternal(source);
        }

        throw new GremlinUnexpectedEntityTypeException("should be vertex domain for findVertexById");
    }

    private Object getEdgeAnnotatedFieldValue(@NonNull Field field, @NonNull Object vertexId) {
        if (field.getType() == String.class || field.getType() == Long.class || field.getType() == Integer.class) {
            return vertexId;
        } else if (field.getType().isPrimitive()) {
            throw new GremlinUnexpectedEntityTypeException("only String/Long/Integer type of Id Field is allowed");
        } else {
            return this.findVertexById(vertexId, GremlinUtils.toGremlinSource(field.getType()));
        }
    }

    @NonNull
    private Field getEdgeAnnotatedField(@NonNull Class<?> domainClass,
        @NonNull Class<? extends Annotation> annotationClass) {
        final List<Field> fields = FieldUtils.getFieldsListWithAnnotation(domainClass, annotationClass);

        if (fields.size() != 1) {
            throw new GremlinEntityInformationException("should be only one Annotation");
        }

        return fields.get(0);
    }

    /**
     * Find Edge need another two query to obtain edgeFrom and edgeTo.
     * This function will do that and make edge domain completion.
     */
    protected <T> void completeEdge(@NonNull T domain, @NonNull GremlinSourceEdge source) {
        final Object cachedSourceVertex = this.idToDomainVertices.get(source.getVertexIdFrom());
        final Object cachedTargetVertex = this.idToDomainVertices.get(source.getVertexIdTo());

        final ConvertingPropertyAccessor accessor = this.mappingConverter.getPropertyAccessor(domain);
        final GremlinPersistentEntity persistentEntity = this.mappingConverter.getPersistentEntity(domain.getClass());

        final Field fromField = this.getEdgeAnnotatedField(domain.getClass(), EdgeFrom.class);
        final Field toField = this.getEdgeAnnotatedField(domain.getClass(), EdgeTo.class);

        final PersistentProperty propertyFrom = persistentEntity.getPersistentProperty(fromField.getName());
        final PersistentProperty propertyTo = persistentEntity.getPersistentProperty(toField.getName());

        Assert.notNull(propertyFrom, "persistence property should not be null");
        Assert.notNull(propertyTo, "persistence property should not be null");

        accessor.setProperty(propertyFrom, cachedSourceVertex == null ?
            this.getEdgeAnnotatedFieldValue(fromField, source.getVertexIdFrom()) : cachedSourceVertex);
        accessor.setProperty(propertyTo, cachedTargetVertex == null ?
            this.getEdgeAnnotatedFieldValue(toField, source.getVertexIdTo()) : cachedTargetVertex);
    }

    @Override
    public <T> T findEdgeById(@NonNull Object id, @NonNull GremlinSource<T> source) {
        if (source instanceof GremlinSourceEdge) {
            return this.findById(id, source);
        }

        throw new GremlinUnexpectedEntityTypeException("should be edge domain for findEdge");
    }

    private <T> T findByIdInternal(@NonNull GremlinSource<T> source) {
        final Object cachedInstance;

        final Object id = source.getId().get();

        if (source instanceof GremlinSourceEdge) {
            cachedInstance = idToDomainEdges.get(id);
        }
        else if (source instanceof GremlinSourceVertex) {
            cachedInstance = idToDomainVertices.get(id);
        }
        else {
            cachedInstance = null;
        }

        if (cachedInstance == null)
        {
            final List<String> queryList =
                source.getGremlinScriptLiteral().generateFindByIdScript(source);
            final List<Result> results = this.executeQuery(queryList);

            if (results.isEmpty())
            {
                return null;
            }

            return recoverDomain(source, results);
        }
        else
        {
            return (T) cachedInstance;
        }
    }

    @Override
    public <T> T findById(@NonNull Object id, @NonNull GremlinSource<T> source) {
        if (source instanceof GremlinSourceGraph) {
            throw new UnsupportedOperationException("Gremlin graph cannot be findById.");
        }

        source.setId(id);

        return findByIdInternal(source);
    }


    private <T> T updateInternal(@NonNull T object, @NonNull GremlinSource<T> source) {
        this.mappingConverter.write(object, source);

        final List<String> queryList = source.getGremlinScriptLiteral().generateUpdateScript(source);

        executeQuery(queryList);

        return object;
    }


    @Override
    public <T> T update(@NonNull T object, @NonNull GremlinSource<T> source) {
        final Optional<Object> optional = source.getId();

        if (!(source instanceof GremlinSourceGraph)
            && (!optional.isPresent() || notExistsById(optional.get(), source))) {
            throw new GremlinQueryException("cannot update the object doesn't exist");
        }

        return this.updateInternal(object, source);
    }

    @Override
    public <T> T save(@NonNull T object, @NonNull GremlinSource<T> source) {
        final Optional<Object> optional = source.getId();
        final boolean entityGraph = source instanceof GremlinSourceGraph;

        if (entityGraph && this.isEmptyGraph(source)) {
            return insert(object, source);
        } else if (!entityGraph && (!optional.isPresent() || notExistsById(optional.get(), source))) {
            return insert(object, source);
        } else {
            return updateInternal(object, source);
        }
    }

    @Override
    public <T> List<T> findAll(@NonNull GremlinSource<T> source) {
        if (source instanceof GremlinSourceGraph) {
            throw new UnsupportedOperationException("Gremlin graph cannot be findAll.");
        }

        final List<String> queryList = source.getGremlinScriptLiteral().generateFindAllScript(source);
        final List<Result> results = executeQuery(queryList);

        if (results.isEmpty()) {
            return Collections.emptyList();
        }

        return recoverDomainList(source, results);
    }

    @Override
    public <T> void deleteById(@NonNull Object id, @NonNull GremlinSource<T> source) {
        source.setId(id);

        final List<String> queryList = source.getGremlinScriptLiteral().generateDeleteByIdScript(source);

        executeQuery(queryList);
    }

    @Override
    public <T> boolean isEmptyGraph(@NonNull GremlinSource<T> source) {
        if (source instanceof GremlinSourceGraph) {
            final GremlinScriptLiteralGraph literalGraph = (GremlinScriptLiteralGraph) source.getGremlinScriptLiteral();
            final List<String> queryList = literalGraph.generateIsEmptyScript();
            final List<Result> results = this.executeQuery(queryList);

            return results.isEmpty();
        }

        throw new GremlinQueryException("only graph domain is allowed.");
    }

    @Override
    public long vertexCount() {
        final GremlinScriptLiteral script = new GremlinScriptLiteralVertex();
        final List<String> queryList = script.generateCountScript(new GremlinSourceVertex());
        final List<Result> results = this.executeQuery(queryList);

        return results.size();
    }

    @Override
    public long edgeCount() {
        final GremlinScriptLiteral script = new GremlinScriptLiteralEdge();
        final List<String> queryList = script.generateCountScript(new GremlinSourceEdge());
        final List<Result> results = this.executeQuery(queryList);

        return results.size();
    }


    @Override
    public <T> long vertexCount(Class<T> domainClass) {
        final GremlinEntityInformation info = GremlinEntityInformation.get(domainClass);
        final GremlinSource source = info.getGremlinSource();

        final GremlinScriptLiteralVertex script = new GremlinScriptLiteralVertex();
        final List<String> query = script.generateCountLabelScript(source);

        return this.executeLongQuery(query.get(0));
    }


    @Override
    public <T> long edgeCount(Class<T> domainClass) {
        final GremlinEntityInformation info = GremlinEntityInformation.get(domainClass);
        final GremlinSource source = info.getGremlinSource();

        final GremlinScriptLiteralEdge script = new GremlinScriptLiteralEdge();
        final List<String> query = script.generateCountLabelScript(source);

        return this.executeLongQuery(query.get(0));
    }



    protected <T> T recoverDomain(@NonNull GremlinSource<T> source, @NonNull List<Result> results) {
        final T domain;
        final Class<T> domainClass = source.getDomainClass();

        source.doGremlinResultRead(results);
        domain = this.mappingConverter.read(domainClass, source);

        if (source instanceof GremlinSourceEdge) {
            this.completeEdge(domain, (GremlinSourceEdge) source);
            idToDomainEdges.put(source.getId().get(), domain);
        } else if (source instanceof GremlinSourceVertex) {
            idToDomainVertices.put(source.getId().get(), domain);
        }

        return domain;
    }

    protected <T> List<T> recoverDomainList(@NonNull GremlinSource<T> source, @NonNull List<Result> results) {
        return results.stream().map(r -> recoverDomain(source, Collections.singletonList(r))).collect(toList());
    }

    protected <T> T recoverGraphDomain(@NonNull GremlinSourceGraph<T> source, @NonNull List<Result> results) {
        final T domain;
        final Class<T> domainClass = source.getDomainClass();

        source.getResultsReader().read(results, source);
        domain = source.doGremlinSourceRead(domainClass, mappingConverter);
        return domain;
    }

    protected <T> boolean notExistsById(@NonNull Object id, @NonNull GremlinSource<T> source) {
        return !existsById(id, source);
    }

    @Override
    public <T> boolean existsById(@NonNull Object id, @NonNull GremlinSource<T> source) {
        return findById(id, source) != null;
    }

    @Override
    public <T> List<T> find(@NonNull GremlinQuery query, @NonNull GremlinSource<T> source) {
        final QueryScriptGenerator generator = new QueryFindScriptGenerator(source);
        final List<String> queryList = generator.generate(query);
        final List<Result> results = this.executeQuery(queryList);

        if (results.isEmpty()) {
            return Collections.emptyList();
        }

        return this.recoverDomainList(source, results);
    }
}

