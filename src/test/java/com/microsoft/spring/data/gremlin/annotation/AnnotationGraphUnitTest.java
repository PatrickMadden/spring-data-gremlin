/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.annotation;

import com.microsoft.spring.data.gremlin.common.domain.Network;
import com.microsoft.spring.data.gremlin.common.domain.Roadmap;
import com.microsoft.spring.data.gremlin.repository.support.GremlinEntityInformation;
import org.junit.Assert;
import org.junit.Test;

public class AnnotationGraphUnitTest {

    @Test
    public void testAnnotationGraphDefaultCollection() {
        Assert.assertNull(GremlinEntityInformation.get(Network.class).getEntityLabel());
        Assert.assertTrue(GremlinEntityInformation.get(Network.class).isEntityGraph());
    }

    @Test
    public void testAnnotationGraphSpecifiedCollection() {
        Assert.assertNull(GremlinEntityInformation.get(Roadmap.class).getEntityLabel());
        Assert.assertTrue(GremlinEntityInformation.get(Roadmap.class).isEntityGraph());
    }
}
