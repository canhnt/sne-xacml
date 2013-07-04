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
package nl.uva.sne.midd.edges;

import java.util.ArrayList;
import java.util.List;

import nl.uva.sne.midd.interval.Interval;
import nl.uva.sne.midd.nodes.AbstractNode;

/**
 * An out-going edge from a node
 * 
 * @author Canh Ngo (t.c.ngo@uva.nl)
 *
 * @version 
 * @date: Aug 9, 2012
 */
public abstract class AbstractEdge<T extends Comparable<T>>{
	
	/**
	 * List of intervals for the current edge.
	 */
	protected List<Interval<T>> intervals;
	
	/**
	 * The sub-diagram of the function at the edge's endpoint
	 */
	private AbstractNode subDiagram;

	private AbstractEdge() {
		this.intervals = new ArrayList<Interval<T>>();
	}
	
	public AbstractEdge(Interval<T> interval) {
		this();
		this.intervals.add(interval);
	}
	
	public AbstractEdge(List<Interval<T>> intervals) {
		this();
		this.intervals.addAll(intervals);
	}
	
	public void addInterval(Interval<T> interval) {
		this.intervals.add(interval);
	}
	
	public void addInterval(List<Interval<T>> intervals) {
		this.intervals.addAll(intervals);
	}
	
	public boolean containsInterval(Interval<T> interval) {
		for(Interval<T> item:intervals) {
			if (item.contains(interval))
				return true;				
		}		
		return false;		
	}
	
	@SuppressWarnings("rawtypes")
	public List<Interval> getIntervals() {
		List<Interval> lst = new ArrayList<Interval>();
		for(Interval<?> e:this.intervals) {
			lst.add(e);
		}
		return lst;
	}
	
	public AbstractNode getSubDiagram() {
		return this.subDiagram;
	}
	
	public abstract Class<T> getType();
	
	/**
	 * Check if the value is matched with the edge's intervals
	 * 
	 * @param value
	 * @return
	 */
	public boolean match(T value) {
		for(Interval<T> interval: this.intervals) {
			if (interval.hasValue(value))
				return true;
		}
		return false;
	}
	
	public void setChild(AbstractNode child) {
		if (child == null)
			throw new RuntimeException("Null child");
		this.subDiagram = child;
	}
	
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");
		for(Interval i : this.intervals) {
			buffer.append(i.toString());
		}
		buffer.append("}");
		return buffer.toString();
	}
}
	