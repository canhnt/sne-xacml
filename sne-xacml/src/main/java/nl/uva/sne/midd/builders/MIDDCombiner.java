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

import nl.uva.sne.midd.DecisionType;
import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.algorithms.CombiningAlgorithm;
import nl.uva.sne.midd.edges.AbstractEdge;
import nl.uva.sne.midd.interval.Interval;
import nl.uva.sne.midd.nodes.AbstractNode;
import nl.uva.sne.midd.nodes.InternalNodeImpl;
import nl.uva.sne.midd.obligations.InternalNodeState;
import nl.uva.sne.midd.obligations.ObligationExpression;
import nl.uva.sne.midd.partition.Partition;
import nl.uva.sne.midd.partition.PartitionBuilder;
import nl.uva.sne.midd.util.EdgeUtils;
import nl.uva.sne.midd.util.GenericUtils;
import nl.uva.sne.midd.util.IntervalUtils;
import nl.uva.sne.midd.util.NodeUtils;
import nl.uva.sne.xacml.ExternalNode3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Combine MIDDs using XACML 3.0 combining algorithms.
 */
public class MIDDCombiner {
    private static final Logger log = LoggerFactory.getLogger(MIDDCombiner.class);

    private CombiningAlgorithm algo;

    public MIDDCombiner(CombiningAlgorithm algorithm) {
        this.algo = algorithm;
    }

    /**
     * Combine two MIDD DAG using PCA algorithm.
     *
     * @param midd1
     * @param midd2
     * @return
     */

    //

    /**
     * Combine two MIDD DAG using PCA algorithm. testing version 2012.11.02: this version is to support ordered
     * combining algorithms(e.g. first-applicable)
     *
     * @param midd1
     * @param midd2
     * @return
     */
    public AbstractNode combine(AbstractNode midd1, AbstractNode midd2) throws MIDDException {

        if (midd1 instanceof ExternalNode3) {
            if (midd2 instanceof ExternalNode3) {
                return combineExternalNodes((ExternalNode3) midd1, (ExternalNode3) midd2);            // combine two external nodes
            } else {
                ExternalNode3 n1 = (ExternalNode3) midd1;
                InternalNodeImpl<?> n2 = (InternalNodeImpl<?>) midd2;
                return combineIDD(n1, n2); // combine an external node n1 with an internal node n2
            }
        } else {
            InternalNodeImpl<?> n1 = (InternalNodeImpl<?>) midd1;

            if (midd2 instanceof ExternalNode3) {
                // combine an internal node (midd1) with an external node (midd2)
                ExternalNode3 n2 = (ExternalNode3) midd2;
                return combineIDD(n1, n2);     // combine an internal node n1 with an external node2
            } else {
                // both are internal nodes, combine two internal nodes here
                InternalNodeImpl<?> n2 = (InternalNodeImpl<?>) midd2;

                if (n1.getID() == n2.getID()) {
                    return combineIDDSameLevel(n1, n2);
                } else {
                    //Algorithm: find a node with lower order, e.g: n1
                    // - Create a node clone from n1 -> n
                    // - combine n2 with all children of n1 -> children[1..k], add them to children of n

                    InternalNodeImpl<?> n = null;
                    if (n1.getID() < n2.getID()) {

                        // Clone n1
                        n = NodeUtils.createInternalNode(n1, n1.getType());

                        for (AbstractEdge<?> e : n1.getEdges()) {
                            AbstractNode child = combine(e.getSubDiagram(), n2);
                            if (child != null) {
                                n.addChild(EdgeUtils.cloneEdge(e), child);
                            } else {
                                log.error("empty child");
                            }
                        }

                        // Create a new edge containing the complement of n1 children intervals, connecting n with n2
                        List<Interval<?>> complementIntervals = IntervalUtils.complement(n1.getIntervals());
                        if (complementIntervals.size() > 0) {
                            AbstractEdge<?> edge = EdgeUtils.createEdge(complementIntervals, n1.getType());
                            n.addChild(edge, n2);
                        }

                    } else { // n2 has lower id than n1, do in other way.
                        n = NodeUtils.createInternalNode(n2, n2.getType());
                        for (AbstractEdge<?> e : n2.getEdges()) {
                            AbstractNode child = combine(n1, e.getSubDiagram());
                            if (child != null) {
                                n.addChild(EdgeUtils.cloneEdge(e), child);
                            }
                        }
                        // Create a new edge containing the complement of n2 children intervals, connecting n with n1
                        List<Interval<?>> complementIntervals = IntervalUtils.complement(n2.getIntervals());
                        if (complementIntervals.size() > 0) {
                            AbstractEdge<?> edge = EdgeUtils.createEdge(complementIntervals, n2.getType());
                            n.addChild(edge, n1);
                        }
                    }
                    if (n.getEdges().size() > 0) {
                        return n;
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    /**
     * Combine two MIDDs: an external node with an internal node.
     * <p/>
     * Algorithm: set of intervals: n2.intervals U (*\{n2.intervals}) - with i = *\{n2.intervals}, n.children[0] = n1,
     * n.intervals[0] = *\{n1.intervals} - with (i : n2.intervals), n.children[i] = n2.children[i], n.intervals[i] =
     * n1.interval[i]
     * <p/>
     * 2012.11.13 - Modified: predicate of the internal node n1 to only external node n2 is 'null-edge' -
     * n.intervals[i+1] = Null; n.children[i+1] = n2;
     * <p/>
     * The output MIDD is the DAG rooted at n, has |n2.intervals| + 1 children
     *
     * @param n1
     * @param n2
     * @return
     */
    @SuppressWarnings("rawtypes")
    private AbstractNode combineIDD(ExternalNode3 n1, InternalNodeImpl<?> n2) throws MIDDException {


        List<Interval> intervals = n2.getIntervals();
        List<Interval<?>> complementIntervals = IntervalUtils.complement(intervals);

        // clone new node from n2

        InternalNodeState newINState = combineInternalNodeStates(n2.getState(), n1);

        InternalNodeImpl<?> n = NodeUtils.createInternalNode(n2.getID(), newINState, n2.getType());

        if (complementIntervals.size() > 0) {
            AbstractEdge<?> edge = EdgeUtils.createEdge(complementIntervals, n2.getType());
            if (edge.getIntervals().size() == 0) {
                throw new RuntimeException("Empty edge");
            }
            n.addChild(edge, n1);
        }

        for (AbstractEdge<?> e : n2.getEdges()) {
            AbstractNode child = combine(n1, e.getSubDiagram());
            n.addChild(EdgeUtils.cloneEdge(e), child);
        }
        return n;
    }

    /**
     * Combine two external nodes following the algorithm in this.algo
     *
     * @param n1
     * @param n2
     * @return
     */
    private ExternalNode3 combineExternalNodes(ExternalNode3 n1, ExternalNode3 n2) {
        if (n1 == null || n2 == null) {
            throw new IllegalArgumentException("Input nodes must not be null");
        }

        DecisionType combinedDecision = algo.combine(n1.getDecision(), n2.getDecision());
        ExternalNode3 n = new ExternalNode3(combinedDecision);

        // only accept OE that match with combined decision.

        List<ObligationExpression> oes1 = getFulfilledObligationExpressions(n1.getObligationExpressions(), combinedDecision);
        List<ObligationExpression> oes2 = getFulfilledObligationExpressions(n2.getObligationExpressions(), combinedDecision);

        n.getObligationExpressions().addAll(oes1);
        n.getObligationExpressions().addAll(oes2);

        return n;
    }

    /**
     * Return OE in the list that are fulfilled the indicated decision.
     *
     * @param oes
     * @param decision
     * @return
     */
    private List<ObligationExpression> getFulfilledObligationExpressions(
            List<ObligationExpression> oes,
            DecisionType decision) {

        List<ObligationExpression> fulfilledOEs = new ArrayList<ObligationExpression>();

        if (oes != null && oes.size() > 0) {
            for (ObligationExpression oe : oes) {
                if (oe.isFulfilled(decision)) {
                    fulfilledOEs.add(oe);
                }
            }
        }
        return fulfilledOEs;
    }

    /**
     * Combine a MIDD with internal node at root with an external node.
     * <p/>
     * 2012.11.13 - revision: do not use complement for connecting from n1 to n2, use null-edge is more sensible.
     *
     * @param n1
     * @param n2
     * @return
     */
    @SuppressWarnings("rawtypes")
    private AbstractNode combineIDD(InternalNodeImpl<?> n1,
                                    ExternalNode3 n2) throws MIDDException {
        // Algorithm:
        // set of intervals: n1.intervals U (*\{n1.intervals})
        // 	- with (i : n1.intervals), n.children[i] = n1.children[i], n.intervals[i] = n1.interval[i]
        // 	- with i = *\{n1.intervals}, n.children[i+1] = n2, n.intervals[i+1] = *\{n1.intervals}
        // the output MIDD is the DAG rooted at n, has |n1.intervals| + 1 children
        // return n


        List<Interval> intervals = n1.getIntervals();
        List<Interval<?>> complementIntervals = IntervalUtils.complement(intervals);

        // clone new node from n1
        InternalNodeState newINState = combineInternalNodeStates(n1.getState(), n2);

        InternalNodeImpl<?> n = NodeUtils.createInternalNode(n1.getID(), newINState, n1.getType());

        for (AbstractEdge<?> e : n1.getEdges()) {
            AbstractNode child = combine(e.getSubDiagram(), n2);
            if (child == null) {
                throw new RuntimeException("Empty child");
            }
            n.addChild(EdgeUtils.cloneEdge(e), child);
        }

        // if the complement of the partition is not empty, add a new edge pointing to external node
        if (complementIntervals.size() > 0) {
            AbstractEdge<?> edge = EdgeUtils.createEdge(complementIntervals, n1.getType());
            n.addChild(edge, n2);
        }
        // else, ignore the external node, because its value has been integrated into node n's INState.

        return n;
    }

    /**
     * Combine two MIDD with same level of attribute at their roots
     *
     * @param n1
     * @param n2
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private InternalNodeImpl<?> combineIDDSameLevel(InternalNodeImpl n1,
                                                    InternalNodeImpl n2) throws MIDDException {
        if (n1.getID() != n2.getID()) {
            throw new IllegalArgumentException("Both params should have the same variable level at their root");
        }

        if (n1.getType() != n2.getType()) {
            throw new IllegalArgumentException("Both IDD roots should have the same data type");
        }

        Partition p1 = PartitionBuilder.createPartition(n1.getIntervals());
        Partition p2 = PartitionBuilder.createPartition(n2.getIntervals());

        Partition<?> p = PartitionBuilder.union(p1, p2);
        if (p.size() == 0) {
            log.error("Empty unioned partition");
            return null;
        }

        InternalNodeState newState = combineIndeterminateStates(n1.getState(), n2.getState());

        InternalNodeImpl<?> n = NodeUtils.createInternalNode(n1.getID(), newState, n1.getType());

        for (Interval<?> interval : p.getIntervals()) {
            AbstractNode op1 = n1.getChild(interval);
            AbstractNode op2 = n2.getChild(interval);
            AbstractNode child = null;
            if (op1 != null && op2 != null) {
                child = combine(op1, op2);
            } else if (op1 != null || op2 != null) {
                child = GenericUtils.newInstance(op1 == null ? op2 : op1);
            } else {
                throw new RuntimeException("Error merging two partitions, " +
                        "the output partition has an item not belong to both previous ones");
            }

            if (child != null) {
                AbstractEdge<?> edge = EdgeUtils.createEdge(interval, n.getType());
                if (edge.getIntervals().size() == 0) {
                    throw new RuntimeException("Empty edge");
                }
                n.addChild(edge, child);
            }
        }

        return n;
    }

    /**
     * Combine an indeterminate state of an internal node with an external node which use indicated combining
     * algorithm.
     *
     * @param inState
     * @param externalNode
     * @return
     */
    private InternalNodeState combineInternalNodeStates(InternalNodeState inState, ExternalNode3 externalNode) {

        ExternalNode3 n1 = inState.getExternalNode();

        ExternalNode3 n = combineExternalNodes(n1, externalNode);

        return new InternalNodeState(n);
    }

    private InternalNodeState combineIndeterminateStates(InternalNodeState state1, InternalNodeState state2) {

        ExternalNode3 e1 = state1.getExternalNode();
        ExternalNode3 e2 = state2.getExternalNode();

        ExternalNode3 e = this.combineExternalNodes(e1, e2);

        return new InternalNodeState(e);
    }
}
