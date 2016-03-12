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
package nl.uva.sne.midd.util;

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.interval.EndPoint;
import nl.uva.sne.midd.interval.Interval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;

import com.google.common.collect.ImmutableList;

/**
 * @author Canh Ngo
 */
public class IntervalUtils {

    /**
     * Return the complement of list of intervals, i.e., (-inf, +inf) \ intervals
     *
     * @param intervals
     * @return the immutable list of intervals that complement with the input
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List<Interval<?>> complement(List<Interval> intervals) throws MIDDException {

        final Interval w = new Interval(new EndPoint(EndPoint.Infinity.NEGATIVE), new EndPoint(EndPoint.Infinity.POSITIVE));
        List<Interval<?>> op1 = new ArrayList<>();
        op1.add(w);

        for (Interval op2 : intervals) {
            final List<Interval<?>> result = new ArrayList<>();

            for (Interval item : op1) {
                if (item.isIntersec(op2)) {
                    final List<Interval<?>> l = item.complement(op2);
                    if (!l.isEmpty()) {
                        result.addAll(l);
                    }
                } else {
                    result.add(item);
                }
            }
            op1 = result;
        }
        return ImmutableList.copyOf(op1);
    }

    public static List<Interval<?>> complement(final Interval interval) throws MIDDException {
        return complement(Arrays.asList(interval));
    }
}
