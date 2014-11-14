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

package nl.uva.sne.midd.nodes;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import nl.uva.sne.midd.util.GenericUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.uva.sne.midd.Decision;
import nl.uva.sne.midd.DecisionType;
import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.UnmatchedException;
import nl.uva.sne.midd.edges.AbstractEdge;
import nl.uva.sne.midd.interval.Interval;
import nl.uva.sne.midd.obligations.InternalNodeState;

/**
 * @author Canh Ngo
 */
public abstract class InternalNode<T extends Comparable<T>> extends AbstractNode {
    private static final Logger log = LoggerFactory.getLogger(InternalNode.class);

    private List<AbstractEdge<T>> edges = new ArrayList<>();

    private InternalNodeState state;

    public InternalNode(int id, DecisionType state) {
        super(id);
        this.state = new InternalNodeState(state);
    }

    public InternalNode(int id, InternalNodeState state) {
        super(id);
        this.state = new InternalNodeState(state);
    }

    /**
     * Copy constructor
     *
     * @param node
     */
    public InternalNode(InternalNode<T> node) throws MIDDException {
        super(node);
        this.state = new InternalNodeState(node.state);
        for (AbstractEdge<T> e : node.edges) {
            AbstractEdge<T> newEdge = GenericUtils.newInstance(e);
            edges.add(newEdge);
        }
    }

    /**
     * Create an out-going edge to the given node. The edge and child node are mutable objects.
     * If they are used in another tree, it'd better to clone before adding them.
     *
     * Note: Edge<?> and InternalNode<T> must use the same type
     *
     * @param edge
     * @param child
     */
    @SuppressWarnings("unchecked")
    public void addChild(final AbstractEdge<?> edge, final AbstractNode child) {
        if (child == null || edge == null ||
                edge.getIntervals() == null || edge.getIntervals().size() == 0) {
            throw new IllegalArgumentException("Cannot add null child or empty edge");
        }

        edge.setSubDiagram(child);
        edges.add((AbstractEdge<T>) edge);
    }

    /**
     * Create decision value from current internal state
     *
     * @return
     */
    public Decision buildDecision() {
        return state.buildDecision();
    }


    /**
     * Return the child of the current node with equivalent interval on its incoming edge.
     *
     * @param interval The interval which is the subset of the interval in the incoming edge
     * @return null if no matching edge found.
     */
    public AbstractNode getChild(Interval<T> interval) throws MIDDException {
        for (AbstractEdge<T> e : this.edges) {
            if (e.containsInterval(interval)) {
                return e.getSubDiagram();
            }
        }
        return null;
    }

    public List<AbstractEdge<T>> getEdges() {
        return this.edges;
    }

    public InternalNodeState getState() {
        return this.state;
    }

    public void setState(InternalNodeState state) {
        this.state = new InternalNodeState(state);
    }

    /**
     * Collect all intervals of all outgoing edges
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    public List<Interval> getIntervals() {

        List<Interval> lstIntervals = new ArrayList<Interval>();
        for (AbstractEdge<T> e : this.edges) {
            lstIntervals.addAll(e.getIntervals());
        }
        return lstIntervals;
    }

    public DecisionType getStateIN() {
        return this.state.getStateIN();
    }

    @Override
    public abstract Class<?> getType();

    /**
     * Return an edge to match with input value
     *
     * @param value
     * @return
     * @throws nl.uva.sne.midd.UnmatchedException
     */
    public AbstractEdge<T> match(T value) throws UnmatchedException, MIDDException {
        for (AbstractEdge<T> e : this.edges) {
            if (e.match(value)) {
                return e;
            }
        }
        throw new UnmatchedException("No matching edge found for value " + value);
    }

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
//
//	/**
//	 * Retrieve the null-edge from the list of edges of the internal node.
//	 * 
//	 * @return Null if no null-edge found
//	 */
//	public NullEdge getNullEdge() {
//		for(AbstractEdge e : this.edges) {
//			if (e instanceof NullEdge)
//				return (NullEdge) e;
//		}
//		return null;
//	}
}
