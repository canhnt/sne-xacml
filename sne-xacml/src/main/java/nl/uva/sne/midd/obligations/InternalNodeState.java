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
package nl.uva.sne.midd.obligations;

import nl.uva.sne.midd.Decision;
import nl.uva.sne.midd.DecisionType;
import nl.uva.sne.xacml.ExternalNode3;

import java.util.List;

/**
 * @author Canh Ngo
 *
 * @version
 * @date: Sep 20, 2012
 */

/**
 * Class represents returned information when evaluation has indeterminate state at a node
 */
public class InternalNodeState {

    private ExternalNode3 state;

    public InternalNodeState(DecisionType state) {
        this.state = new ExternalNode3(state);
    }

    public InternalNodeState(DecisionType stateIN, List<ObligationExpression> oes) {
        this.state = new ExternalNode3(stateIN, oes);
    }


    public InternalNodeState(ExternalNode3 n) {
        this.state = new ExternalNode3(n);
    }

    public InternalNodeState(InternalNodeState state) {
        this.state = new ExternalNode3(state.state);
    }

    public Decision buildDecision() {
        return state.buildDecision();
    }

    public ExternalNode3 getExternalNode() {
        return state;
    }

    public List<ObligationExpression> getObligationExpressions() {
        return state.getObligationExpressions();
    }

    public DecisionType getStateIN() {
        return state.getDecision();
    }
}
