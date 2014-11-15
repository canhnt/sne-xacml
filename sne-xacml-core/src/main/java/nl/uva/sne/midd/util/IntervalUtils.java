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
package nl.uva.sne.midd.util;

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.interval.EndPoint;
import nl.uva.sne.midd.interval.Interval;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Canh Ngo
 */
public class IntervalUtils {

    /**
     * Return the complement of set of intervals
     *
     * @param intervals
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List<Interval> complement(
            List<Interval> intervals) throws MIDDException {

        Interval w = new Interval(new EndPoint(true, false), new EndPoint(false, true));
        List<Interval> op1 = new ArrayList<Interval>();
        op1.add(w);

        for (Interval op2 : intervals) {
            List<Interval> result = new ArrayList<Interval>();

            for (Interval item : op1) {
                if (item.isIntersec(op2)) {
                    List<Interval> l = item.complement(op2);
                    if (l != null && l.size() > 0) {
                        result.addAll(l);
                    }
                } else {
                    result.add(item);
                }
            }
            op1 = result;
        }
        return op1;
    }
}