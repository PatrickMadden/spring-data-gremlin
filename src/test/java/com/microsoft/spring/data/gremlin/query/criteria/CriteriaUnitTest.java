/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query.criteria;


import com.microsoft.spring.data.gremlin.common.Constants;
import com.microsoft.spring.data.gremlin.common.domain.Person;
import com.microsoft.spring.data.gremlin.query.query.GremlinQuery;
import com.microsoft.spring.data.gremlin.query.query.QueryFindScriptGenerator;
import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.microsoft.spring.data.gremlin.query.criteria.CriteriaType.IS_EQUAL;
import static com.microsoft.spring.data.gremlin.query.criteria.CriteriaType.OR;


public class CriteriaUnitTest {

    private static final String notResult =
        "g.V().has(label, 'label-person').not(where(where(has('name', 'Patrick')).and().where(has(id, '12345'))))";

    @Test(expected = IllegalArgumentException.class)
    public void testGetUnaryInstanceException() {
        final List<Object> values = new ArrayList<>();

        Criteria.getUnaryInstance(OR, "fake-name", values);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetBinaryInstanceException() {
        final List<Object> values = new ArrayList<>();
        final Criteria left = Criteria.getUnaryInstance(IS_EQUAL, "fake-name", values);
        final Criteria right = Criteria.getUnaryInstance(IS_EQUAL, "fake-name", values);

        Criteria.getBinaryInstance(IS_EQUAL, left, right);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCriteriaTypeToGremlinException() {
        CriteriaType.criteriaTypeToGremlin(IS_EQUAL);
    }


    @Test
    public void testNotCriteria() {
        final Criteria c1 = Criteria.getUnaryInstance(IS_EQUAL, "name", Arrays.asList("Patrick"));
        final Criteria c2 = Criteria.getUnaryInstance(IS_EQUAL, Constants.PROPERTY_ID, Arrays.asList("12345"));
        final Criteria notCriteria = Criteria.getNotInstance(Criteria.getBinaryInstance(CriteriaType.AND, c1, c2));

        final QueryFindScriptGenerator findScriptGenerator = new QueryFindScriptGenerator();

        final GremlinQuery query = new GremlinQuery(notCriteria);
        final List<String> stringList = findScriptGenerator.generate(query, Person.class);

        Assert.assertTrue(stringList.size() > 0);

        final String gremlinQuery = String.join(Constants.GREMLIN_PRIMITIVE_INVOKE, stringList);

        Assert.assertEquals(gremlinQuery, notResult);
    }
}
