/**
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
package nl.uva.sne.xacml.policy.parsers.util;

import nl.uva.sne.midd.algorithms.CombiningAlgorithm;
import nl.uva.sne.midd.algorithms.DenyOverridesAlg;
import nl.uva.sne.midd.algorithms.DenyUnlessPermitAlg;
import nl.uva.sne.midd.algorithms.FirstApplicableAlg;
import nl.uva.sne.midd.algorithms.PermitOverridesAlg;
import nl.uva.sne.midd.algorithms.PermitUnlessDenyAlg;
import nl.uva.sne.xacml.policy.parsers.XACMLParsingException;

public class CombiningAlgConverterUtil {
    public static String XACML_3_0_RULE_COMBINING_ALGO_DENY_OVERRIDES = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-overrides";

    public static String XACML_3_0_RULE_COMBINING_ALGO_PERMIT_OVERRIDES = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-overrides";

    public static String XACML_3_0_RULE_COMBINING_ALGO_ORDER_PERMIT_OVERRIDES = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:ordered-permit-overrides";

    public static String XACML_3_0_RULE_COMBINING_ALGO_ORDER_DENY_OVERRIDES = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:ordered-deny-overrides";

    public static String XACML_3_0_RULE_COMBINING_ALGO_DENY_UNLESS_PERMIT = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-unless-permit";

    public static String XACML_3_0_RULE_COMBINING_ALGO_PERMIT_UNLESS_DENY = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-unless-deny";

    public static String XACML_1_0_RULE_COMBINING_ALGO_FIRST_APPLICABLE = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable";

    // Policy algorithms
    public static String XACML_3_0_POLICY_COMBINING_ALGO_DENY_OVERRIDES = "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-overrides";

    public static String XACML_3_0_POLICY_COMBINING_ALGO_PERMIT_OVERRIDES = "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:permit-overrides";

    public static String XACML_3_0_POLICY_COMBINING_ALGO_ORDER_PERMIT_OVERRIDES = "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:ordered-permit-overrides";

    public static String XACML_3_0_POLICY_COMBINING_ALGO_ORDER_DENY_OVERRIDES = "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:ordered-deny-overrides";

    public static String XACML_3_0_POLICY_COMBINING_ALGO_DENY_UNLESS_PERMIT = "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-unless-permit";

    public static String XACML_3_0_POLICY_COMBINING_ALGO_PERMIT_UNLESS_DENY = "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:permit-unless-deny";

    public static String XACML_1_0_POLICY_COMBINING_ALGO_FIRST_APPLICABLE = "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:first-applicable";

    public static CombiningAlgorithm getAlgorithm(String combiningAlgId) throws XACMLParsingException {

        if (combiningAlgId == null) {
            throw new IllegalArgumentException("Combining algorithm identifier not found");
        }

        if (combiningAlgId.equalsIgnoreCase(XACML_3_0_RULE_COMBINING_ALGO_DENY_OVERRIDES) ||
                combiningAlgId.equalsIgnoreCase(XACML_3_0_POLICY_COMBINING_ALGO_DENY_OVERRIDES) ||
                combiningAlgId.equalsIgnoreCase(XACML_3_0_RULE_COMBINING_ALGO_ORDER_DENY_OVERRIDES) ||
                combiningAlgId.equalsIgnoreCase(XACML_3_0_POLICY_COMBINING_ALGO_ORDER_DENY_OVERRIDES)) {
            return new DenyOverridesAlg();
        } else if (combiningAlgId.equalsIgnoreCase(XACML_3_0_RULE_COMBINING_ALGO_PERMIT_OVERRIDES) ||
                combiningAlgId.equalsIgnoreCase(XACML_3_0_POLICY_COMBINING_ALGO_PERMIT_OVERRIDES) ||
                combiningAlgId.equalsIgnoreCase(XACML_3_0_RULE_COMBINING_ALGO_ORDER_PERMIT_OVERRIDES) ||
                combiningAlgId.equalsIgnoreCase(XACML_3_0_POLICY_COMBINING_ALGO_ORDER_PERMIT_OVERRIDES))

        {
            return new PermitOverridesAlg();
        } else if (combiningAlgId.equalsIgnoreCase(XACML_3_0_RULE_COMBINING_ALGO_DENY_UNLESS_PERMIT) ||
                combiningAlgId.equalsIgnoreCase(XACML_3_0_POLICY_COMBINING_ALGO_DENY_UNLESS_PERMIT)) {
            return new DenyUnlessPermitAlg();
        } else if (combiningAlgId.equalsIgnoreCase(XACML_3_0_RULE_COMBINING_ALGO_PERMIT_UNLESS_DENY) ||
                combiningAlgId.equalsIgnoreCase(XACML_3_0_POLICY_COMBINING_ALGO_PERMIT_UNLESS_DENY)) {
            return new PermitUnlessDenyAlg();
        } else if (combiningAlgId.equalsIgnoreCase(XACML_1_0_POLICY_COMBINING_ALGO_FIRST_APPLICABLE) ||
                combiningAlgId.equalsIgnoreCase(XACML_1_0_RULE_COMBINING_ALGO_FIRST_APPLICABLE)) {
            return new FirstApplicableAlg();
        } else {
            throw new XACMLParsingException("Not supported combining algorithm: " + combiningAlgId);
        }

    }

}
