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
package nl.uva.sne.midd.edges;

import java.util.List;

import nl.uva.sne.midd.interval.Interval;

/**
 * @author Canh Ngo (t.c.ngo@uva.nl)
 * @date: Sep 12, 2012
 */
public class DoubleEdge extends AbstractEdge<Double> {

    /**
     * @param interval
     */
    public DoubleEdge(Interval<Double> interval) {
        super(interval);
    }

    /**
     * @param intervals
     */
    public DoubleEdge(List<Interval<Double>> intervals) {
        super(intervals);
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }
}
