/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query.query;


import com.microsoft.spring.data.gremlin.common.GremlinUtils;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSource;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceEdge;
import com.microsoft.spring.data.gremlin.conversion.source.GremlinSourceVertex;
import com.microsoft.spring.data.gremlin.query.criteria.Criteria;
import com.microsoft.spring.data.gremlin.query.criteria.CriteriaType;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.microsoft.spring.data.gremlin.common.Constants.*;
import static com.microsoft.spring.data.gremlin.conversion.script.AbstractGremlinScriptLiteral.*;

public class QueryFindScriptGenerator implements QueryScriptGenerator {

    private final GremlinSource source;

    public QueryFindScriptGenerator(@NonNull GremlinSource source) {
        this.source = source;
    }

    public String getCriteriaSubject(@NonNull Criteria criteria) {
        String subject = criteria.getSubject();

        if (subject.equals(this.source.getIdField().getName())) {
            subject = PROPERTY_ID; // If subject is @Id/id field, use id property in database.
        }

        return subject;
    }

    public String generateIsEqual(@NonNull Criteria criteria) {
        final String subject = getCriteriaSubject(criteria);

        if (subject.equals(PROPERTY_ID)) {
            return String.format(GREMLIN_PRIMITIVE_WHERE, generateHasId(criteria.getSubValues().get(0)));
        } else {
            return String.format(GREMLIN_PRIMITIVE_WHERE, generateHas(subject, criteria.getSubValues().get(0)));
        }
    }

    /**
     * Generate script with only one subject and no subValue, like findByActiveExists().
     *
     * @param criteria given query represent a query subject
     * @return simple script with keyword from criteria type
     */
    public String generateEmptyScript(@NonNull Criteria criteria) {
        final String subject = this.getCriteriaSubject(criteria);
        final String has = generateHas(subject, true);

        return String.format(GREMLIN_PRIMITIVE_WHERE, has);
    }

    /**
     * Generate script with only one subject and only one subValue, like findByCreateAtBefore(Date start).
     *
     * @param criteria given query represent a query subject
     * @return simple script with keyword from criteria type
     */
    public String generateSingleScript(@NonNull Criteria criteria) {
        final CriteriaType type = criteria.getType();
        final String subject = this.getCriteriaSubject(criteria);
        final long milliSeconds = GremlinUtils.timeToMilliSeconds(criteria.getSubValues().get(0));

        final String values = String.format(GREMLIN_PRIMITIVE_VALUES, subject);
        final String query = String.format(CriteriaType.criteriaTypeToGremlin(type), milliSeconds);
        final String content = String.join(GREMLIN_PRIMITIVE_INVOKE, values, query);

        return String.format(GREMLIN_PRIMITIVE_WHERE, content);
    }

    /**
     * Generate script with only one subject and two subValue, like findByCreateAtBetween(Date start, Date end).
     *
     * @param criteria given query represent a query subject
     * @return simple script with keyword from criteria type
     */
    public String generateDoubleScript(Criteria criteria) {
        final CriteriaType type = criteria.getType();
        final String subject = this.getCriteriaSubject(criteria);
        final long start = GremlinUtils.toPrimitiveLong(criteria.getSubValues().get(0));
        final long end = GremlinUtils.toPrimitiveLong(criteria.getSubValues().get(1));

        final String values = String.format(GREMLIN_PRIMITIVE_VALUES, subject);
        final String query = String.format(CriteriaType.criteriaTypeToGremlin(type), start, end);
        final String content = String.join(GREMLIN_PRIMITIVE_INVOKE, values, query);

        return String.format(GREMLIN_PRIMITIVE_WHERE, content);
    }

    /**
     * Generate script combined by AND/OR keyword.
     *
     * @param left  sub script on left
     * @param right sub script on right
     * @param type  should be AND/OR
     * @return combined script with AND/OR
     */
    public String generateCombinedScript(@NonNull String left, @NonNull String right, CriteriaType type) {
        final String operation = CriteriaType.criteriaTypeToGremlin(type);
        final String content = String.join(GREMLIN_PRIMITIVE_INVOKE, left, operation, right);

        return String.format(GREMLIN_PRIMITIVE_WHERE, content);
    }


    public String generateScriptTraversal(@NonNull Criteria criteria) {
        final CriteriaType type = criteria.getType();

        switch (type) {
            case IS_EQUAL:
                return this.generateIsEqual(criteria);
            case NOT:
                return this.generateNot(criteria);
            case AND:
                if (criteria.getSubCriteria().size() == 2) {
                    final String left =
                        this.generateScriptTraversal(criteria.getSubCriteria().get(0));
                    final String right =
                        this.generateScriptTraversal(criteria.getSubCriteria().get(1));

                    return this.generateCombinedScript(left, right, type);
                } else {
                    return generateAnd(criteria);
                }
            case OR:
                if (criteria.getSubCriteria().size() == 2) {
                    final String left =
                        this.generateScriptTraversal(criteria.getSubCriteria().get(0));
                    final String right =
                        this.generateScriptTraversal(criteria.getSubCriteria().get(1));

                    return this.generateCombinedScript(left, right, type);
                } else {
                    return generateOr(criteria);
                }
            case AFTER:
            case BEFORE:
                return this.generateSingleScript(criteria);
            case BETWEEN:
                return this.generateDoubleScript(criteria);
            case EXISTS:
                return this.generateEmptyScript(criteria);
            case INV:
                return this.generateInVScript(criteria);
            case OUTV:
                return this.generateOutVScript(criteria);
            case INE:
                return this.generateInEdgeScript(criteria);
            case OUTE:
                return this.generateOutEdgeScript(criteria);
            default:
                throw new UnsupportedOperationException("unsupported Criteria type");
        }
    }

    public List<String> generateScript(@NonNull GremlinQuery query) {
        final Criteria criteria = query.getCriteria();
        final List<String> scriptList = new ArrayList<>();

        scriptList.add(GREMLIN_PRIMITIVE_GRAPH);

        if (this.source instanceof GremlinSourceVertex) {
            scriptList.add(GREMLIN_PRIMITIVE_VERTEX_ALL);
        } else if (this.source instanceof GremlinSourceEdge) {
            scriptList.add(GREMLIN_PRIMITIVE_EDGE_ALL);
        } else {
            throw new UnsupportedOperationException("Cannot generate script from graph entity");
        }

        scriptList.add(generateHasLabel(this.source.getLabel()));


        if (criteria != null)
        {
            scriptList.add(this.generateScriptTraversal(criteria));
        }

        return scriptList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> generate(@NonNull GremlinQuery query) {
        final List<String> scriptList = new ArrayList<>(this.generateScript(query));

        return Collections.singletonList(String.join(GREMLIN_PRIMITIVE_INVOKE, scriptList));
    }

    /**
     * Generates a where(not(%s))
     * @param criteria
     * @return
     */
    public String generateNot(@NonNull Criteria criteria) {
        return String.format(GREMLIN_PRIMITIVE_NOT,
            this.generateScriptTraversal(criteria.getSubCriteria().get(0)));
    }

    /**
     * Generates a where(or(has(subject, value1),...,has(subject, valueN))) based on the subject and values of the
     * @param criteria
     * @return
     */
    public String generateOr(@NonNull Criteria criteria) {
        //where(or(has(id, '1458678'), has(id, '1723881'), has(id, '8595585')))
        Assert.notEmpty(criteria.getSubValues(),
            "OR criteria sub values can't be empty");

        final String subject = this.getCriteriaSubject(criteria);
        final List<String> hasList = new ArrayList<>();

        if (subject.equals(PROPERTY_ID)) {
            criteria.getSubValues().forEach(
                value -> hasList.add(generateHasId(value.toString())));
        } else {
            criteria.getSubValues().forEach(
                value -> hasList.add(generateHas(subject, value)));
        }

        return String.format("where(or(%s))",
            String.join(",", hasList));
    }

    /**
     * Generates a where(and(where(has(subject, value1)),...,where(has(subject, valueN))))
     * based on the subject and values of the input criteria.
     * @param criteria The input {@link Criteria}
     * @return The generated script.
     */
    public String generateAnd(@NonNull Criteria criteria) {
        //where(or(has(id, '1458678'), has(id, '1723881'), has(id, '8595585')))
        Assert.notEmpty(criteria.getSubCriteria(),
            "AND sub criteria values can't be empty");

        final List<String> subCriteriaTraversal = new ArrayList<>();

        criteria.getSubCriteria().forEach(
            subCriteria -> {
                subCriteriaTraversal.add(
                    this.generateScriptTraversal(subCriteria));
            });

        return String.format("where(and(%s))",
            String.join(",", subCriteriaTraversal));
    }

    public String generateInVScript(@NonNull Criteria criteria) {
        final String left =
            this.generateScriptTraversal(criteria.getSubCriteria().get(0));
        final String right =
            this.generateScriptTraversal(criteria.getSubCriteria().get(1));

        return this.generateCombinedScript(left, right, criteria.getType());
    }

    public String generateOutVScript(@NonNull Criteria criteria) {
        return GREMLIN_PRIMITIVE_OUTV;
    }

    public String generateOutEdgeScript(@NonNull Criteria criteria) {
        return String.format(criteria.getSubject(), criteria.getSubValues().get(0));
    }

    public String generateInEdgeScript(@NonNull Criteria criteria) {
        return String.format(criteria.getSubject(), criteria.getSubValues().get(0));
    }
}

