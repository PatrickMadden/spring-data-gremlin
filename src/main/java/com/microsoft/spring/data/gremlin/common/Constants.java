/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.common;

import org.apache.tinkerpop.gremlin.driver.ser.Serializers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final String PROPERTY_ID = "id";
    public static final String PROPERTY_LABEL = "label";
    public static final String PROPERTY_TYPE = "type";
    public static final String PROPERTY_VALUE = "value";
    public static final String PROPERTY_PROPERTIES = "properties";
    public static final String PROPERTY_INV = "inV";
    public static final String PROPERTY_OUTV = "outV";

    public static final String RESULT_TYPE_VERTEX = "vertex";
    public static final String RESULT_TYPE_EDGE = "edge";

    public static final String DEFAULT_VERTEX_LABEL = "";
    public static final String DEFAULT_EDGE_LABEL = "";
    public static final String DEFAULT_COLLECTION_NAME = "";
    public static final int DEFAULT_ENDPOINT_PORT = 443;
    public static final String DEFAULT_REPOSITORY_IMPLEMENT_POSTFIX = "Impl";
    public static final int DEFAULT_MAX_CONTENT_LENGTH = 65536;
    public static final int DEFAULT_MAX_WAIT_FOR_CONNECTION = 3000;
    public static final int DEFAULT_MAX_WAIT_FOR_SESSION_CLOSE = 3000;
    public static final Serializers DEFAULT_SERIALIZERS = Serializers.GRAPHSON;
    public static final int DEFAULT_MIN_CONNECTION_POOL_SIZE = 2;
    public static final int DEFAULT_MAX_CONNECTION_POOL_SIZE = 8;
    public static final int DEFUAULT_MIN_SIMULTANEOUS_USAGE_PER_CONNECTION = 8;
    public static final int DEFUAULT_MAX_SIMULTANEOUS_USAGE_PER_CONNECTION = 16;
    public static final int DEFAULT_MIN_IN_PROCESS_PER_CONNECTION = 1;
    public static final int DEFAULT_MAX_IN_PROCESS_PER_CONNECTION = 4;
    public static final long DEFAULT_KEEP_ALIVE_INTERVAL = 1800000L;
    public static final int DEFAULT_WORKER_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    public static final String GREMLIN_MODULE_NAME = "Gremlin";
    public static final String GREMLIN_MODULE_PREFIX = "gremlin";
    public static final String GREMLIN_MAPPING_CONTEXT = "gremlinMappingContext";

    public static final String GREMLIN_PRIMITIVE_GRAPH = "g";
    public static final String GREMLIN_PRIMITIVE_INVOKE = ".";
    public static final String GREMLIN_PRIMITIVE_DROP = "drop()";
    public static final String GREMLIN_PRIMITIVE_COUNT = "count()";

    public static final String GREMLIN_PRIMITIVE_EDGE_ALL = "E()";

    public static final String GREMLIN_PRIMITIVE_VERTEX_ALL = "V()";

    public static final String GREMLIN_PRIMITIVE_HAS_STRING = "has('%s', '%s')";
    public static final String GREMLIN_PRIMITIVE_HAS_NUMBER = "has('%s', %d)";
    public static final String GREMLIN_PRIMITIVE_HAS_DOUBLE = "has('%s', %f)";
    public static final String GREMLIN_PRIMITIVE_HAS_BOOLEAN = "has('%s', %b)";

    public static final String GREMLIN_PRIMITIVE_PROPERTY_STRING = "property('%s', '%s')";
    public static final String GREMLIN_PRIMITIVE_PROPERTY_NUMBER = "property('%s', %d)";
    public static final String GREMLIN_PRIMITIVE_PROPERTY_DOUBLE = "property('%s', %f)";
    public static final String GREMLIN_PRIMITIVE_PROPERTY_BOOLEAN = "property('%s', %b)";

    public static final String GREMLIN_PRIMITIVE_OUT_EDGE = "outE('%s')";
    public static final String GREMLIN_PRIMITIVE_IN_EDGE = "inE('%s')";

    public static final String GREMLIN_PRIMITIVE_UPDATE_PROPERTY_STRING = "property(single, '%s', '%s')";
    public static final String GREMLIN_PRIMITIVE_UPDATE_PROPERTY_NUMBER = "property(single, '%s', %d)";
    public static final String GREMLIN_PRIMITIVE_UPDATE_PROPERTY_DOUBLE = "property(single, '%s', %f)";
    public static final String GREMLIN_PRIMITIVE_UPDATE_PROPERTY_BOOLEAN = "property(single, '%s', %b)";
    public static final String GREMLIN_PRIMITIVE_UPDATE_PROPERTY_KEYWORD = "property(single, %s, '%s')";


    public static final String GREMLIN_PRIMITIVE_AND = "and()";
    public static final String GREMLIN_PRIMITIVE_OR = "or()";
    public static final String GREMLIN_PRIMITIVE_WHERE = "where(%s)";
    public static final String GREMLIN_PRIMITIVE_NOT = "not(%s)";

    public static final String GREMLIN_QUERY_BARRIER = "barrier";

    public static final String GREMLIN_PRIMITIVE_VALUES = "values('%s')";
    public static final String GREMLIN_PRIMITIVE_IS = "is(%s)";
    public static final String GREMLIN_PRIMITIVE_GT = "gt(%d)";
    public static final String GREMLIN_PRIMITIVE_LT = "lt(%d)";
    public static final String GREMLIN_PRIMITIVE_BETWEEN = "between(%d, %d)";
    public static final String GREMLIN_PRIMITIVE_INV = "inV()";
    public static final String GREMLIN_PRIMITIVE_OUTV = "outV()";

    public static final String GREMLIN_PRIMITIVE_IS_GT = String.format(GREMLIN_PRIMITIVE_IS, GREMLIN_PRIMITIVE_GT);
    public static final String GREMLIN_PRIMITIVE_IS_LT = String.format(GREMLIN_PRIMITIVE_IS, GREMLIN_PRIMITIVE_LT);
    public static final String GREMLIN_PRIMITIVE_IS_BETWEEN = String.format(
            GREMLIN_PRIMITIVE_IS,
            GREMLIN_PRIMITIVE_BETWEEN
    );

    public static final String GREMLIN_SCRIPT_EDGE_ALL = String.join(GREMLIN_PRIMITIVE_INVOKE,
            GREMLIN_PRIMITIVE_GRAPH,
            GREMLIN_PRIMITIVE_EDGE_ALL
    );

    public static final String GREMLIN_SCRIPT_VERTEX_ALL = String.join(GREMLIN_PRIMITIVE_INVOKE,
            GREMLIN_PRIMITIVE_GRAPH,
            GREMLIN_PRIMITIVE_VERTEX_ALL
    );

    public static final String GREMLIN_SCRIPT_EDGE_DROP_ALL = String.join(GREMLIN_PRIMITIVE_INVOKE,
            GREMLIN_PRIMITIVE_GRAPH,
            GREMLIN_PRIMITIVE_EDGE_ALL,
            GREMLIN_PRIMITIVE_DROP
    );

    public static final String GREMLIN_SCRIPT_VERTEX_DROP_ALL = String.join(GREMLIN_PRIMITIVE_INVOKE,
            GREMLIN_PRIMITIVE_GRAPH,
            GREMLIN_PRIMITIVE_VERTEX_ALL,
            GREMLIN_PRIMITIVE_DROP
    );

    public static final String GREMLIN_PROPERTY_CLASSNAME = "_classname";
}
