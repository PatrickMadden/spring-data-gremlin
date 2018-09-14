/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.script;

import com.microsoft.spring.data.gremlin.common.Constants;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceVertex;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedSourceTypeException;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.microsoft.spring.data.gremlin.common.Constants.*;
import static com.microsoft.spring.data.gremlin.common.GremlinEntityType.VERTEX;


@NoArgsConstructor
public class GremlinScriptLiteralVertex extends AbstractGremlinScriptLiteral implements GremlinScriptLiteral {
    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> generateInsertScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        final List<String> scriptList = new ArrayList<>();

        scriptList.add(GREMLIN_PRIMITIVE_GRAPH);                               // g
        scriptList.add(generateAddEntityWithLabel(source.getLabel(), VERTEX)); // addV('label')
        scriptList.add(generatePropertyWithRequiredId(source.getId()));        // property(id, xxx)

        scriptList.addAll(generateProperties(source.getProperties()));

        return completeScript(scriptList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> generateDeleteAllScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        return Collections.singletonList(Constants.GREMLIN_SCRIPT_VERTEX_DROP_ALL);
    }

    @Override
    public List<String> generateDeleteAllByClassScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        final List<String> scriptList = Arrays.asList(
                GREMLIN_PRIMITIVE_GRAPH,             // g
                GREMLIN_PRIMITIVE_VERTEX_ALL,        // V()
                generateHasLabel(source.getLabel()), // has(label, 'label')
                GREMLIN_PRIMITIVE_DROP               // drop()
        );

        return completeScript(scriptList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> generateFindByIdScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        final List<String> scriptList = Arrays.asList(
                GREMLIN_PRIMITIVE_GRAPH,                             // g
                generateEntityWithRequiredId(source.getId(), VERTEX) // V(id)
        );

        return completeScript(scriptList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> generateUpdateScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        final List<String> scriptList = new ArrayList<>();

        scriptList.add(GREMLIN_PRIMITIVE_GRAPH);                              // g
        scriptList.add(generateEntityWithRequiredId(source.getId(), VERTEX)); // V(id)

        scriptList.addAll(generateUpdateProperties(source.getProperties()));

        return completeScript(scriptList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> generateFindAllScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        final List<String> scriptList = Arrays.asList(
                GREMLIN_PRIMITIVE_GRAPH,            // g
                GREMLIN_PRIMITIVE_VERTEX_ALL,       // V()
                generateHasLabel(source.getLabel()) // has(label, 'label')
        );

        return completeScript(scriptList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> generateDeleteByIdScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        final List<String> scriptList = Arrays.asList(
                GREMLIN_PRIMITIVE_GRAPH,                              // g
                generateEntityWithRequiredId(source.getId(), VERTEX), // V(id)
                GREMLIN_PRIMITIVE_DROP                                // drop()
        );

        return completeScript(scriptList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateCountScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        return String.join(Constants.GREMLIN_PRIMITIVE_INVOKE,
            Constants.GREMLIN_SCRIPT_VERTEX_ALL,
            Constants.GREMLIN_PRIMITIVE_COUNT);
    }


    /**
     * Generate the Count query for a particular vertex label.
     * @param source The {@link GremlinSource} instane.
     * @return The query.
     */
    public String generateCountLabelScript(GremlinSource source) {
        if (!(source instanceof GremlinSourceVertex)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceVertex");
        }

        final String label = source.getLabel();
        final List<String> scriptList = new ArrayList<>();

        Assert.notNull(label, "label should not be null");

        scriptList.add(Constants.GREMLIN_PRIMITIVE_GRAPH);
        scriptList.add(Constants.GREMLIN_PRIMITIVE_VERTEX_ALL);
        scriptList.add(String.format(Constants.GREMLIN_PRIMITIVE_HAS_KEYWORD, Constants.PROPERTY_LABEL, label));
        scriptList.add(Constants.GREMLIN_PRIMITIVE_COUNT);

        final String query = String.join(Constants.GREMLIN_PRIMITIVE_INVOKE, scriptList);

        return query;
    }
}

