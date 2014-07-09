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

import org.junit.Test;

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.interval.EndPoint;
import static org.junit.Assert.assertTrue;

public class EndPointTest {

    @Test
    public void testCompareTo() throws MIDDException {
        EndPoint<Integer> pi1 = new EndPoint<Integer>(10);
        EndPoint<Integer> pi2 = new EndPoint<Integer>(false, true);
        EndPoint<Integer> pi3 = new EndPoint<Integer>(true, false);

        EndPoint<Double> pd1 = new EndPoint<Double>(15.0);

        assertTrue(pi1.compareTo(pi2) < 0);
        assertTrue(pi1.compareTo(pi3) > 0);
        assertTrue(pi2.compareTo(pi3) > 0);

        EndPoint<Integer> pi4 = new EndPoint<Integer>(40);
        EndPoint<Integer> pi5 = new EndPoint<Integer>(100);
        assertTrue(pi4.compareTo(pi5) < 0);

    }

    @Test
    public void testEquals() throws MIDDException {
        EndPoint<Integer> pi1 = new EndPoint<Integer>(10);
        assertTrue(pi1.equals(new EndPoint<Integer>(10)));

        EndPoint<Double> pd1 = new EndPoint<Double>(15.0);
        assertTrue(pd1.equals(new EndPoint<Double>(15.0)));

        EndPoint<Double> pd2 = new EndPoint<Double>(true, false);
        assertTrue(pd2.equals(new EndPoint<Double>(true, false)));

        EndPoint<Double> pd3 = new EndPoint<Double>(false, true);
        assertTrue(pd3.equals(new EndPoint<Double>(false, true)));

    }


}
