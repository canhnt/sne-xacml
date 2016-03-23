/*
 * Copyright (C) 2016 Canh Ngo <canhnt@gmail.com>
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
 *
 */

package nl.uva.sne.xacml.nodes.internal;

import java.io.OutputStream;
import java.io.PrintStream;

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.nodes.internal.InternalNodeImpl;
import nl.uva.sne.xacml.Decision;
import nl.uva.sne.xacml.DecisionType;

public abstract class AbstractInternalXACMLNode<T extends Comparable<T>>
        extends InternalNodeImpl<T> implements InternalXACMLNode<T> {

    private State state;

    public AbstractInternalXACMLNode(int id, final Class<T> type, final State state) {
        super(id, type);
        this.state = new StateImpl(state);
    }

    public AbstractInternalXACMLNode(InternalXACMLNode node) throws MIDDException {
        super(node);
        this.state = new StateImpl(node.getState());

    }
    @Override
    public Decision buildDecision() {
        return state.buildDecision();
    }


    @Override
    public DecisionType getStateIN() {
        return this.state.getStateIN();
    }

    @Override
    public State getState() {
        return this.state;
    }

    @Override
    public void setState(State state) {
        this.state = new StateImpl(state);
    }

    @Override
    public void print(final OutputStream os) throws MIDDException {
        super.print(os);
        PrintStream ps = new PrintStream(os);;
        ps.println("State=" + this.state.getStateIN());
    }
}
