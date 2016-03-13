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

package nl.uva.sne.midd.nodes.internal;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.UnmatchedException;
import nl.uva.sne.midd.edges.AbstractEdge;
import nl.uva.sne.midd.interval.Interval;
import nl.uva.sne.midd.nodes.AbstractNode;
import nl.uva.sne.midd.nodes.Node;
import nl.uva.sne.midd.util.GenericUtils;

public class InternalNodeImpl<T extends Comparable<T>>
        extends AbstractNode<T> implements InternalNode<T> {
    private static final Logger log = LoggerFactory.getLogger(InternalNodeImpl.class);

    private final Class<T> type;

    private final List<AbstractEdge<T>> edges = new ArrayList<>();

    protected InternalNodeImpl(final int id, Class<T> type) {
        super(id);
        this.type = type;
    }
    /**
     * Copy constructor
     *
     * @param node
     */
    public InternalNodeImpl(InternalNode<T> node) throws MIDDException {
        super(node);
        this.type = node.getType();

        for (AbstractEdge<T> e : node.getEdges()) {
            AbstractEdge<T> newEdge = GenericUtils.newInstance(e);
            edges.add(newEdge);
        }
    }

    @Override
    public void addChild(final AbstractEdge<?> edge, final Node child) {
        if (child == null || edge == null ||
                edge.getIntervals() == null || edge.getIntervals().size() == 0) {
            throw new IllegalArgumentException("Cannot add null child or empty edge");
        }

        edge.setSubDiagram(child);
        edges.add((AbstractEdge<T>) edge);
    }

    @Override
    public Node getChild(Interval<T> interval) throws MIDDException {
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
    public List<Interval> getIntervals() {

        List<Interval> lstIntervals = new ArrayList<Interval>();
        for (AbstractEdge<T> e : this.edges) {
            lstIntervals.addAll(e.getIntervals());
        }
        return lstIntervals;
    }

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
    public Class<T> getType() {
        return null;
    }

    @Override
    public void print(OutputStream os) throws MIDDException {
        String strNode = "ID:" + this.getID();
        PrintStream ps = new PrintStream(os);

        ps.println(strNode);

        for (AbstractEdge<?> e : this.edges) {
            Node child = e.getSubDiagram();
            ps.println(e.getIntervals());
            if (child != null) {
                child.print(os);
            }
        }
    }
}
