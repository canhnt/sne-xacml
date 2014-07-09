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
/**
 * System and Network Engineering Group
 * University of Amsterdam
 *
 */
package nl.uva.sne.midd.obligations;

import nl.uva.sne.midd.DecisionType;

/**
 * @author Canh Ngo (t.c.ngo@uva.nl)
 * @date: Sep 20, 2012
 */
public class ObligationExpression {
    public DecisionType fulFillOn;

    public Obligation obligation;

    public ObligationExpression(DecisionType fulFillOn, Obligation obligation) {
        this.fulFillOn = fulFillOn;
        this.obligation = obligation;
    }

    public Obligation getObligation() {
        return obligation;
    }

    public boolean isFulfilled(DecisionType effect) {
        return this.fulFillOn == effect;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[" + this.obligation.toString() + "]");
        return buf.toString();
    }
}
