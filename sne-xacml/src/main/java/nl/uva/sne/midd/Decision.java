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
package nl.uva.sne.midd;

import java.util.ArrayList;
import java.util.List;

import nl.uva.sne.midd.obligations.Obligation;

/**
 * @author Canh Ngo (t.c.ngo@uva.nl)
 * @since: Sep 20, 2012
 */
public class Decision {
    private DecisionType decision;
    private List<Obligation> obligations;

    public Decision(DecisionType decision) {
        this.decision = decision;
        obligations = new ArrayList<Obligation>();
    }

    public Decision(DecisionType decision, List<Obligation> obligations) {
        this.decision = decision;
        this.obligations = new ArrayList<Obligation>();
        this.obligations.addAll(obligations);
    }

    public DecisionType getDecision() {
        return this.decision;
    }

    public List<Obligation> getObligations() {
        return this.obligations;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(decision);
        if (obligations != null && obligations.size() > 0) {
            buf.append(",{");
            for (Obligation o : obligations) {
                buf.append(o).append(",");
            }
            buf.deleteCharAt(buf.length() - 1);
            buf.append("}");
        }
        return buf.toString();
    }
}
