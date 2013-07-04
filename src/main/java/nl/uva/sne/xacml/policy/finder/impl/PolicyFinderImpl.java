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
package nl.uva.sne.xacml.policy.finder.impl;

import java.util.HashMap;
import java.util.Map;

import nl.uva.sne.xacml.policy.finder.PolicyFinder;

public class PolicyFinderImpl implements PolicyFinder{

	private Map<String, Object> policies;
	
	public PolicyFinderImpl(Map<String, Object> policies){
		this.policies = new HashMap<String, Object>(policies);
	}
	
	@Override
	public Object lookup(String id) {
		return policies.get(id);
	}
	
}
