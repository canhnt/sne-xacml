/**
 * SNE-XACML: A high performance XACML evaluation engine.
 *
 * Copyright (C) 2013 Canh T. Ngo <canhnt@gmail.com>
 * System and Network Engineering Group, University of Amsterdam.
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

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.algorithms.CombiningAlgorithm;
import nl.uva.sne.midd.builders.ConjunctiveBuilder;
import nl.uva.sne.midd.builders.MIDDCombiner;
import nl.uva.sne.midd.nodes.AbstractNode;
import nl.uva.sne.midd.nodes.ExternalNode;
import nl.uva.sne.midd.nodes.InternalNode;
import nl.uva.sne.midd.utils.MIDDUtil;
import nl.uva.sne.xacml.AttributeMapper;
import nl.uva.sne.xacml.policy.parsers.util.CombiningAlgConverterUtil;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PolicyParser {
	private static final Logger logger = LoggerFactory.getLogger(PolicySetParser.class);
	
	private PolicyType policy;
	
	private AbstractNode preCondition;
	
	private AttributeMapper attrMapper = null;

	/**
	 * 
	 * @param condition a MIDD that represents the target expression of the parents' policy. 
	 * @param policy a XACML 3.0 policy element.
	 */
	public PolicyParser(AbstractNode condition, PolicyType policy, AttributeMapper attrMapper) {
		if (policy == null)
			throw new IllegalArgumentException("PolicyType argument must not be null");
		if (attrMapper == null)
			throw new IllegalArgumentException("AttributeMapper argument must not be null");
		
		this.policy = policy;
		this.attrMapper = attrMapper;
		
		// If there's no condition, assume it's the true value
		if (condition == null)
			this.preCondition = new ExternalNode();
		else {
//			this.preCondition = condition;
			this.preCondition = condition.clone();
		}
	}
	
	private AbstractNode combineRuleMIDDs(List<AbstractNode> lstMIDDs,
			CombiningAlgorithm rca) {
//		System.out.println("Combining policy " + this.policy.getPolicyId());
		logger.debug("Combining policy {}", this.policy.getPolicyId());
		
		MIDDCombiner combiner = new MIDDCombiner(rca);
		
		Iterator<AbstractNode> it = lstMIDDs.iterator();
		AbstractNode root = null;
				
		while (it.hasNext()) {
			AbstractNode n = it.next();
			if (root == null)
				root = n;
			else {
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

		if (objs == null || objs.size() == 0)
			throw new XACMLParsingException("No children rules found in the policy: " + policy.getPolicyId());
		
		List<RuleType> rules = new ArrayList<RuleType>();
		
		for(Object obj: objs) {
			if (obj instanceof RuleType) {
				rules.add((RuleType)obj);
			} else {
				logger.info("Unsupport element of type " + obj.getClass() + " inside policy '" + policy.getPolicyId() + "'");
			}
			
		}
		if (rules.size() == 0)
			throw new XACMLParsingException("No children rules found in the policy: '" + policy.getPolicyId() + "'");
		
		return rules;
	}
	

	private AbstractNode getTargetCondition() throws XACMLParsingException, MIDDParsingException, MIDDException {
		TargetType target = policy.getTarget();		
		
		List<AnyOfType> lstAnyOf;
		if (target != null) {
			lstAnyOf = target.getAnyOf();
		} else {
			lstAnyOf = null;
		}
		
		TargetExpression te = new TargetExpression(lstAnyOf, this.attrMapper);
		return te.parse();
	}

	public AbstractNode parse() throws XACMLParsingException, MIDDParsingException, MIDDException {
		
		// Get a MIDD to represent the policy's target expression		
		AbstractNode targetCondition = getTargetCondition();
		if (targetCondition == null) // no applicable MIDD extracted from Target
			return null;
		
		
		// Conjunctive join it with the MIDD representing preconditions of the policy 
		AbstractNode condition = ConjunctiveBuilder.join(this.preCondition, targetCondition);
		
		List<RuleType> rules = getRules();
		
		// Create MIDDs for rules inside the policy 
		List<AbstractNode> lstMIDDs = new ArrayList<AbstractNode>();
		for(RuleType r : rules) {
			RuleParser ruleParser = new RuleParser(condition, r, attrMapper);
			
			// return the MIDD with XACML decisions at the external nodes
			AbstractNode xacmlMIDD = ruleParser.parse();
			if (xacmlMIDD == null) {// a never-applicable rule
				System.err.println("Found a non-transformable MIDD rule:" + r.getRuleId());
			} 
			else
				lstMIDDs.add(xacmlMIDD);
		}
		
		// combine MIDDs using policy's rule-combining-algorithm		
		CombiningAlgorithm rca = CombiningAlgConverterUtil.getAlgorithm(policy.getRuleCombiningAlgId());		
		return combineRuleMIDDs(lstMIDDs, rca);
	}
}
