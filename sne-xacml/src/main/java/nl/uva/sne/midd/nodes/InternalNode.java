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
 */
package nl.uva.sne.midd.nodes;

import java.util.List;

import nl.uva.sne.midd.Decision;
import nl.uva.sne.midd.DecisionType;
import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.UnmatchedException;
import nl.uva.sne.midd.edges.AbstractEdge;
import nl.uva.sne.midd.interval.Interval;
import nl.uva.sne.midd.obligations.InternalNodeState;

public interface InternalNode<T extends Comparable<T>> {

    /**
     * Create decision value from current internal state
     */
    Decision buildDecision();

    /**
     * Return the child of the current node with equivalent interval on its incoming edge.
     *
     * @param interval The interval which is the subset of the interval in the incoming edge
     * @return null if no matching edge found.
     */
    AbstractNode getChild(Interval<T> interval) throws MIDDException;

    /**
     * Create an out-going edge to the given node. The edge and child node are mutable objects.
     * If they are used in another tree, it'd better to clone before adding them.
     *
     * Note: Edge<?> and InternalNode<T> must use the same type
     *
     * @param edge
     * @param child
     */
    void addChild(final AbstractEdge<?> edge, final AbstractNode child);

    List<AbstractEdge<T>> getEdges();

    InternalNodeState getState();

    void setState(InternalNodeState state);

    /**
     * Collect all intervals of all outgoing edges
     * @return
     */
    List<Interval> getIntervals();

    DecisionType getStateIN();

    /**
     * Return an edge to match with input value
     *
     * @param value
     * @return
     * @throws nl.uva.sne.midd.UnmatchedException
     */
    AbstractEdge<T> match(T value) throws UnmatchedException, MIDDException;

    void addAllEdges(List<AbstractEdge<T>> newEdges);
}
