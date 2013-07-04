/**
 * SNE-XACML: A high performance XACML evaluation engine.
 *
 * Copyright (C) 2013 Canh T. Ngo <canhnt@gmail.com>
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
/**
 * System and Network Engineering Group
 * University of Amsterdam
 *
 */
package nl.uva.sne.midd.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import nl.uva.sne.midd.edges.AbstractEdge;
import nl.uva.sne.midd.interval.EndPoint;
import nl.uva.sne.midd.interval.Interval;
import nl.uva.sne.midd.nodes.AbstractNode;
import nl.uva.sne.midd.nodes.InternalNode;

/**
 * @author Canh Ngo (t.c.ngo@uva.nl)
 *
 * @version 
 * @date: Sep 13, 2012
 */
public class IntervalUtil {

	/**
	 * Return the complement of set of intervals
	 * 
	 * @param intervals
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Interval> complement(
			List<Interval> intervals) {
		
		Interval w = new Interval(new EndPoint(true, false), new EndPoint(false, true));
		List<Interval> op1 = new ArrayList<Interval>();
		op1.add(w);
				
				
		for(Interval op2 : intervals) {
			List<Interval> result = new ArrayList<Interval>();
			
			for(Interval item:op1) {
				if (item.isIntersec(op2)) {
					List<Interval> l = item.complement(op2);
					if (l != null && l.size() > 0)
						result.addAll(l);
				} else
					result.add(item);					
			}
			op1 = result;
		}
		
		return op1;		
	}
	
	

}
