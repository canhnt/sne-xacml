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
package nl.uva.sne.midd;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import nl.uva.sne.midd.interval.EndPoint;
import nl.uva.sne.midd.interval.Interval;
import nl.uva.sne.midd.util.IntervalUtils;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class IntervalUtilTest {


    @Test
    public void complement_of_single_value() throws MIDDException {
        //(-inf,3.0)
        final Interval<?> expected1 = new Interval<>(new EndPoint<Double>(EndPoint.Infinity.NEGATIVE), new EndPoint<>(3.0), false, false);
        // (3.0,inf)
        final Interval<?> expected2 = new Interval<>(new EndPoint<>(3.0), new EndPoint<Double>(EndPoint.Infinity.POSITIVE), false, false);

        final List<Interval<?>> result = IntervalUtils.complement(new Interval<>(3.0));
        assertThat(result, containsInAnyOrder(expected1, expected2));
    }

    @Test
    public void complement_of_full_closed_interval() throws MIDDException {
        final List<Interval<?>> result = IntervalUtils.complement(new Interval<>(1.0, 2.0, true, true));

        //(-inf,1.0)
        final Interval<?> expected1 = new Interval<>(new EndPoint<Double>(EndPoint.Infinity.NEGATIVE), new EndPoint<>(1.0), false, false);
        // (2.0,inf)
        final Interval<?> expected2 = new Interval<>(new EndPoint<>(2.0), new EndPoint<Double>(EndPoint.Infinity.POSITIVE), false, false);

        assertThat(result, containsInAnyOrder(expected1, expected2));
    }

    @Test
    public void complement_of_half_closed_interval() throws MIDDException {
        final List<Interval<?>> result = IntervalUtils.complement(new Interval<>(1.0, 2.0, true, false));

        //(-inf,1.0)
        final Interval<?> expected1 = new Interval<>(new EndPoint<Double>(EndPoint.Infinity.NEGATIVE), new EndPoint<>(1.0), false, false);
        // [2.0,inf)
        final Interval<?> expected2 = new Interval<>(new EndPoint<>(2.0), new EndPoint<Double>(EndPoint.Infinity.POSITIVE), true, false);

        assertThat(result, containsInAnyOrder(expected1, expected2));
    }

    @Test
    public void complement_of_open_interval() throws MIDDException {
        final List<Interval<?>> result = IntervalUtils.complement(new Interval<>(1.0, 2.0, false, false));

        //(-inf,1.0]
        final Interval<?> expected1 = new Interval<>(new EndPoint<Double>(EndPoint.Infinity.NEGATIVE), new EndPoint<>(1.0), false, true);
        // [2.0,inf)
        final Interval<?> expected2 = new Interval<>(new EndPoint<>(2.0), new EndPoint<Double>(EndPoint.Infinity.POSITIVE), true, false);

        assertThat(result, containsInAnyOrder(expected1, expected2));
    }

    @Test
    public void complement_of_multiple_intervals() throws MIDDException {
        final List<Interval> intervals = new ArrayList<>();

        intervals.add(new Interval<>(2.0, 3.0, true, false));
        intervals.add(new Interval<>(4.0, 6.0, true, true));

        // (-inf, 2)
        final Interval<?> expected1 = new Interval<>(new EndPoint<Double>(EndPoint.Infinity.NEGATIVE), new EndPoint<>(2.0), false, false);
        // [3, 4)
        final Interval<?> expected2 = new Interval<>(new EndPoint<>(3.0), new EndPoint<>(4.0), true, false);
        // (6, +inf)
        final Interval<?> expected3 = new Interval<>(new EndPoint<>(6.0), new EndPoint<Double>(EndPoint.Infinity.POSITIVE), false, false);

        final List<Interval<?>> result = IntervalUtils.complement(intervals);
        assertThat(result, containsInAnyOrder(expected2, expected1, expected3));
    }
}
