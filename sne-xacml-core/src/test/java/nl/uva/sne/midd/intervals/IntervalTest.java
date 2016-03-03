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
package nl.uva.sne.midd.intervals;

import java.util.List;

import com.google.common.collect.ImmutableList;

import org.junit.Test;

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.interval.EndPoint;
import nl.uva.sne.midd.interval.Interval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class IntervalTest {

    private static final List<Interval<Double>> EMPTY_LIST = ImmutableList.of();

    @Test
    public void testContainsMethod() throws MIDDException {
        assertTrue((Interval.of(1.0, 5.0, true, false)).contains(Interval.of(1.0, 3.0)));

        assertFalse((Interval.of(1.0, 5.0, false, true)).contains(Interval.of(1.0, 3.0, true, false)));

        assertTrue((Interval.of(1.0, 5.0, true, true)).contains(Interval.of(2.0, 5.0, true, true)));

        assertFalse((Interval.of(1.0, 5.0, true, false)).contains(Interval.of(2.0, 5.0, true, true)));

        final Interval<Double> upTo5 = Interval.from(EndPoint.Infinity.NEGATIVE, 5.0);

        final Interval<Double> upToInclude5 = Interval.from(EndPoint.Infinity.NEGATIVE, 5.0).closeRight(true); // (-inf, 5]

        assertTrue(Interval.from(EndPoint.Infinity.NEGATIVE, 5.0). //(-inf, 5)
                contains(upTo5));
        assertTrue(upToInclude5.contains(upTo5));

        assertFalse(upTo5.contains(upToInclude5));

        assertTrue(upTo5.contains(
                Interval.from(EndPoint.Infinity.NEGATIVE, 4.0).closeRight(true)));// (-inf, 4]

        assertTrue(upTo5.contains(
                Interval.from(EndPoint.Infinity.NEGATIVE, 4.0) // (-inf, 4)
        ));

        assertFalse(upTo5.contains(
                Interval.from(EndPoint.Infinity.NEGATIVE, 5.1) // (-inf, 5.1)
        ));

        assertFalse(upTo5.contains(
                Interval.from(EndPoint.Infinity.NEGATIVE, 5.1).closeRight(true)));// (-inf, 5.1]

        assertFalse(Interval.from(EndPoint.Infinity.POSITIVE, 4.0).contains(
                Interval.from(EndPoint.Infinity.NEGATIVE, 4.0)
        ));
    }

    @Test
    public void open_interval_includes_with_a_single_point() throws MIDDException {
        // (1,2) U [1] -> [1, 2)
        assertEquals(Interval.of(1.0, 2.0)
                        .closeLeft(true)
                        .closeRight(false),
                Interval.of(1.0, 2.0).includeBound(Interval.of(1.0)));

        // (1,2) U [2] -> (1, 2]
        assertEquals(Interval.of(1.0, 2.0)
                        .closeLeft(false)
                        .closeRight(true),
                Interval.of(1.0, 2.0).includeBound(Interval.of(2.0)));

        // [1] U (1,2) -> [1, 2)
        assertEquals(Interval.of(1.0, 2.0)
                        .closeLeft(true)
                        .closeRight(false),
                Interval.of(1.0).includeBound(Interval.of(1.0, 2.0)));

        // [2] U (1,2) -> (1, 2]
        assertEquals(Interval.of(1.0, 2.0)
                        .closeLeft(false)
                        .closeRight(true),
                Interval.of(2.0).includeBound(Interval.of(1.0, 2.0)));
    }

    @Test
    public void testComplement() throws MIDDException {
        final Interval<Double> from1To5 = new Interval<>(1.0, 5.0, true, true);

        assertEquals(Interval.of(1.0, 5.0, true, true), from1To5.complement(Interval.of(-3.0, -2.0, true, true)).get(0));
        assertEquals(Interval.of(1.0, 5.0, false, true), from1To5.complement(Interval.of(-3.0, 1.0, true, true)).get(0));
        assertEquals(Interval.of(1.0, 5.0, true, true), from1To5.complement(Interval.of(-3.0, 1.0, true, false)).get(0));
        assertEquals(Interval.of(2.5, 5.0, false, true), from1To5.complement(Interval.of(-3.0, 2.5, true, true)).get(0));
        assertEquals(Interval.of(2.5, 5.0, true, true), from1To5.complement(Interval.of(-3.0, 2.5, true, false)).get(0));
        assertEquals(Interval.of(5.0, 5.0, true, true), from1To5.complement(Interval.of(-3.0, 5.0, true, false)).get(0));

        assertEquals(EMPTY_LIST, from1To5.complement(new Interval<>(-3.0, 5.0, true, true)));
        assertEquals(EMPTY_LIST, from1To5.complement(new Interval<>(-3.0, 6.0, true, true)));
        assertEquals(EMPTY_LIST, from1To5.complement(new Interval<>(-3.0, 6.0, true, false)));

        assertEquals(Interval.of(1.0, 5.0, false, true), from1To5.complement(Interval.of(1.0, 1.0, true, true)).get(0));
        assertEquals(Interval.of(3.0, 5.0, false, true), from1To5.complement(Interval.of(1.0, 3.0, true, true)).get(0));
        assertEquals(Interval.of(5.0, 5.0, true, true), from1To5.complement(Interval.of(1.0, 5.0, true, false)).get(0));

        assertEquals(EMPTY_LIST, from1To5.complement(Interval.of(1.0, 5.0, true, true)));
        assertEquals(EMPTY_LIST, from1To5.complement(Interval.of(1.0, 7.0, true, true)));
        assertEquals(EMPTY_LIST, from1To5.complement(Interval.of(1.0, 7.0, true, false)));

        assertEquals(Interval.of(1.0, 2.0, true, false), from1To5.complement(Interval.of(2.0, 5.0, true, true)).get(0));
        assertEquals(Interval.of(1.0, 2.0, true, false), from1To5.complement(Interval.of(2.0, 7.0, true, true)).get(0));
        assertEquals(Interval.of(1.0, 5.0, true, false), from1To5.complement(Interval.of(5.0, 5.0, true, true)).get(0));
        assertEquals(Interval.of(1.0, 5.0, true, false), from1To5.complement(Interval.of(5.0, 7.0, true, true)).get(0));
        assertEquals(Interval.of(1.0, 5.0, true, true), from1To5.complement(Interval.of(7.0, 8.0, false, false)).get(0));

        // [1, 5] \ [2, 3) -> [1, 2) U [3, 5]
        List<Interval<Double>> c1 = from1To5.complement(Interval.of(2.0, 3.0, true, false));
        assertEquals(c1.size(), 2);
        assertTrue(c1.get(0).equals(Interval.of(1.0, 2.0, true, false)));
        assertTrue(c1.get(1).equals(Interval.of(3.0, 5.0, true, true)));

        // [1, 5] \ (2, 3] -> [1, 2] U (3, 5]
        List<Interval<Double>> c2 = from1To5.complement(Interval.of(2.0, 3.0, false, true));
        assertEquals(c2.size(), 2);
        assertTrue(c2.get(0).equals(Interval.of(1.0, 2.0, true, true)));
        assertTrue(c2.get(1).equals(Interval.of(3.0, 5.0, false, true)));

        // [1, 5] \ [2, 5) -> [1, 2) U [5]
        List<Interval<Double>> c3 = from1To5.complement(Interval.of(2.0, 5.0, true, false));
        assertEquals(c3.size(), 2);
        assertTrue(c3.get(0).equals(Interval.of(1.0, 2.0, true, false)));
        assertTrue(c3.get(1).equals(Interval.of(5.0, 5.0, true, true)));

    }

    @Test
    public void testComplement2() throws MIDDException {
        // (-inf, 3)
        Interval<Double> upTo3 = Interval.from(EndPoint.Infinity.NEGATIVE, 3.0);

        // (1, 4)
        final List<Interval<Double>> c1 = upTo3.complement(Interval.of(1.0, 4.0, true, true));
        assertEquals(c1.size(), 1);
        assertEquals(Interval.from(EndPoint.Infinity.NEGATIVE, 1.0), c1.get(0));

        // (1,2)
        final List<Interval<Double>> c2 = upTo3.complement(Interval.of(1.0, 2.0));
        assertNotNull(c2);
        assertEquals(c2.size(), 2);
        assertEquals(Interval.from(EndPoint.Infinity.NEGATIVE, 1.0).closeRight(true), c2.get(0));
        assertEquals(Interval.of(2.0, 3.0).closeLeft(true), c2.get(1));

        // (-inf, 0)
        final List<Interval<Double>> c3 = upTo3.complement(Interval.from(EndPoint.Infinity.NEGATIVE, 0.0));
        assertNotNull(c3);
        assertEquals(c3.size(), 1);
        assertEquals(Interval.of(0.0, 3.0, true, false), c3.get(0));

        // (0, inf)
        final List<Interval<Double>> c4 = upTo3.complement(Interval.from(EndPoint.Infinity.POSITIVE, 0.0));
        assertNotNull(c4);
        assertEquals(c4.size(), 1);
        assertEquals(Interval.from(EndPoint.Infinity.NEGATIVE, 0.0).closeRight(true), c4.get(0));
    }

    @Test
    public void testEquals() throws MIDDException {
        assertEquals(Interval.from(EndPoint.Infinity.NEGATIVE, 3.0), Interval.from(EndPoint.Infinity.NEGATIVE, 3.0));
        assertEquals(Interval.of(1.0, 2.0, true, false), Interval.of(1.0, 2.0).closeLeft(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_set_close_left_on_negative_infinite() throws MIDDException {
        Interval.from(EndPoint.Infinity.NEGATIVE, 1.0).closeLeft(true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_set_close_rights_on_negative_infinite() throws MIDDException {
        Interval.from(EndPoint.Infinity.POSITIVE, 1.0).closeRight(true);
    }

    @Test
    public void testToString() throws MIDDException {
        assertEquals("(-inf, 1.0)", Interval.from(EndPoint.Infinity.NEGATIVE, 1.0).toString());
        assertEquals("(-inf, 2.0]", Interval.from(EndPoint.Infinity.NEGATIVE, 2.0).closeRight(true).toString());

        assertEquals("(3.0, 5.0)", Interval.of(3.0, 5.0).toString());
        assertEquals("(3.0, 5.0]", Interval.of(3.0, 5.0).closeRight(true).toString());
        assertEquals("[3.0, 5.0)", Interval.of(3.0, 5.0).closeLeft(true).toString());
        assertEquals("[3.0, 5.0]", Interval.of(3.0, 5.0, true, true).toString());
    }
}
