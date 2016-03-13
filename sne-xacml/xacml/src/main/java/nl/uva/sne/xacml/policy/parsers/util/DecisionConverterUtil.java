/*
 * *
 *  * Copyright (C) 2016 Canh Ngo <canhnt@gmail.com>
 *  * All rights reserved.
 *  *
 *  * This library is free software; you can redistribute it and/or
 *  * modify it under the terms of the GNU Lesser General Public
 *  * License as published by the Free Software Foundation; either
 *  * version 3.0 of the License, or any later version.
 *  *
 *  * This library is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  * Lesser General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU Lesser General Public
 *  * License along with this library; if not, write to the Free Software
 *  * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 *  * MA 02110-1301 USA
 *
 */
package nl.uva.sne.xacml.policy.parsers.util;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.DecisionType;

public class DecisionConverterUtil {

    public static DecisionType convertMIDDDecision(nl.uva.sne.xacml.DecisionType decision) {
        switch (decision) {
            case Deny:
                return DecisionType.DENY;
            case Permit:
                return DecisionType.PERMIT;
            case Indeterminate:
            case Indeterminate_P:
            case Indeterminate_D:
            case Indeterminate_DP:
                return DecisionType.INDETERMINATE;
            case NotApplicable:
                return DecisionType.NOT_APPLICABLE;
            default:
                throw new IllegalArgumentException("Unknow decision value:" + decision);
        }
    }

}
