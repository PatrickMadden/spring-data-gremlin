/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.annotation;

import com.microsoft.spring.data.gremlin.common.TestConstants;
import com.microsoft.spring.data.gremlin.common.domain.Library;
import com.microsoft.spring.data.gremlin.common.domain.Person;
import com.microsoft.spring.data.gremlin.repository.support.GremlinEntityInformation;
import org.junit.Assert;
import org.junit.Test;

public class AnnotationVertexUnitTest {

    @Test
    public void testAnnotationVertexDefaultLabel() {
        Assert.assertNotNull(GremlinEntityInformation.get(Library.class).getEntityLabel());
        Assert.assertTrue(GremlinEntityInformation.get(Library.class).isEntityVertex());
        Assert.assertEquals(GremlinEntityInformation.get(Library.class).getEntityLabel(),
                Library.class.getSimpleName());
    }

    @Test
    public void testAnnotationVertexSpecifiedLabel() {
        Assert.assertNotNull(GremlinEntityInformation.get(Person.class).getEntityLabel());
        Assert.assertTrue(GremlinEntityInformation.get(Person.class).isEntityVertex());
        Assert.assertEquals(GremlinEntityInformation.get(Person.class).getEntityLabel(),
                TestConstants.VERTEX_PERSON_LABEL);
    }
}
