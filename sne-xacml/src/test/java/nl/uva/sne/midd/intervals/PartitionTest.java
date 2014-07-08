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
package nl.uva.sne.midd.intervals;


import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.interval.EndPoint;
import nl.uva.sne.midd.interval.Interval;
import nl.uva.sne.midd.partition.Partition;
import nl.uva.sne.midd.partition.PartitionBuilder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class PartitionTest {

    @Test
    /**
     * {[1.0,2.0),(5.0,7.0)} U {[1.0,1.0],[2.0,3.0),(6.0,8.0)} ->
     * {[1.0,1.0],(1.0,2.0),[2.0,3.0),(5.0,6.0],(6.0,7.0),[7.0,8.0)}
     */
    public void testCombinePartition() throws MIDDException {

        List<Interval<Double>> listP1 = new ArrayList<Interval<Double>>();
        listP1.add(new Interval<Double>(1.0, 2.0, true, false));
        listP1.add(new Interval<Double>(5.0, 7.0));

        assertEquals(listP1.size(), 2);

        Partition<Double> p1 = new Partition<Double>(listP1);
        for (Interval<Double> i : listP1) {
            assertTrue(p1.containsInterval(i));
        }
        ///////////////
        List<Interval<Double>> listP2 = new ArrayList<Interval<Double>>();
        listP2.add(new Interval<Double>(1.0));
        listP2.add(new Interval<Double>(2.0, 3.0, true, false));
        listP2.add(new Interval<Double>(6.0, 8.0));

        assertEquals(listP2.size(), 3);

        Partition<Double> p2 = new Partition<Double>(listP2);
        for (Interval<Double> i : listP2) {
            assertTrue(p2.containsInterval(i));
        }
        ///////////////////
        // Combine p1 and p2, output should be: [1], (1,2), [2, 3), (5,6], (6,7), [7, 8)
        List<Interval<Double>> listP = new ArrayList<Interval<Double>>();
        listP.add(new Interval<Double>(1.0));
        listP.add(new Interval<Double>(1.0, 2.0));
        listP.add(new Interval<Double>(2.0, 3.0, true, false));
        listP.add(new Interval<Double>(5.0, 6.0, false, true));
        listP.add(new Interval<Double>(6.0, 7.0));
        listP.add(new Interval<Double>(7.0, 8.0, true, false));

        //Perform combine two partitions
        Partition<Double> p = PartitionBuilder.union(p1, p2);

        // verify output size
        assertEquals(listP.size(), p.size());
        // verify output items
        for (Interval<Double> i : listP) {
            assertTrue(p.containsInterval(i));
        }

        //print output
        System.out.println(p1.toString() + " U " + p2.toString() + " -> " + p.toString());
    }

    @Test
    /**
     * {[1.0,2.0],[3.0,4.0]} U {[1.0,1.0],[2.0,3.0],[4.0,4.0]} ->
     * {[1.0,1.0],(1.0,2.0),[2.0,2.0],(2.0,3.0),[3.0,3.0],(3.0,4.0),[4.0,4.0]}
     */
    public void testCombinePartition2() throws MIDDException {
        List<Interval<Double>> listP1 = new ArrayList<Interval<Double>>();
        listP1.add(new Interval<Double>(1.0, 2.0, true, true));
        listP1.add(new Interval<Double>(3.0, 4.0, true, true));

        List<Interval<Double>> listP2 = new ArrayList<Interval<Double>>();
        listP2.add(new Interval<Double>(1.0));
        listP2.add(new Interval<Double>(2.0, 3.0, true, true));
        listP2.add(new Interval<Double>(4.0));

        List<Interval<Double>> listP = new ArrayList<Interval<Double>>();
        listP.add(new Interval<Double>(1.0));
        listP.add(new Interval<Double>(1.0, 2.0));
        listP.add(new Interval<Double>(2.0));
        listP.add(new Interval<Double>(2.0, 3.0));
        listP.add(new Interval<Double>(3.0));
        listP.add(new Interval<Double>(3.0, 4.0));
        listP.add(new Interval<Double>(4.0));

        Partition<Double> p1 = new Partition<Double>(listP1);
        Partition<Double> p2 = new Partition<Double>(listP2);

        Partition<Double> p = PartitionBuilder.union(p1, p2);

        // verify output size
        assertEquals(listP.size(), p.size());
        // verify output items
        for (Interval<Double> i : listP) {
            assertTrue(p.containsInterval(i));
        }

        System.out.println(p1.toString() + " U " + p2.toString() + " -> " + p.toString());
    }

    @Test
    /**
     * {[3.0,4.0],[1.0,2.0]} U {[4.0,4.0]} -> {[1.0,2.0],[3.0,4.0),[4.0,4.0]}
     */
    public void testCombinePartition3() throws MIDDException {
        List<Interval<Double>> listP1 = new ArrayList<Interval<Double>>();
        listP1.add(new Interval<Double>(3.0, 4.0, true, true));
        listP1.add(new Interval<Double>(1.0, 2.0, true, true));

        List<Interval<Double>> listP2 = new ArrayList<Interval<Double>>();
        listP2.add(new Interval<Double>(4.0));

        Partition<Double> p1 = new Partition<Double>(listP1);
        Partition<Double> p2 = new Partition<Double>(listP2);

        Partition<Double> p = PartitionBuilder.union(p1, p2);

        System.out.println(p1.toString() + " U " + p2.toString() + " -> " + p.toString());
    }

    @Test
    /**
     * {(-inf,1.0],(3.0,4.0],[6.0,8.0)} U {(-2.0,2.0),[4.0,5.5],(8.0,10.0]} ->
     * {(-inf,-2.0],(-2.0,1.0],(1.0,2.0),(3.0,4.0),[4.0,4.0],(4.0,5.5],[6.0,8.0),(8.0,10.0]}
     */
    public void testCombinePartition4() throws MIDDException {
        List<Interval<Double>> listP1 = new ArrayList<Interval<Double>>();
        listP1.add(new Interval<Double>(new EndPoint<Double>(true, false), new EndPoint<Double>(1.0), false, true));    // (-inf, 1]
        listP1.add(new Interval<Double>(3.0, 4.0, false, true));
        listP1.add(new Interval<Double>(6.0, 8.0, true, false));

        List<Interval<Double>> listP2 = new ArrayList<Interval<Double>>();
        listP2.add(new Interval<Double>(-2.0, 2.0));                // (-2, 2)
        listP2.add(new Interval<Double>(4.0, 5.5, true, true));        // [4, 5.5]
        listP2.add(new Interval<Double>(8.0, 10.0, false, true));    // (8, 10]

        Partition<Double> p1 = new Partition<Double>(listP1);
        Partition<Double> p2 = new Partition<Double>(listP2);

        Partition<Double> p = PartitionBuilder.union(p1, p2);

        System.out.println(p1.toString() + " U " + p2.toString() + " -> " + p.toString());
    }
}
