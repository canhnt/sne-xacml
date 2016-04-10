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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.builders.MIDDBuilder;
import nl.uva.sne.xacml.algorithms.CombiningAlgorithm;
import nl.uva.sne.xacml.builders.MIDDCombiner;
import nl.uva.sne.midd.nodes.ExternalNode;
import nl.uva.sne.midd.nodes.Node;
import nl.uva.sne.midd.util.GenericUtils;
import nl.uva.sne.xacml.AttributeMapper;
import nl.uva.sne.xacml.builders.MIDDCombinerFactory;
import nl.uva.sne.xacml.policy.parsers.util.CombiningAlgConverterUtil;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

public class PolicyParser {
    private static final Logger log = LoggerFactory.getLogger(PolicySetParser.class);

    private final MIDDBuilder middBuilder;

    @Inject
    private MIDDCombinerFactory middCombinerFactory;

    @Inject
    private TargetExpressionFactory targetExpressionFactory;

    @Inject
    private RuleParserFactory ruleParserFactory;

    private PolicyType policy;

    private Node preCondition;

    private AttributeMapper attrMapper = null;

    /**
     * @param condition a MIDD that represents the target expression of the parents' policy.
     * @param policy    a XACML 3.0 policy element.
     */
    @Inject
    public PolicyParser(MIDDBuilder middBuilder,
                        @Assisted Node condition,
                        @Assisted PolicyType policy,
                        @Assisted AttributeMapper attrMapper) throws MIDDException {
        this.middBuilder = middBuilder;

        if (policy == null) {
            throw new IllegalArgumentException("PolicyType argument must not be null");
        }
        if (attrMapper == null) {
            throw new IllegalArgumentException("AttributeMapper argument must not be null");
        }

        this.policy = policy;
        this.attrMapper = attrMapper;

        // If there's no condition, assume it's the true value
        if (condition == null) {
            this.preCondition = ExternalNode.newInstance();
        } else {
            this.preCondition = GenericUtils.newInstance(condition);
        }
    }

    private Node combineRuleMIDDs(List<Node> lstMIDDs,
                                          CombiningAlgorithm rca) throws MIDDException {
        log.debug("Combining policy {}", this.policy.getPolicyId());

        final MIDDCombiner combiner = middCombinerFactory.create(rca);

        Iterator<Node> it = lstMIDDs.iterator();
        Node root = null;

        while (it.hasNext()) {
            Node n = it.next();
            if (root == null) {
                root = n;
            } else {
                root = combiner.combine(root, n);

//				if (root instanceof InternalNode)
//					System.out.println("root size:"  + MIDDUtil.countNodes((InternalNode) root));
//				if (n instanceof InternalNode)
//					System.out.println("child midd size:"  + MIDDUtil.countNodes((InternalNode) n));
//								
//				root = combiner.combine(root, n);
//				
//				System.out.println("Combined midd size:" + MIDDUtil.countNodes((InternalNode) root));

            }
        }
        return root;
    }

    private List<RuleType> getRules() throws XACMLParsingException {
        List<Object> objs = policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();

        if (objs == null || objs.size() == 0) {
            throw new XACMLParsingException("No children rules found in the policy: " + policy.getPolicyId());
        }

        List<RuleType> rules = new ArrayList<RuleType>();

        for (Object obj : objs) {
            if (obj instanceof RuleType) {
                rules.add((RuleType) obj);
            } else {
                log.info("Unsupport element of type " + obj.getClass() + " inside policy '" + policy.getPolicyId() + "'");
            }

        }
        if (rules.size() == 0) {
            throw new XACMLParsingException("No children rules found in the policy: '" + policy.getPolicyId() + "'");
        }

        return rules;
    }


    private Node getTargetCondition() throws XACMLParsingException, MIDDParsingException, MIDDException {
        TargetType target = policy.getTarget();

        List<AnyOfType> lstAnyOf;
        if (target != null) {
            lstAnyOf = target.getAnyOf();
        } else {
            lstAnyOf = null;
        }

        final TargetExpression te = targetExpressionFactory.create(lstAnyOf, this.attrMapper);
        return te.parse();
    }

    public Node parse() throws XACMLParsingException, MIDDParsingException, MIDDException {

        // Get a MIDD to represent the policy's target expression
        Node targetCondition = getTargetCondition();
        if (targetCondition == null) // no applicable MIDD extracted from Target
        {
            return null;
        }


        // Conjunctive join it with the MIDD representing preconditions of the policy
        Node condition = middBuilder.and(this.preCondition, targetCondition);

        List<RuleType> rules = getRules();

        // Create MIDDs for rules inside the policy
        List<Node> lstMIDDs = new ArrayList<Node>();
        for (RuleType r : rules) {
            final RuleParser ruleParser = ruleParserFactory.create(condition, r, attrMapper);

            // return the MIDD with XACML decisions at the external nodes
            Node xacmlMIDD = ruleParser.parse();
            if (xacmlMIDD == null) {// a never-applicable rule
                System.err.println("Found a non-transformable MIDD rule:" + r.getRuleId());
            } else {
                lstMIDDs.add(xacmlMIDD);
            }
        }

        // combine MIDDs using policy's rule-combining-algorithm
        CombiningAlgorithm rca = CombiningAlgConverterUtil.getAlgorithm(policy.getRuleCombiningAlgId());
        return combineRuleMIDDs(lstMIDDs, rca);
    }
}
