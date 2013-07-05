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
/**
 * System and Network Engineering Group
 * University of Amsterdam
 *
 */
package nl.uva.sne.xacml;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.EffectType;

import nl.uva.sne.midd.algorithms.CombiningAlgorithm;
import nl.uva.sne.midd.nodes.AbstractNode;
import nl.uva.sne.midd.nodes.ExternalNode;
import nl.uva.sne.midd.obligations.Obligation;
import nl.uva.sne.midd.obligations.ObligationExpression;
import nl.uva.sne.midd.Decision;
import nl.uva.sne.midd.DecisionType;
/**
 * External node for XACML 3.0 decisions.
 *
 * @version 
 * @date: Sep 11, 2012
 */
public class ExternalNode3 extends ExternalNode {

	private DecisionType effect;
	
	List<ObligationExpression> obligationExpressions;
	
	// place holder: condition element here
	
	/**
	 * @param id
	 */
	public ExternalNode3(DecisionType effect) {
		super();
		this.effect = effect;
		this.obligationExpressions = new ArrayList<ObligationExpression>();
	}

	public ExternalNode3(DecisionType effect, List<ObligationExpression> oes) {
		super();
		this.effect = effect;
		
		// only accept OE that has fullFillOn equals to effect value
		if (oes != null && oes.size() > 0) {
			for(ObligationExpression oe: oes) {
				if (!oe.isFulfilled(effect))
					throw new RuntimeException("Only accept OE that has fullFillOn equals to effect value");
			}
			this.obligationExpressions = new ArrayList<ObligationExpression>(oes);			
		}
	}
	

	public ExternalNode3(ExternalNode3 n) {
		this.effect = n.effect;
		this.obligationExpressions = new ArrayList<ObligationExpression>(n.obligationExpressions);
	}

	public Decision buildDecision() {
		Decision decision = new Decision(this.effect);
		
		if (obligationExpressions != null && obligationExpressions.size() > 0) {
			for (ObligationExpression oe : this.obligationExpressions) {
				if (oe.isFulfilled(effect))
					decision.getObligations().add(oe.getObligation());
			}			
		}
		return decision;
	}

	@Override
	public AbstractNode clone() {
		return new ExternalNode3(this.effect, obligationExpressions);
	}		
	
//	/**
//	 * Combine values of two external nodes
//	 * @param node
//	 */
//	public ExternalNode3 combine(ExternalNode3 node, CombiningAlgorithm algo) {
//		effect = algo.combine(this.effect, node.effect);
//		
//		// only accept OE that match with combined effect.		
//		ArrayList<ObligationExpression> lstOE = new ArrayList<ObligationExpression>();
//		for(ObligationExpression oe : node.obligationExpressions) {
//			if (oe.isFulfilled(effect))
//				lstOE.add(oe);
//		}
//		for(ObligationExpression oe : obligationExpressions) {
//			if (oe.isFulfilled(effect))
//				lstOE.add(oe);
//		}
//		
//		obligationExpressions = lstOE;
//		return this;				
//	}
	
	public DecisionType getDecision() {
		return this.effect;
	}

	public List<ObligationExpression> getObligationExpressions() {
		return obligationExpressions;
	}
	
	public List<Obligation> getObligations() {
		List<Obligation> obligations = new ArrayList<Obligation>();
		for(ObligationExpression oe:this.obligationExpressions) {
			obligations.add(oe.getObligation());
		}
		return obligations;	
	}

	@Override
	public Class<?> getType() {		
		return DecisionType.class;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("{" + this.effect + ",[");
		if (obligationExpressions != null)
			for(Object o: this.obligationExpressions) {
				if (o != null)
					buf.append(o.toString() + ",");			
			}
		buf.deleteCharAt(buf.length() - 1);
		buf.append("]}");
		return buf.toString();
		
	}
	
	@Override
	public void print(OutputStream os){
		PrintStream ps = new PrintStream(os); 
		ps.println(this.toString());
	}
}
