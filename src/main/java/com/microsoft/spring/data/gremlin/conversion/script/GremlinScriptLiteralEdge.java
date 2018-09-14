/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.conversion.script;

import com.microsoft.spring.data.gremlin.common.Constants;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceEdge;
import com.microsoft.spring.data.gremlin.exception.GremlinUnexpectedSourceTypeException;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.*;

import static com.microsoft.spring.data.gremlin.common.Constants.*;
import static com.microsoft.spring.data.gremlin.common.GremlinEntityType.EDGE;
import static com.microsoft.spring.data.gremlin.common.GremlinEntityType.VERTEX;

@NoArgsConstructor
public class GremlinScriptLiteralEdge extends AbstractGremlinScriptLiteral implements GremlinScriptLiteral {

    private static final String FROM_ALIAS = "from";
    private static final String TO_ALIAS = "to";

    private String generateEdgeDirection(@NonNull String from, @NonNull String to) {
        return String.format("from('%s').to('%s')", from, to);
    }

    @Override
    public List<String> generateInsertScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        final GremlinSourceEdge sourceEdge = (GremlinSourceEdge) source;
        final List<String> scriptList = new ArrayList<>();

        scriptList.add(GREMLIN_PRIMITIVE_GRAPH);                                            // g
        scriptList.add(generateEntityWithRequiredId(sourceEdge.getVertexIdFrom(), VERTEX)); // V(id)
        scriptList.add(generateAsWithAlias(FROM_ALIAS));                                    // from('from')
        scriptList.add(generateEntityWithRequiredId(sourceEdge.getVertexIdTo(), VERTEX));   // V(id)
        scriptList.add(generateAsWithAlias(TO_ALIAS));                                      // to('to')
        scriptList.add(generateAddEntityWithLabel(sourceEdge.getLabel(), EDGE));            // addE(label)
        scriptList.add(generateEdgeDirection(FROM_ALIAS, TO_ALIAS));                        // from('from').to('to')
        scriptList.add(generatePropertyWithRequiredId(source.getId()));                     // property(id, xxx)

        scriptList.addAll(generateProperties(source.getProperties()));

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateDeleteAllScript(@Nullable GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        return Collections.singletonList(Constants.GREMLIN_SCRIPT_EDGE_DROP_ALL);
    }

    @Override
    public List<String> generateDeleteAllByClassScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        final List<String> scriptList = Arrays.asList(
                GREMLIN_PRIMITIVE_GRAPH,             // g
                GREMLIN_PRIMITIVE_EDGE_ALL,          // E()
                generateHasLabel(source.getLabel()), // has(label, 'label')
                GREMLIN_PRIMITIVE_DROP               // drop()
        );

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateFindByIdScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        final List<String> scriptList = Arrays.asList(
                GREMLIN_PRIMITIVE_GRAPH,                           // g
                generateEntityWithRequiredId(source.getId(), EDGE) // E(id)
        );

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateUpdateScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        final List<String> scriptList = new ArrayList<>();

        scriptList.add(GREMLIN_PRIMITIVE_GRAPH);                            // g
        scriptList.add(generateEntityWithRequiredId(source.getId(), EDGE)); // E(id)

        scriptList.addAll(generateUpdateProperties(source.getProperties()));

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateFindAllScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        final List<String> scriptList = Arrays.asList(
                GREMLIN_PRIMITIVE_GRAPH,            // g
                GREMLIN_PRIMITIVE_EDGE_ALL,         // E()
                generateHasLabel(source.getLabel()) // has(label, 'label')
        );

        return completeScript(scriptList);
    }

    @Override
    public List<String> generateDeleteByIdScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        final List<String> scriptList = Arrays.asList(
                GREMLIN_PRIMITIVE_GRAPH,                            // g
                generateEntityWithRequiredId(source.getId(), EDGE), // E(id)
                GREMLIN_PRIMITIVE_DROP                              // drop()
        );

        return completeScript(scriptList);
    }

    @Override
    public String generateCountScript(@NonNull GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        return String.join(GREMLIN_PRIMITIVE_INVOKE,
            GREMLIN_SCRIPT_EDGE_ALL,
            GREMLIN_PRIMITIVE_COUNT);
    }


    /**
     * Generate the Count query for a particular edge label.
     * @param source The {@link GremlinSource} instane.
     * @return The query.
     */
    public String generateCountLabelScript(GremlinSource source) {
        if (!(source instanceof GremlinSourceEdge)) {
            throw new GremlinUnexpectedSourceTypeException("should be the instance of GremlinSourceEdge");
        }

        final List<String> scriptList = Arrays.asList(
            GREMLIN_PRIMITIVE_GRAPH,
            GREMLIN_PRIMITIVE_EDGE_ALL,
            generateHasLabel(source.getLabel()),
            GREMLIN_PRIMITIVE_COUNT
        );

        return String.join(Constants.GREMLIN_PRIMITIVE_INVOKE, scriptList);
    }


    /**
     * Edges properties do not support single cardinality. Use default properties instead of update properties.
     * @param properties The properties of interest.
     * @return The list of property literals.
     */
    @Override
    public List<String> generateUpdateProperties(@NonNull final Map<String, Object> properties) {
        final List<String> scripts = new ArrayList<>();

        properties.forEach((name, value) -> scripts.add(generateProperty(name, value)));

        return scripts;
    }
}

