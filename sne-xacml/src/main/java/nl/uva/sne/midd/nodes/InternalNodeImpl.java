/*
 * Copyright (C) 2013-2016 Canh Ngo <canhnt@gmail.com>
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

package nl.uva.sne.midd.nodes;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.uva.sne.midd.Decision;
import nl.uva.sne.midd.DecisionType;
import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.UnmatchedException;
import nl.uva.sne.midd.edges.AbstractEdge;
import nl.uva.sne.midd.interval.Interval;
import nl.uva.sne.midd.obligations.InternalNodeState;
import nl.uva.sne.midd.util.GenericUtils;

public abstract class InternalNodeImpl<T extends Comparable<T>>
        extends AbstractNode implements InternalNode<T> {
    private static final Logger log = LoggerFactory.getLogger(InternalNodeImpl.class);

    private List<AbstractEdge<T>> edges = new ArrayList<>();

    private InternalNodeState state;

    public InternalNodeImpl(int id, DecisionType state) {
        super(id);
        this.state = new InternalNodeState(state);
    }

    public InternalNodeImpl(int id, InternalNodeState state) {
        super(id);
        this.state = new InternalNodeState(state);
    }

    /**
     * Copy constructor
     *
     * @param node
     */
    public InternalNodeImpl(InternalNodeImpl<T> node) throws MIDDException {
        super(node);
        this.state = new InternalNodeState(node.state);
        for (AbstractEdge<T> e : node.edges) {
            AbstractEdge<T> newEdge = GenericUtils.newInstance(e);
            edges.add(newEdge);
        }
    }

    @Override
    public void addChild(final AbstractEdge<?> edge, final AbstractNode child) {
        if (child == null || edge == null ||
                edge.getIntervals() == null || edge.getIntervals().size() == 0) {
            throw new IllegalArgumentException("Cannot add null child or empty edge");
        }

        edge.setSubDiagram(child);
        edges.add((AbstractEdge<T>) edge);
    }

    @Override
    public Decision buildDecision() {
        return state.buildDecision();
    }

    @Override
    public AbstractNode getChild(Interval<T> interval) throws MIDDException {
        for (AbstractEdge<T> e : this.edges) {
            if (e.containsInterval(interval)) {
                return e.getSubDiagram();
            }
        }
        return null;
    }

    @Override
    public List<AbstractEdge<T>> getEdges() {
        return this.edges;
    }

    @Override
    public InternalNodeState getState() {
        return this.state;
    }

    @Override
    public void setState(InternalNodeState state) {
        this.state = new InternalNodeState(state);
    }

    @Override
    public List<Interval> getIntervals() {

        List<Interval> lstIntervals = new ArrayList<Interval>();
        for (AbstractEdge<T> e : this.edges) {
            lstIntervals.addAll(e.getIntervals());
        }
        return lstIntervals;
    }

    @Override
    public DecisionType getStateIN() {
        return this.state.getStateIN();
    }

    @Override
    public abstract Class<?> getType();

    @Override
    public AbstractEdge<T> match(T value) throws UnmatchedException, MIDDException {
        for (AbstractEdge<T> e : this.edges) {
            if (e.match(value)) {
                return e;
            }
        }
        throw new UnmatchedException("No matching edge found for value " + value);
    }

    @Override
    public void addAllEdges(List<AbstractEdge<T>> newEdges) {
        this.edges.addAll(newEdges);
    }

    @Override
    public void print(OutputStream os) throws MIDDException {
        String strNode = "ID:" + this.getID() + ";" + this.state.getStateIN();
        PrintStream ps = new PrintStream(os);

        ps.println(strNode);

        for (AbstractEdge<?> e : this.edges) {
            AbstractNode child = e.getSubDiagram();
            ps.println(e.getIntervals());
            if (child != null) {
                child.print(os);
            }
        }
    }
}
