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
package nl.uva.sne.midd;

import java.util.ArrayList;
import java.util.List;

import nl.uva.sne.midd.datatype.AnyURI;
import nl.uva.sne.midd.edges.AbstractEdge;
import nl.uva.sne.midd.edges.AnyURIEdge;
import nl.uva.sne.midd.edges.DoubleEdge;
import nl.uva.sne.midd.edges.IntegerEdge;
import nl.uva.sne.midd.edges.StringEdge;
import nl.uva.sne.midd.interval.Interval;
import nl.uva.sne.midd.nodes.AnyURINode;
import nl.uva.sne.midd.nodes.DoubleNode;
import nl.uva.sne.midd.nodes.IntegerNode;
import nl.uva.sne.midd.nodes.InternalNode;
import nl.uva.sne.midd.nodes.StringNode;
import nl.uva.sne.midd.obligations.InternalNodeState;

/**
 * @author Canh Ngo (t.c.ngo@uva.nl)
 * @date: Sep 13, 2012
 */
public class IDDFactory {

    @SuppressWarnings("unchecked")
    public static AbstractEdge<?> cloneEdge(AbstractEdge<?> e) throws MIDDException {

        final Class<?> type = e.getType();
        List<? extends Interval> intervals = e.getIntervals();

        return createEdge(intervals, type);
    }

    @SuppressWarnings("unchecked")
    public static AbstractEdge<?> createEdge(Interval interval, Class<?> type) {
        if (type == Integer.class) {
            return new IntegerEdge(interval);
        } else if (type == Double.class) {
            return new DoubleEdge(interval);
        } else if (type == String.class) {
            return new StringEdge(interval);
        } else if (type == AnyURI.class) {
            return new AnyURIEdge(interval);
        } else {
            throw new UnsupportedOperationException("Unsupported data type to create createEdge" + type.getName());
        }
    }

    public static AbstractEdge<?> createEdge(Interval<?> interval) throws MIDDException {
        return createEdge(interval, interval.getType());
    }

    @SuppressWarnings("unchecked")
    public static AbstractEdge<?> createEdge(List<? extends Interval> intervals, Class<?> type) throws MIDDException {

        if (type == Integer.class) {
            List<Interval<Integer>> lst = new ArrayList<Interval<Integer>>();
            for (Interval i : intervals) {

                lst.add(new Interval<Integer>(i));
            }
            return new IntegerEdge(lst);
        } else if (type == Double.class) {
            List<Interval<Double>> lst = new ArrayList<Interval<Double>>();
            for (Interval<?> i : intervals) {
                lst.add(new Interval<Double>((Interval<Double>) i));
            }
            return new DoubleEdge(lst);
        } else if (type == String.class) {
            List<Interval<String>> lst = new ArrayList<Interval<String>>();
            for (Interval<?> i : intervals) {
                lst.add(new Interval<String>((Interval<String>) i));
            }
            return new StringEdge(lst);
        } else if (type == AnyURI.class) {
            List<Interval<AnyURI>> lst = new ArrayList<Interval<AnyURI>>();
            for (Interval<?> i : intervals) {
                lst.add(new Interval<AnyURI>((Interval<AnyURI>) i));
            }
            return new AnyURIEdge(lst);
        } else {
            throw new UnsupportedOperationException("Unsupported data type to create createEdge" + type.getName());
        }

    }

    public static InternalNode<?> createInternalNode(int id, InternalNodeState state, Class<?> type) {

        if (type == Integer.class) {
            return new IntegerNode(id, state);
        } else if (type == Double.class) {
            return new DoubleNode(id, state);
        } else if (type == String.class) {
            return new StringNode(id, state);
        } else if (type == AnyURI.class) {
            return new AnyURINode(id, state);
        } else {
            throw new UnsupportedOperationException("Unsupported data type to create InternalNode" + type.getName());
        }
    }

    public static InternalNode<?> createInternalNode(InternalNode<?> n1,
                                                     Class<?> type) {
        return createInternalNode(n1.getID(), n1.getState(), type);
    }

    public static <T extends Comparable<T>> Variable<?> createVariable(int id, T value, Class<T> type) {
        return new Variable<T>(id, value, type);
    }
}
