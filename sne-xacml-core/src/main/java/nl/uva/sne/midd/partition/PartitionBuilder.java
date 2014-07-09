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
/**
 * System and Network Engineering Group
 * University of Amsterdam
 *
 */
package nl.uva.sne.midd.partition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.interval.EndPoint;
import nl.uva.sne.midd.interval.Interval;

/**
 * @author Canh Ngo (t.c.ngo@uva.nl)
 * @date: Sep 17, 2012
 */
public class PartitionBuilder {
    private static final Logger log = LoggerFactory.getLogger(PartitionBuilder.class);

    public static <T extends Comparable<T>> Partition<T> createPartition(List<Interval<T>> intervals) {
        return new Partition<T>(intervals);
    }

    /**
     * Combine two disjoint interval partitions into a single disjoint partition. Condition: every interval of the
     * result partition belongs to at least an old partition
     * <p/>
     * Version 2
     *
     * @param <T>
     * @param p1
     * @param p2
     * @return
     */
    public static <T extends Comparable<T>> Partition<T> union(Partition<T> p1, Partition<T> p2) throws MIDDException {

        if (p1.size() == 0) {
            return p2;
        }
        if (p2.size() == 0) {
            return p1;
        }

        Set<EndPoint<T>> boundSet = new HashSet<EndPoint<T>>();

        //collect all bounds and remove duplicated items
        for (Interval<T> interval : p1.getIntervals()) {
            boundSet.add(interval.getLowerBound());
            boundSet.add(interval.getUpperBound());
        }
        for (Interval<T> interval : p2.getIntervals()) {
            boundSet.add(interval.getLowerBound());
            boundSet.add(interval.getUpperBound());
        }
        if (boundSet.size() == 0) {
            throw new RuntimeException("no bounds");
        }
        //sorting the bounds
        List<EndPoint<T>> sortedBounds = new ArrayList<EndPoint<T>>(boundSet);
        Collections.sort(sortedBounds);

        List<Interval<T>> tempPartition = new ArrayList<Interval<T>>();

        // Generate new intervals
        for (int i = 0; i < sortedBounds.size() - 1; i++) {
            EndPoint<T> lowBound = sortedBounds.get(i);
            EndPoint<T> upBound = sortedBounds.get(i + 1);
            tempPartition.add(new Interval<T>(lowBound));
            tempPartition.add(new Interval<T>(lowBound, upBound));
        }
        // add the last end-point
        if (sortedBounds.size() > 0) {
            EndPoint<T> lastBound = sortedBounds.get(sortedBounds.size() - 1);
            tempPartition.add(new Interval<T>(lastBound));
        } else {
            log.warn("Empty sortedBound");
        }
        // combine adjacent intervals which belong to the same type
        List<Interval<T>> outputPartition = new ArrayList<Interval<T>>();

        Interval<T> newInterval = null;
        Interval<T> superOfNewIntervalP1 = null;
        Interval<T> superOfNewIntervalP2 = null;
//		int newIntervalType = 0;
/**
 * New version, updated at 2:00PM, 09/19/2012		
 */
        for (int i = 0; i < tempPartition.size(); i++) {
            Interval<T> tempInterval = tempPartition.get(i);
            Interval<T> superOfTempIntervalP1 = p1.getSuperInterval(tempInterval);
            Interval<T> superOfTempIntervalP2 = p2.getSuperInterval(tempInterval);

            if (superOfTempIntervalP1 == null && superOfTempIntervalP2 == null) // not belong to any partitions, ignore it
            {
                continue;
            }

            if (newInterval == null) {
                newInterval = tempInterval;
                superOfNewIntervalP1 = superOfTempIntervalP1;
                superOfNewIntervalP2 = superOfTempIntervalP2;
                continue;
            }

            if (superOfNewIntervalP1 == superOfTempIntervalP1 && superOfNewIntervalP2 == superOfTempIntervalP2) {
                newInterval.includeBound(tempInterval);
            } else {
                outputPartition.add(newInterval);
                newInterval = tempInterval;
                superOfNewIntervalP1 = superOfTempIntervalP1;
                superOfNewIntervalP2 = superOfTempIntervalP2;
            }

        }
        if (newInterval != null) {
            outputPartition.add(newInterval);
        }
        return new Partition<T>(outputPartition);
    }

    /**
     * Return the intersect of two disjoint partition. The returned disjoint partition has items that: - Each interval
     * item is a subset of only an interval I1 \in p1 and a subset of only an interval I2 \in p2.
     *
     * @param p1
     * @param p2
     * @return
     */
    public static <T extends Comparable<T>> Partition<T> intersect(Partition<T> p1, Partition<T> p2) throws MIDDException {

        if (p1.size() == 0 || p2.size() == 0) {
            return new Partition<T>();
        }

        List<Interval<T>> tempIntervals = combine(p1, p2);

        Interval<T> newInterval = null;
        Interval<T> superOfNewIntervalP1 = null;
        Interval<T> superOfNewIntervalP2 = null;

        // combine adjacent intervals which belong to the same type
        List<Interval<T>> outputPartition = new ArrayList<Interval<T>>();

        for (int i = 0; i < tempIntervals.size(); i++) {
            Interval<T> tempInterval = tempIntervals.get(i);
            Interval<T> superOfTempIntervalP1 = p1.getSuperInterval(tempInterval);
            Interval<T> superOfTempIntervalP2 = p2.getSuperInterval(tempInterval);

            // if it belongs to at most one partition, ignore!
            if (superOfTempIntervalP1 == null || superOfTempIntervalP2 == null) {
                continue;
            }

            if (newInterval == null) {
                newInterval = tempInterval;
                superOfNewIntervalP1 = superOfTempIntervalP1;
                superOfNewIntervalP2 = superOfTempIntervalP2;
                continue;
            }

            if (superOfNewIntervalP1 == superOfTempIntervalP1 && superOfNewIntervalP2 == superOfTempIntervalP2) {
                newInterval.includeBound(tempInterval);
            } else {
                outputPartition.add(newInterval);
                newInterval = tempInterval;
                superOfNewIntervalP1 = superOfTempIntervalP1;
                superOfNewIntervalP2 = superOfTempIntervalP2;
            }

        }
        if (newInterval != null) {
            outputPartition.add(newInterval);
        }
        return new Partition<T>(outputPartition);
    }

    /**
     * Create a list of intervals from two partitions
     *
     * @param p1
     * @param p2
     * @return
     */
    private static <T extends Comparable<T>> List<Interval<T>> combine(Partition<T> p1, Partition<T> p2) throws MIDDException {

        Set<EndPoint<T>> boundSet = new HashSet<EndPoint<T>>();

        //collect all bounds and remove duplicated items
        for (Interval<T> interval : p1.getIntervals()) {
            boundSet.add(interval.getLowerBound());
            boundSet.add(interval.getUpperBound());
        }
        for (Interval<T> interval : p2.getIntervals()) {
            boundSet.add(interval.getLowerBound());
            boundSet.add(interval.getUpperBound());
        }

        //sorting the bounds
        List<EndPoint<T>> sortedBounds = new ArrayList<EndPoint<T>>(boundSet);
        Collections.sort(sortedBounds);

        List<Interval<T>> tempPartition = new ArrayList<Interval<T>>();

        // Generate new intervals
        for (int i = 0; i < sortedBounds.size() - 1; i++) {
            EndPoint<T> lowBound = sortedBounds.get(i);
            EndPoint<T> upBound = sortedBounds.get(i + 1);
            tempPartition.add(new Interval<T>(lowBound));
            tempPartition.add(new Interval<T>(lowBound, upBound));
        }
        // add the last end-point
        if (sortedBounds.size() > 0) {
            EndPoint<T> lastBound = sortedBounds.get(sortedBounds.size() - 1);
            tempPartition.add(new Interval<T>(lastBound));
        } else {
            log.warn("empty sortedBound");

        }
        return tempPartition;
    }
}
