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
package nl.uva.sne.xacml.policy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.uva.sne.xacml.policy.parsers.util.CombiningAlgConverterUtil;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObjectFactory;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;

public class PolicySetBuilder {
	private static final Logger logger = LoggerFactory.getLogger(PolicySetBuilder.class);

	private static final String DEFAULT_ROOT_VERSION = "1.0";

	private static final String DEFAULT_ROOT_POLICY_ID = "urn:nl:uva:sne:xacml:default-policy-repository-id";

	public static final String DEFAULT_COMBINING_ALG_ID = CombiningAlgConverterUtil.XACML_3_0_POLICY_COMBINING_ALGO_DENY_UNLESS_PERMIT;

	// List contains both PolicyType and PolicySetType
	protected List<?> children;

	private String combiningAlgId;

	
	public PolicySetBuilder(String combiningAlgId, List<?> policies){
		
		if (policies == null || policies.size() == 0)
			throw new IllegalArgumentException("policies collection argument must not be null or empty");
		
		this.combiningAlgId = combiningAlgId;
		
		for(Object o : policies) {
			if (!(o instanceof PolicyType) && !(o instanceof PolicySetType))
				throw new IllegalArgumentException("policies collection argument must only contain PolicyType or PolicySetType");
		}
		
		children = new ArrayList(policies);
	}
	
	public PolicySetType create(String policyId, String version) {
		PolicySetType root;
		root = (new ObjectFactory()).createPolicySetType();
		root.setPolicyCombiningAlgId(combiningAlgId);
		root.setPolicySetId(policyId);
		root.setVersion(version);
		
		Collection<? extends JAXBElement<?>> c;
		List<JAXBElement<?>> childrenJAXB = new ArrayList<JAXBElement<?>>();
		for(Object o: children) {
			if (o instanceof PolicyType) {
				JAXBElement<PolicyType> child = (new ObjectFactory()).createPolicy((PolicyType)o);
				childrenJAXB.add(child);
			} else if (o instanceof PolicySetType) {
				JAXBElement<PolicySetType> child = (new ObjectFactory()).createPolicySet((PolicySetType)o);
				childrenJAXB.add(child);
			} else {
				logger.debug("Unsupport object type in the policy collection:" + o.getClass());
			}
		}
		
		root.getPolicySetOrPolicyOrPolicySetIdReference().addAll(childrenJAXB);
		
		return root;
	}
	
	public PolicySetType create() {
		return create(DEFAULT_ROOT_POLICY_ID, DEFAULT_ROOT_VERSION);
	}
}
