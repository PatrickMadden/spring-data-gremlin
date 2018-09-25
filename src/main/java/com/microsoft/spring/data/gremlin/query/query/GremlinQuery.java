/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query.query;


import com.microsoft.spring.data.gremlin.query.criteria.Criteria;
import org.springframework.util.Assert;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;


public class GremlinQuery {

    @Getter
    @Setter
    private final Criteria criteria;
    private QueryScriptGenerator generator;

    public GremlinQuery(QueryScriptGenerator generator)
    {
        this(null, generator);
    }

    public GremlinQuery(Criteria criteria) {
        this(criteria, null);
    }

    public GremlinQuery(Criteria criteria, QueryScriptGenerator generator) {
        this.criteria = criteria;
        this.generator = generator;
    }

    public void setScriptGenerator(QueryScriptGenerator generator) {
        this.generator = generator;
    }

    public QueryScriptGenerator getScriptGenerator() {
        return this.generator;
    }

    public <T> List<String> doSentenceGenerate(@NonNull Class<T> domainClass) {
        Assert.notNull(this.generator, "Generator should not be null");

        return this.generator.generate(this, domainClass);
    }
}
