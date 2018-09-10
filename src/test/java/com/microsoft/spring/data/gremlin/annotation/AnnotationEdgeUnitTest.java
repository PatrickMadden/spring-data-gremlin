/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.annotation;

import com.microsoft.spring.data.gremlin.common.TestConstants;
import com.microsoft.spring.data.gremlin.common.domain.*;
import com.microsoft.spring.data.gremlin.repository.support.GremlinEntityInformation;
import org.junit.Assert;
import org.junit.Test;

public class AnnotationEdgeUnitTest {

    @Test
    public void testAnnotationEdgeDefaultLabel() {
        Assert.assertTrue(GremlinEntityInformation.get(Dependency.class).isEntityEdge());
        Assert.assertNotNull(GremlinEntityInformation.get(Dependency.class).getEntityLabel());
        Assert.assertEquals(GremlinEntityInformation.get(Dependency.class).getEntityLabel(),
                Dependency.class.getSimpleName());
    }

    @Test
    public void testAnnotationEdgeSpecifiedLabel() {
        Assert.assertNotNull(GremlinEntityInformation.get(Relationship.class).getEntityLabel());
        Assert.assertTrue(GremlinEntityInformation.get(Relationship.class).isEntityEdge());
        Assert.assertEquals(GremlinEntityInformation.get(Relationship.class).getEntityLabel(),
                TestConstants.EDGE_RELATIONSHIP_LABEL);
    }
}
