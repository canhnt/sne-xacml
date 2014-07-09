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

import org.junit.Test;

import nl.uva.sne.midd.interval.Interval;
import nl.uva.sne.midd.utils.IntervalUtil;

public class IntervalUtilTest {

    @Test
    public void testComplement() throws MIDDException {
        List<Interval<Double>> intervals = new ArrayList<Interval<Double>>();

        intervals.add(new Interval<Double>(3.0));
        intervals.add(new Interval<Double>(1.0, 2.0, true, true));
        intervals.add(new Interval<Double>(3.0, 4.0, true, true));


        for (Interval<Double> i : intervals) {
            List<Interval> lst = new ArrayList<Interval>();
            lst.add(i);
            System.out.println("Complement of: " + i + " -> " + IntervalUtil.complement(lst));
        }
    }

    @Test
    public void testComplement2() throws MIDDException {
        List<Interval> intervals = new ArrayList<Interval>();

        intervals.add(new Interval<Double>(2.0, 3.0, true, false));
        intervals.add(new Interval<Double>(4.0, 6.0, true, true));

        System.out.print("Complement of ");
        for (Interval<Double> i : intervals) {
            System.out.print(i);
        }
        System.out.println(" -> " + IntervalUtil.complement(intervals));
    }
}
