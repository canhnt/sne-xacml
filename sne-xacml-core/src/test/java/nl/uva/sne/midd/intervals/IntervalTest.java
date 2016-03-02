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
package nl.uva.sne.midd.intervals;

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.interval.EndPoint;
import nl.uva.sne.midd.interval.Interval;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class IntervalTest {

    private static Interval<Double> i1;

    private static Interval<Double> i2;

    private static Interval<Double> i3;

    private static Interval<Double> i4;

    private static Interval<Double> i5;

    static {
        try {
            i4 = new Interval<Double>(1.0, 2.0, true, false);
            i5 = new Interval<Double>(1.0, 2.0, false, true);
            i3 = new Interval<Double>(2.0);
            i2 = new Interval<Double>(1.0);
            i1 = new Interval<Double>(1.0, 2.0);
        } catch (MIDDException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testContainsMethod() throws MIDDException {
        assertTrue((new Interval<>(1.0, 5.0, true, false)).contains(new Interval<>(1.0, 3.0)));

        assertFalse((new Interval<>(1.0, 5.0, false, true)).contains(new Interval<>(1.0, 3.0, true, false)));

        assertTrue((new Interval<>(1.0, 5.0, true, true)).contains(new Interval<>(2.0, 5.0, true, true)));

        assertFalse((new Interval<>(1.0, 5.0, true, false)).contains(new Interval<>(2.0, 5.0, true, true)));

        Interval<Double> it1 = new Interval<>(new EndPoint<Double>(EndPoint.Infinity.NEGATIVE), new EndPoint<>(5.0)); //(-inf, 5)

        Interval<Double> it11 = new Interval<>(it1); // (-inf, 5]
        it11.setUpperBoundClosed(true);

        Interval<Double> it2 = new Interval<>(new EndPoint<Double>(EndPoint.Infinity.NEGATIVE), new EndPoint<>(5.0)); //(-inf, 5)
        assertTrue(it1.contains(it2));
        assertTrue(it11.contains(it2));

        it2.setUpperBoundClosed(true); // it2 = (-inf, 5]
        assertFalse(it1.contains(it2));


        it2.setUpperBound(4.0); // (-inf, 4]
        assertTrue(it1.contains(it2));


        it2.setUpperBoundClosed(false); // (-inf, 4)
        assertTrue(it1.contains(it2));

        it2.setUpperBound(5.1);
        it2.setUpperBoundClosed(false); // (-inf, 5.1)
        assertFalse(it1.contains(it2));
        it2.setUpperBoundClosed(true); // (-inf, 5.1]
        assertFalse(it1.contains(it2));


        // (4, inf)
        it1.setLowerBound(4.0);
        it1.setLowerBoundClosed(false);
        it1.setUpperInfinite();

        assertFalse(it1.contains(it2));

    }

//	@Test
//	public void testIsIntersect() {
//		assertTrue((new Interval<Double>(1.0, 3.0)).isIntersect(new Interval<Double>(2.0, 4.0)));
//		
//		assertTrue((new Interval<Double>(1.0, 5.0)).isIntersect(new Interval<Double>(-2.0, 2.0)));
//		
//		assertTrue((new Interval<Double>(1.0, 5.0)).isIntersect(new Interval<Double>(2.0, 3.0)));
//		
//		assertTrue((new Interval<Double>(1.0, 5.0)).isIntersect(new Interval<Double>(-2.0, 6.0)));
//	}

    @Test
    public void testIncludeBound() throws MIDDException {
        // (1,2) U [1] -> [1, 2)
        Interval<Double> t1 = new Interval<Double>(i1);
        System.out.print(t1.toString() + " U " + i2.toString() + "->");
        t1.includeBound(i2);
        System.out.println(t1.toString());
        assertTrue(t1.equals(i4));


        // [1] U (1,2) -> [1, 2)
        Interval<Double> t2 = new Interval<Double>(i2);
        System.out.print(t2.toString() + " U " + i1.toString() + "->");
        t2.includeBound(i1);
        System.out.println(t2.toString());
        assertTrue(t2.equals(i4));

        // (1,2) U [2] -> (1, 2]
        Interval<Double> t3 = new Interval<Double>(i1);
        System.out.print(t3.toString() + " U " + i3.toString() + "->");
        t3.includeBound(i3);
        System.out.println(t3.toString());
        assertTrue(t3.equals(i5));

        // [2] U (1,2) -> (1, 2]
        Interval<Double> t4 = new Interval<Double>(i3);
        System.out.print(t4.toString() + " U " + i1.toString() + "->");
        t4.includeBound(i1);
        System.out.println(t4.toString());
        assertTrue(t4.equals(i5));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testComplement() throws MIDDException {
        Interval<Double> i1 = new Interval<Double>(1.0, 5.0, true, true);

        Interval<Double> targets[] = new Interval[]{
                new Interval<Double>(-3.0, -2.0, true, true),
                new Interval<Double>(-3.0, 1.0, true, true),
                new Interval<Double>(-3.0, 1.0, true, false),
                new Interval<Double>(-3.0, 2.5, true, true),
                new Interval<Double>(-3.0, 2.5, true, false),
                new Interval<Double>(-3.0, 5.0, true, false),
                new Interval<Double>(-3.0, 5.0, true, true),
                new Interval<Double>(-3.0, 6.0, true, true),
                new Interval<Double>(-3.0, 6.0, true, false),

                new Interval<Double>(1.0, 1.0, true, true),
                new Interval<Double>(1.0, 3.0, true, true),
                new Interval<Double>(1.0, 5.0, true, false),
                new Interval<Double>(1.0, 5.0, true, true),
                new Interval<Double>(1.0, 7.0, true, true),
                new Interval<Double>(1.0, 7.0, true, false),

                new Interval<Double>(2.0, 5.0, true, true),
                new Interval<Double>(2.0, 7.0, true, true),

                new Interval<Double>(5.0, 5.0, true, true),
                new Interval<Double>(5.0, 7.0, true, true),

                new Interval<Double>(7.0, 8.0, false, false)
        };

        Interval<Double> results[] = new Interval[]{
                new Interval<Double>(1.0, 5.0, true, true),
                new Interval<Double>(1.0, 5.0, false, true),
                new Interval<Double>(1.0, 5.0, true, true),
                new Interval<Double>(2.5, 5.0, false, true),
                new Interval<Double>(2.5, 5.0, true, true),
                new Interval<Double>(5.0, 5.0, true, true),
                null,
                null,
                null,

                new Interval<Double>(1.0, 5.0, false, true),
                new Interval<Double>(3.0, 5.0, false, true),
                new Interval<Double>(5.0, 5.0, true, true),
                null,
                null,
                null,

                new Interval<Double>(1.0, 2.0, true, false),
                new Interval<Double>(1.0, 2.0, true, false),

                new Interval<Double>(1.0, 5.0, true, false),
                new Interval<Double>(1.0, 5.0, true, false),

                new Interval<Double>(1.0, 5.0, true, true)
        };
//		
        for (int i = 0; i < targets.length; i++) {
            List<Interval<Double>> c = i1.complement(targets[i]);
            if (null != c) {
                assertEquals(c.size(), 1);
                assertTrue(c.get(0).equals(results[i]));
            } else {
                assertEquals(c, results[i]);
            }

        }

        // [1, 5] \ [2, 3) -> [1, 2) U [3, 5]
        List<Interval<Double>> c1 = i1.complement(new Interval<Double>(2.0, 3.0, true, false));
        assertEquals(c1.size(), 2);
        assertTrue(c1.get(0).equals(new Interval<Double>(1.0, 2.0, true, false)));
        assertTrue(c1.get(1).equals(new Interval<Double>(3.0, 5.0, true, true)));

        // [1, 5] \ (2, 3] -> [1, 2] U (3, 5]
        List<Interval<Double>> c2 = i1.complement(new Interval<Double>(2.0, 3.0, false, true));
        assertEquals(c2.size(), 2);
        assertTrue(c2.get(0).equals(new Interval<Double>(1.0, 2.0, true, true)));
        assertTrue(c2.get(1).equals(new Interval<Double>(3.0, 5.0, false, true)));

        // [1, 5] \ [2, 5) -> [1, 2) U [5]
        List<Interval<Double>> c3 = i1.complement(new Interval<Double>(2.0, 5.0, true, false));
        assertEquals(c3.size(), 2);
        assertTrue(c3.get(0).equals(new Interval<Double>(1.0, 2.0, true, false)));
        assertTrue(c3.get(1).equals(new Interval<Double>(5.0, 5.0, true, true)));

    }

    @Test
    public void testComplement2() throws MIDDException {
        // (-inf, 3]
        Interval<Double> i1 = new Interval<>(new EndPoint<Double>(EndPoint.Infinity.NEGATIVE),
                new EndPoint<>(3.0), false, false);

        // (1, 4)
        List<Interval<Double>> c1 = i1.complement(new Interval<>(1.0, 4.0, true, true));
        assertNotNull(c1);
        assertEquals(c1.size(), 1);
        assertTrue(c1.get(0).equals(new Interval<>(new EndPoint<Double>(EndPoint.Infinity.NEGATIVE),
                new EndPoint<>(1.0), false, false)));

        // (1,2)
        List<Interval<Double>> c2 = i1.complement(new Interval<>(1.0, 2.0, false, false));
        assertNotNull(c2);
        assertEquals(c2.size(), 2);
        assertTrue(c2.get(0).equals(new Interval<>(new EndPoint<Double>(EndPoint.Infinity.NEGATIVE),
                new EndPoint<>(1.0), false, true)));
        assertTrue(c2.get(1).equals(new Interval<>(2.0, 3.0, true, false)));

        // (-inf, 0)
        List<Interval<Double>> c3 = i1.complement(new Interval<>(new EndPoint<Double>(EndPoint.Infinity.NEGATIVE),
                new EndPoint<Double>(0.0), false, false));
        assertNotNull(c3);
        assertEquals(c3.size(), 1);
        assertTrue(c3.get(0).equals(new Interval<>(0.0, 3.0, true, false)));

        // (0, inf)
        List<Interval<Double>> c4 = i1.complement(new Interval<>(new EndPoint<>(0.0),
                new EndPoint<Double>(EndPoint.Infinity.POSITIVE), false, false));
        assertNotNull(c4);
        assertEquals(c4.size(), 1);
        assertTrue(c4.get(0).equals(new Interval<>(new EndPoint<Double>(EndPoint.Infinity.NEGATIVE),
                new EndPoint<>(0.0), false, true)));
    }

    @Test
    public void testEquals() throws MIDDException {
        Interval<Double> i1 = new Interval<>(new EndPoint<Double>(EndPoint.Infinity.NEGATIVE),
                new EndPoint<>(3.0), false, false);
        Interval<Double> i2 = new Interval<>(new EndPoint<Double>(EndPoint.Infinity.NEGATIVE),
                new EndPoint<>(3.0), false, false);

        assertTrue(i1.equals(i2));
        assertTrue(i4.equals(new Interval<>(1.0, 2.0, true, false)));
    }

}
