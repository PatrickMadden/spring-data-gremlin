/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.spring.data.gremlin.query.criteria;


import com.microsoft.spring.data.gremlin.common.Constants;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;

@Getter
public class Criteria {

    private String subject;
    private List<Object> subValues;
    private final CriteriaType type;
    private final List<Criteria> subCriteria;

    private Criteria(CriteriaType type) {
        this.type = type;
        this.subCriteria = new ArrayList<>();
    }

    private static boolean isBinaryOperation(CriteriaType type) {
        switch (type) {
            case AND:
            case OR:
            case INV:
            case OUTV:
                return true;
            default:
                return false;
        }
    }

    private static boolean isUnaryOperation(CriteriaType type) {
        switch (type) {
            case EXISTS:
            case AFTER:
            case BEFORE:
            case BETWEEN:
            case IS_EQUAL:
            case NOT:
                return true;
            default:
                return false;
        }
    }

    public static Criteria getNotInstance(Criteria criteria) {

        final Criteria notCriteria = new Criteria(CriteriaType.NOT);

        notCriteria.subCriteria.add(criteria);

        return notCriteria;
    }


    public static Criteria getInVInstance()
    {
        return new Criteria(CriteriaType.INV);
    }


    public static Criteria getOutEdgeInstance(String label)
    {
        final Criteria criteria = new Criteria(CriteriaType.OUTE);
        criteria.subject = Constants.GREMLIN_PRIMITIVE_OUT_EDGE;
        criteria.subValues = Arrays.asList(label);

        return criteria;
    }

    public static Criteria getInEdgeInstance(String label)
    {
        final Criteria criteria = new Criteria(CriteriaType.INE);
        criteria.subject = Constants.GREMLIN_PRIMITIVE_IN_EDGE;
        criteria.subValues = Arrays.asList(label);

        return criteria;
    }

    public static Criteria getUnaryInstance(CriteriaType type, @NonNull String subject, @NonNull List<Object> values) {
        Assert.isTrue(isUnaryOperation(type), "type should be Unary operation");

        final Criteria criteria = new Criteria(type);

        criteria.subject = subject;
        criteria.subValues = values;

        return criteria;
    }

	/**
	 * Multivalent OR operation.
	 * @param values A list of one or more values for a given subject to be OR'd.
	 * @return A multivalent OR criteria.
	 */
    public static Criteria getOrInstance(@NonNull String subject, @NonNull List<Object> values) {
        final Criteria criteria = new Criteria(CriteriaType.OR);

        criteria.subject = subject;
        criteria.subValues = values;

        return criteria;
    }


	/**
	 * Multivalent AND operation.
	 * <li>Produces where(and(traversalScript, traversalScript...))</li>
	 * @param subCriteria A list of one or more criteria to be AND
	 * @return A multivalent AND criteria.
	 */
	public static Criteria getAndInstance(@NonNull List<Criteria> subCriteria) {
        final Criteria criteria = new Criteria(CriteriaType.AND);
        criteria.subCriteria.addAll(subCriteria);

        return criteria;
    }


    public static Criteria getBinaryInstance(CriteriaType type, @NonNull Criteria left, @NonNull Criteria right) {
        Assert.isTrue(isBinaryOperation(type), "type should be Binary operation");

        final Criteria criteria = new Criteria(type);

        criteria.subCriteria.add(left);
        criteria.subCriteria.add(right);

        Assert.isTrue(criteria.getSubCriteria().size() == 2, "Binary should contains 2 subCriteria");

        return criteria;
    }
}
