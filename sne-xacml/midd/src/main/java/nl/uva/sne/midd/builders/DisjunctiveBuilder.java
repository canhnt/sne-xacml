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
package nl.uva.sne.midd.builders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.edges.AbstractEdge;
import nl.uva.sne.midd.interval.Interval;
import nl.uva.sne.midd.nodes.ExternalNode;
import nl.uva.sne.midd.nodes.Node;
import nl.uva.sne.midd.nodes.NodeFactory;
import nl.uva.sne.midd.nodes.internal.InternalNode;
import nl.uva.sne.midd.partition.Partition;
import nl.uva.sne.midd.partition.PartitionBuilder;
import nl.uva.sne.midd.util.EdgeUtils;
import nl.uva.sne.midd.util.GenericUtils;

/**
 * Join two MIDD using disjunctive operator.
 *
 * @author Canh Ngo
 */
public class DisjunctiveBuilder implements MIDDBuilder {
    private static final Logger log = LoggerFactory.getLogger(DisjunctiveBuilder.class);

    private final NodeFactory nodeFactory;

    public DisjunctiveBuilder(final NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    /**
     * @param midd1
     * @param midd2
     * @return
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Node join(Node midd1, Node midd2) throws MIDDException {

        if (midd1 == null || midd2 == null) {
            log.error("Disjunctive join with a null MIDD");
            return (midd1 == null) ? midd2 : midd1;
        }

        // Find the MIDD with lower variable order, if MIDD1 has higher, swap to MIDD2.
        if (midd1.getID() != ExternalNode.EXTERNAL_NODE_ID &&
                midd2.getID() != ExternalNode.EXTERNAL_NODE_ID) {
            if (midd1.getID() > midd2.getID()) {
                Node temp = midd1;
                midd1 = midd2;
                midd2 = temp;
            }
        } else if (midd1.getID() == ExternalNode.EXTERNAL_NODE_ID) {

            Node temp = midd1;
            midd1 = midd2;
            midd2 = temp;
        }

        // now MIDD2 always has higher order variable than MIDD1

        // If any of them is true, then return true
        if (midd1 instanceof ExternalNode ||
                midd2 instanceof ExternalNode) {
            // midd2 is also external node, return true external node
            return new ExternalNode();
        } else {
            // both are internal nodes, combine two internal nodes here
            InternalNode<?> n1 = (InternalNode) midd1;
            InternalNode<?> n2 = (InternalNode) midd2;

            if (n1.getID() == n2.getID()) {
                return joinWithSameLevel(n1, n2);
            } else {
                //n1 as lower variable order than n2, so:
                // - Create a node clone from n1 -> n
                // - combine n2 with all children of n1 -> children[1..k], add them to children of n

                // Clone n1
                InternalNode<?> n = nodeFactory.create(n1);

                for (AbstractEdge<?> e : n1.getEdges()) {
                    Node child = join(e.getSubDiagram(), n2);
                    if (child != null) {
                        n.addChild(EdgeUtils.cloneEdge(e), child);
                    } else {
                        throw new RuntimeException("empty child");
                    }
                }
                if (n.getEdges().size() == 0) {
                    System.err.println("Disjunctive join two midd return null");
                    return null;
                } else {
                    return n;
                }
            }
        }
    }

    /**
     * Combine two MIDD with same level of attribute at their roots
     *
     * @param n1
     * @param n2
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private InternalNode<?> joinWithSameLevel(InternalNode n1,
                                                             InternalNode n2) throws MIDDException {
        if (n1.getID() != n2.getID()) {
            throw new IllegalArgumentException("Both params should have the same variable level at their root");
        }

        if (n1.getType() != n2.getType()) {
            throw new IllegalArgumentException("Both IDD roots should have the same data type");
        }

        Partition p1 = PartitionBuilder.createPartition(n1.getIntervals());
        Partition p2 = PartitionBuilder.createPartition(n2.getIntervals());

        // Union two partitions
        Partition<?> p = PartitionBuilder.union(p1, p2);

        if (p.size() == 0) {
            throw new RuntimeException("Empty partition");
        }

        // Clone n1 to n: should combine two internal node states of n1 & n2?
        InternalNode<?> newIDD = nodeFactory.create(n1);

        for (Interval<?> interval : p.getIntervals()) {
            Node op1 = n1.getChild(interval);
            Node op2 = n2.getChild(interval);
            Node child = null;
            if (op1 != null && op2 != null) {
                child = join(op1, op2);
            } else if (op1 != null || op2 != null) {
                child = GenericUtils.newInstance(op1 != null ? op1 : op2);
            } else {
                throw new RuntimeException("Error joining two partitions, the output partition is incorrect");
            }

            if (child != null) {
                AbstractEdge<?> edge = EdgeUtils.createEdge(interval, newIDD.getType());
                newIDD.addChild(edge, child);
            } else {
                throw new RuntimeException("Empty child");
            }
        }

        return newIDD;
    }
}
