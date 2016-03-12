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

import nl.uva.sne.midd.DecisionType;
import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.builders.ConjunctiveBuilder;
import nl.uva.sne.midd.edges.AbstractEdge;
import nl.uva.sne.midd.nodes.Node;
import nl.uva.sne.midd.nodes.ExternalNode;
import nl.uva.sne.midd.nodes.internal.InternalNode;
import nl.uva.sne.midd.nodes.internal.StateImpl;
import nl.uva.sne.midd.obligations.Obligation;
import nl.uva.sne.midd.obligations.ObligationExpression;
import nl.uva.sne.midd.util.GenericUtils;
import nl.uva.sne.xacml.AttributeMapper;
import nl.uva.sne.xacml.ExternalNode3;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class RuleParser {
    private static final transient org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RuleParser.class);

    private static nl.uva.sne.midd.DecisionType convertEffectType(EffectType effect) {
        switch (effect) {
            case PERMIT:
                return nl.uva.sne.midd.DecisionType.Permit;
            case DENY:
                return nl.uva.sne.midd.DecisionType.Deny;
            default:
                throw new UnsupportedOperationException("Effect type not found");
        }
    }

    private static ObligationExpression convertObligationExpression(ObligationExpressionType xacmlOE) {
        String id = xacmlOE.getObligationId();
        nl.uva.sne.midd.DecisionType fulFillOn = convertEffectType(xacmlOE.getFulfillOn());
        ObligationExpression oe = new ObligationExpression(fulFillOn, new Obligation(id));
        return oe;
    }

//	/**
//	 * Create the indeterminate state for internal node of the MIDD representing a rule:
//	 *  - If rule's effect is "Permit", return Indeterminate(P)
//	 *  - If rule's effect is "Deny", return Indeterminate(D)
//	 * 
//	 * @param extNode
//	 * @return
//	 */
//	private static InternalNodeState getRuleStateIN(nl.uva.sne.midd.DecisionType effect) {
//		if (effect == nl.uva.sne.midd.DecisionType.Permit) 
//			return new InternalNodeState(nl.uva.sne.midd.DecisionType.Indeterminate_P);
//		else if (effect == nl.uva.sne.midd.DecisionType.Deny)
//			return new InternalNodeState(nl.uva.sne.midd.DecisionType.Indeterminate_D);
//		else
//			throw new UnsupportedOperationException("Invalid used of method createStateIN");		
//	}

    private RuleType rule;

    private Node preCondition;

    private AttributeMapper attrMapper = null;

    private nl.uva.sne.midd.DecisionType ruleEffect;

    /**
     * @param condition  A MIDD representing the target condition of the parents' policy
     * @param rule       A XACML 3.0 Rule element
     * @param attrMapper
     */
    public RuleParser(Node condition, RuleType rule, AttributeMapper attrMapper) throws MIDDException {
        if (rule == null) {
            throw new IllegalArgumentException("RuleType argument must not be null");
        }
        if (attrMapper == null) {
            throw new IllegalArgumentException("AttributeMapper argument must not be null");
        }

        log.debug("Processing rule: {}", rule.getRuleId());

        this.rule = rule;
        this.attrMapper = attrMapper;

        // If there's no condition, assume it's the true value
        if (condition == null) {
            this.preCondition = ExternalNode.newInstance();
        } else {
            this.preCondition = GenericUtils.newInstance(condition);
        }
    }

    private List<ObligationExpression> getObligationExpressions() {
        ObligationExpressionsType xacmlOES = rule.getObligationExpressions();
        if (xacmlOES == null) {
            return null;
        }

        List<ObligationExpressionType> lstxacmlOE = xacmlOES.getObligationExpression();

        if (lstxacmlOE == null || lstxacmlOE.size() == 0) {
            return null;
        }

        List<ObligationExpression> oes = new ArrayList<ObligationExpression>();

        for (ObligationExpressionType xacmlOE : lstxacmlOE) {
            ObligationExpression oe = convertObligationExpression(xacmlOE);
            oes.add(oe);
        }

        if (oes.size() > 0) {
            return oes;
        }

        return null;
    }

    private Node getTargetCondition() throws XACMLParsingException, MIDDParsingException, MIDDException {

        TargetType target = rule.getTarget();

        List<AnyOfType> lstAnyOf;
        if (target != null) {
            lstAnyOf = target.getAnyOf();
        } else {
            lstAnyOf = null;
        }

        TargetExpression te = new TargetExpression(lstAnyOf, attrMapper);
        return te.parse();
    }

    public Node parse() throws XACMLParsingException, MIDDParsingException, MIDDException {

        Node targetCondition = getTargetCondition();
        if (targetCondition == null) // no applicable MIDD extracted from Target
        {
            return null;
        }

        // Conjunctive join it with the MIDD representing preconditions of the parents' policy
        Node midd = ConjunctiveBuilder.join(this.preCondition, targetCondition);

        ruleEffect = convertEffectType(rule.getEffect());
        List<ObligationExpression> oes = getObligationExpressions();

        ExternalNode3 extNode = new ExternalNode3(ruleEffect, oes);

        // change the leaves of MIDD by the extNode (effect node)
        if (midd instanceof InternalNode) {
            setEffectNode(midd, extNode);
            return midd;
        } else {
            return extNode;
        }
    }


    /**
     * Set indicated external-xacml3 node to be the leaves of the MIDD
     *
     * @param midd
     * @param extNode
     */
    @SuppressWarnings("rawtypes")
    private void setEffectNode(Node midd, ExternalNode3 extNode) throws MIDDException {
        // Replace current external nodes in the MIDD with the extNode (XACML 3 external node)

        if (!(midd instanceof InternalNode)) {
            throw new IllegalArgumentException("MIDD argument must not be an ExternalNode");
        }

        InternalNode currentNode = (InternalNode) midd;

        Stack<InternalNode> stackNodes = new Stack<>();

        stackNodes.push(currentNode);

        while (!stackNodes.empty()) {
            InternalNode n = stackNodes.pop();

            // Change indeterminate state of the internal node,
            //	- By default is NotApplicable (XACML 3.0, sec 7.3.5, 7.19.3)
            // 	- If the attribute "MustBePresent" is true, then state is "Indeterminate_P" if Effect is "Permit",
            //	  "Indeterminate_D" if Effect is "Deny" - XACML 3.0, section 7.11

            if (n.getStateIN() == DecisionType.Indeterminate) { // this attribute has 'MustBePresent'=true
                if (ruleEffect == DecisionType.Deny) {
                    n.setState(new StateImpl(nl.uva.sne.midd.DecisionType.Indeterminate_D));
                } else if (ruleEffect == DecisionType.Permit) {
                    n.setState(new StateImpl(nl.uva.sne.midd.DecisionType.Indeterminate_P));
                }
            }
//			else {
//				n.setState(new InternalNodeState(nl.uva.sne.midd.DecisionType.NotApplicable));
//			}

            // search for all children of the poped internal node
            @SuppressWarnings("unchecked")
            Iterator<AbstractEdge> it = n.getEdges().iterator();
            while (it.hasNext()) {
                AbstractEdge edge = it.next();
                Node child = edge.getSubDiagram();
                if (child instanceof InternalNode) {
                    stackNodes.push((InternalNode) child);
                } else {
                    edge.setSubDiagram(extNode);        // set the final edge pointing to the xacml3 external node.
                }
            }
        }
    }
}
