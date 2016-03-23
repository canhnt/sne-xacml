
/*
 * Copyright (C) 2013-2016 Canh Ngo <canhnt@gmail.com>
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA
 */

package nl.uva.sne.xacml.policy.parsers;

import nl.uva.sne.midd.builders.ConjunctiveBuilder;
import nl.uva.sne.midd.builders.MIDDBuilder;
import nl.uva.sne.xacml.DecisionType;
import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.builders.DisjunctiveBuilder;
import nl.uva.sne.midd.edges.AbstractEdge;
import nl.uva.sne.midd.interval.Interval;
import nl.uva.sne.midd.nodes.Node;
import nl.uva.sne.midd.nodes.ExternalNode;
import nl.uva.sne.midd.nodes.internal.InternalNode;
import nl.uva.sne.xacml.builders.ServiceRegistry;
import nl.uva.sne.xacml.builders.XNodeFactory;
import nl.uva.sne.xacml.nodes.internal.State;
import nl.uva.sne.xacml.nodes.internal.StateImpl;
import nl.uva.sne.midd.util.EdgeUtils;
import nl.uva.sne.xacml.util.NodeUtils;
import nl.uva.sne.xacml.AttributeMapper;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;

import java.util.*;

/**
 * AnyOfExpression class is to parse AnyOf XACML 3.0 element to return map of parsed intervals for each variable
 * identifier.
 * <p/>
 * Note: AnyOf element is composed from disjunctive sequence of AllOf elements.
 * <p/>
 * AnyOfType := SEQUENCE_OF<AllOfType>
 *
 * @author Canh Ngo
 * @date: Sep 27, 2012
 */
public class AnyOfExpression {

    private final XNodeFactory nodeFactory;

    private AnyOfType anyOf = null;

    private AttributeMapper attrMapper;

    // Store flags to indicate if an attribute is set 'MustBePresent' property
    private Map<String, Boolean> mapperMustBePresent = null;

    public AnyOfExpression(final AnyOfType anyOf, final AttributeMapper attrMapper) {
        this((XNodeFactory) ServiceRegistry.getInstance().getService(ServiceRegistry.NODE_FACTORY),
                anyOf, attrMapper);
    }

    public AnyOfExpression(final XNodeFactory nodeFactory, final AnyOfType anyOf, final AttributeMapper attrMapper) {
        if (anyOf == null) {
            throw new IllegalArgumentException("Cannot parse a null AnyOf expression");
        }

        if (attrMapper == null) {
            throw new IllegalArgumentException("Attribute mapper parameter must not be null");
        }

        this.nodeFactory = nodeFactory;

        this.anyOf = anyOf;
        this.attrMapper = attrMapper;

        mapperMustBePresent = new HashMap<String, Boolean>();
    }

    /**
     * Create a MIDD from conjunctions of intervals
     *
     * @param intervals
     * @return
     * @throws MIDDParsingException
     * @throws MIDDException
     */
    public Node createFromConjunctionClauses(Map<String, AttributeInfo> intervals) throws MIDDParsingException, MIDDException {

        if (intervals == null || intervals.size() == 0) {
            return ExternalNode.newInstance(); // return true-value external node
        }

        // Create edges from intervals
        Map<Integer, AbstractEdge<?>> edges = new HashMap<Integer, AbstractEdge<?>>();
        for (String attrId : intervals.keySet()) {
            int varId = attrMapper.getVariableId(attrId);
            Interval<?> interval = intervals.get(attrId).getInterval();

            Class<?> type = interval.getType();
            AbstractEdge<?> e = EdgeUtils.createEdge(interval);
            edges.put(varId, e);
        }

        List<Integer> lstVarIds = new ArrayList<Integer>(edges.keySet());
        Collections.sort(lstVarIds);

        // create a MIDD path from list of edges, start from lowest var-id
        InternalNode<?> root = null;
        InternalNode<?> currentNode = null;
        AbstractEdge<?> currentEdge = null;

        Iterator<Integer> lstIt = lstVarIds.iterator();
        while (lstIt.hasNext()) {
            Integer varId = lstIt.next();
            AbstractEdge<?> e = edges.get(varId);

            // default is NotApplicable, unless the "MustBePresent" is set to "true"
            String attrId = attrMapper.getAttributeId(varId);
            boolean isAttrMustBePresent = intervals.get(attrId).isMustBePresent;

            State nodeState = new StateImpl(isAttrMustBePresent ? DecisionType.Indeterminate : DecisionType.NotApplicable);

            InternalNode<?> node = nodeFactory.create(varId, nodeState, e.getType());
            if (root == null) {
                root = node; // root points to the start of the MIDD path
                currentNode = node;
                currentEdge = e;
            } else {
                currentNode.addChild(currentEdge, node);
                currentNode = node;
                currentEdge = e;
            }
        }
        // the tail points to the true clause
        currentNode.addChild(currentEdge, ExternalNode.newInstance()); // add a true-value external node
        return root;
    }

    public Node parse() throws XACMLParsingException, MIDDParsingException, MIDDException {
        if (anyOf == null) {
            throw new NullPointerException("AnyOf element must not be null");
        }

        List<AllOfType> lstAllOf = anyOf.getAllOf();

        if (lstAllOf == null || lstAllOf.size() == 0) {
            return ExternalNode.newInstance(); // return true-value external node
        }

        /**
         * Each element in this list represents a AllOf expression.
         */
        List<Map<String, AttributeInfo>> lstAllOfExprIntervals = new ArrayList<Map<String, AttributeInfo>>();

        // Parsing from XML element to list of intervals
        AllOfExpression allOfExpression = new AllOfExpression();

        for (AllOfType allOf : lstAllOf) {
            // Extract intervals from a AllOfType element
            allOfExpression.parse(allOf);
            Map<String, AttributeInfo> attributeInfos = allOfExpression.getAttributeInfos();

            if (!attributeInfos.isEmpty())    // if this AllOf expr has any applicable predicates
            {
                lstAllOfExprIntervals.add(attributeInfos);
            }
        }

        // Indexing variables
        List<String> attributes = new ArrayList<String>();
        for (Map<String, AttributeInfo> i : lstAllOfExprIntervals) {
            Set<String> attrSet = i.keySet();
            attributes.addAll(attrSet);
        }
        // sort attribute in alphabet order (temporary)
        Collections.sort(attributes);
        attrMapper.addAttributes(attributes);

        // Build MIDDs from allOf expressions and combine as disjunction
        Node root = null;

        Iterator<Map<String, AttributeInfo>> iterAllOf = lstAllOfExprIntervals.iterator();

        while (iterAllOf.hasNext()) {
            Map<String, AttributeInfo> currentAllOfExp = iterAllOf.next();
            // Create an IDD from list of intervals in the AllOf expression
            Node n = createFromConjunctionClauses(currentAllOfExp);

            // Combine MIDDs as disjunction clauses
            if (root == null) {
                root = n;
            } else {
                // Join current MIDD with the new MIDD using disjunctive operation
                final DisjunctiveBuilder disjunctiveBuilder = (DisjunctiveBuilder) ServiceRegistry.getInstance().getService("DISJUNCTIVE");
                root = disjunctiveBuilder.join(root, n);
            }
        }

        return root;
    }
}
