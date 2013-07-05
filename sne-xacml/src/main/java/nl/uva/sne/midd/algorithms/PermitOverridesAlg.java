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
package nl.uva.sne.midd.algorithms;

import nl.uva.sne.midd.DecisionType;

/**
 * @author Canh Ngo (t.c.ngo@uva.nl)
 *
 * @version 
 * @date: Sep 20, 2012
 */
public class PermitOverridesAlg implements
		CombiningAlgorithm {

	/* (non-Javadoc)
	 * @see nl.uva.sne.midd.algorithms.CombiningAlgorithmInterface#combine(nl.uva.sne.midd.DecisionType, nl.uva.sne.midd.DecisionType)
	 */
	@Override
	public DecisionType combine(DecisionType op1, DecisionType op2) {
		if (op1 == DecisionType.Permit || op2 == DecisionType.Permit)
			return DecisionType.Permit;
		
		if ((op1 == DecisionType.Indeterminate_DP) || (op2 == DecisionType.Indeterminate_DP)) 
			return DecisionType.Indeterminate_DP;
		
		if (op1 == DecisionType.Indeterminate_P) {
			if (op2 == DecisionType.Deny || op2 == DecisionType.Indeterminate_D)
				return DecisionType.Indeterminate_DP;
			else 
				return DecisionType.Indeterminate_P;			
		}
		
		if (op2 == DecisionType.Indeterminate_P) {
			if (op1 == DecisionType.Deny || op1 == DecisionType.Indeterminate_D)
				return DecisionType.Indeterminate_DP;
			else 
				return DecisionType.Indeterminate_P;			
		}
		
		if (op1 == DecisionType.Deny || op2 == DecisionType.Deny)
			return DecisionType.Deny;
		
		if (op1 == DecisionType.Indeterminate_D || op2 == DecisionType.Indeterminate_D)
			return DecisionType.Indeterminate_D;
		
		return DecisionType.NotApplicable;
	}
}
