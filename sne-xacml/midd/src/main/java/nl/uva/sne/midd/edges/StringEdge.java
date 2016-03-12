/*
 * Copyright (C) 2013-2014 Canh Ngo <canhnt@gmail.com>
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

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.interval.Interval;

import java.util.List;

/**
 * @author Canh Ngo
 * @date: Sep 20, 2012
 */
public class StringEdge extends AbstractEdge<String> {

    /**
     * @param interval
     */
    public StringEdge(Interval<String> interval) throws MIDDException {
        super(interval);
    }

    /**
     * @param intervals
     */
    public StringEdge(List<Interval<String>> intervals) {
        super(intervals);
    }

    public StringEdge(StringEdge e) throws MIDDException {
        super(e);
    }
    /* (non-Javadoc)
     * @see nl.uva.sne.midd.AbstractEdge#getType()
     */
    @Override
    public Class<String> getType() {
        return String.class;
    }

}
