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
package nl.uva.sne.midd.edges;

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.interval.Interval;
import nl.uva.sne.midd.nodes.Node;
import nl.uva.sne.midd.util.GenericUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An out-going edge from a node
 *
 * @author Canh Ngo
 * @date: Aug 9, 2012
 */
public abstract class AbstractEdge<T extends Comparable<T>> {

    /**
     * List of intervals for the current edge.
     */
    private List<Interval<T>> intervals;

    /**
     * The sub-diagram of the function at the edge's endpoint
     */
    private Node subDiagram;

    private AbstractEdge() {
        this.intervals = new ArrayList<>();
    }

    public AbstractEdge(final Interval<T> interval) throws MIDDException {
        this();
        this.intervals.add(new Interval<T>(interval));
    }

    public AbstractEdge(final List<Interval<T>> intervals) {
        this.intervals = new ArrayList<>(intervals);
    }

    /**
     * Copy constructor
     * @param e
     */
    public AbstractEdge(AbstractEdge<T> e) throws MIDDException {
        intervals = new ArrayList<>(e.intervals);
        this.subDiagram = GenericUtils.newInstance(e.subDiagram);
    }
    public boolean containsInterval(final Interval<T> interval) {
        for (Interval<T> item : intervals) {
            if (item.contains(interval)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the immutable list of intervals
     */
    public List<Interval<T>> getIntervals() {
        return Collections.unmodifiableList(this.intervals);
    }

    /**
     * Return the mutable sub diagram of the edge.
     *
     * @return
     */
    public Node getSubDiagram() throws MIDDException {
        return this.subDiagram;
    }

    public abstract Class<T> getType();

    /**
     * Check if the value is matched with the edge's intervals
     *
     * @param value
     * @return
     */
    public boolean match(final T value) throws MIDDException {
        for (Interval<T> interval : this.intervals) {
            if (interval.hasValue(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Set the referred node become the sub-diagram of the edge.
     * Note: the child node is mutable
     *
     * @param node
     */
    public void setSubDiagram(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("child argument must not be null");
        }
        this.subDiagram = node;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("{");
        for (Interval i : this.intervals) {
            buffer.append(i.toString());
        }
        buffer.append("}");
        return buffer.toString();
    }
}
