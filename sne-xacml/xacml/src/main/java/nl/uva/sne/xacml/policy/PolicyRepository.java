/*
 * SNE-XACML: A high performance XACML evaluation engine.
 *
 * Copyright (C) 2013-2014 Canh Ngo <canhnt@gmail.com>
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
 *
 */
package nl.uva.sne.xacml.policy;

import nl.uva.sne.xacml.policy.finder.PolicyFinder;
import nl.uva.sne.xacml.policy.finder.impl.PolicyFinderImpl;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Canh Ngo
 */
public class PolicyRepository {
    private static final Logger logger = LoggerFactory.getLogger(PolicyRepository.class);

    /**
     * Collection of referenced-based policies/policysets
     */
    protected Map<String, Object> policyRepo;

    private PolicySetType rootPolicySet;


    public PolicyRepository() {
        policyRepo = new HashMap<String, Object>();
    }

    public void setContextPolicy(PolicySetType policySet) {
        rootPolicySet = policySet;
    }

    public void setContextPolicies(String combiningAlgId, List<?> ctxPolicies) {
        // create a root policyset of the repository from combiningAlgId and policies children.
        PolicySetBuilder builder = new PolicySetBuilder(combiningAlgId, ctxPolicies);
        rootPolicySet = builder.create();

        // add context policies to the repository
        addPolicies(ctxPolicies);
    }

    public PolicySetType getRootPolicySet() {
        return this.rootPolicySet;
    }

    public void addPolicies(List<?> policies) {

        for (Object o : policies) {
            if (o instanceof PolicySetType) {
                PolicySetType ps = (PolicySetType) o;
                String id = ps.getPolicySetId();
                if (policyRepo.containsKey(id)) {
                    logger.error("PolicySet " + id + " has exisited!");
                }

                policyRepo.put(id, ps);
            } else if (o instanceof PolicyType) {
                PolicyType p = (PolicyType) o;
                String id = p.getPolicyId();
                if (policyRepo.containsKey(id)) {
                    logger.error("Policy " + id + " has exisited!");
                }

                policyRepo.put(id, p);
            } else {
                throw new RuntimeException("Illegal policy type");
            }
        }
    }

    public PolicyFinder createPolicyFinder() {
        PolicyFinder finder = new PolicyFinderImpl(this.policyRepo);
        return finder;
    }
}
