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
package nl.uva.sne.midd.partition;

import java.util.ArrayList;
import java.util.List;

import nl.uva.sne.midd.interval.Interval;

/**
 * @author Canh Ngo (t.c.ngo@uva.nl)
 *
 * @version 
 * @date: Sep 7, 2012
 */
public class Partition <T extends Comparable<T>> {
	private List<Interval<T>> intervals;

	
	/**
	 * Create empty partition
	 */
	public Partition() {
		this.intervals = new ArrayList<Interval<T>>();
	}
	
	public Partition(List<Interval<T>> intervals){
		this.intervals = new ArrayList<Interval<T>>(intervals);
	}
	
	/**
	 * Return true if the argument interval is a subset of an interval of the partition
	 * @param i
	 * @return
	 */
	public boolean containsInterval(Interval<T> i) {
		
		for(Interval<T> item:intervals) {
			if (item.contains(i))
				return true;				
		}
		
		return false;
	}
	
//	public boolean isDisjointPartition() {
//		int n = intervals.size();
//		for(int i = 0; i < n - 1; i++) 
//			for (int j = i + 1; j < n; j++) {
//				if (intervals.get(i).isIntersect(intervals.get(j)))
//					return false;
//			}
//		return true;
//	}

	

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Partition))
			return false;
		Partition<T> other = (Partition<T>) obj;
		if (intervals == null) {
			if (other.intervals != null)
				return false;
		} else if (!intervals.equals(other.intervals))
			return false;
		return true;
	}
	
	public List<Interval<T>> getIntervals() {
		return this.intervals;
	}

	/**
	 * Return the superset I' of target interval i: i \subset I'
	 * @param i
	 * @return
	 */
	public Interval<T> getSuperInterval(Interval<T> i) {
		for(Interval<T> item:intervals) {
			if (item.contains(i))
				return item;				
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((intervals == null) ? 0 : intervals.hashCode());
		return result;
	}

	/**
	 * Return number of intervals in the partition
	 * @return
	 */
	public int size() {
		return this.intervals.size();
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer(); 
		buffer.append("{");
		for(Interval<T> i:this.intervals) {
			buffer.append(i.toString() + ",");
		}
		buffer.deleteCharAt(buffer.length()-1);
		buffer.append("}");
		return buffer.toString();
	}
}
