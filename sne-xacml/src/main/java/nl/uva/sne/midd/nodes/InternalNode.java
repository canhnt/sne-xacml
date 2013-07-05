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

package nl.uva.sne.midd.nodes;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import nl.uva.sne.midd.Decision;
import nl.uva.sne.midd.DecisionType;
import nl.uva.sne.midd.IDDFactory;
import nl.uva.sne.midd.edges.AbstractEdge;
import nl.uva.sne.midd.interval.Interval;
import nl.uva.sne.midd.obligations.InternalNodeState;

/**
 * @author Canh Ngo (t.c.ngo@uva.nl)
 *
 * @version 
 * @date: Aug 9, 2012
 */
/**
 * @author Canh Ngo (t.c.ngo@uva.nl)
 *
 * @version 
 * @date: Sep 14, 2012
 */
public abstract class InternalNode <T extends Comparable<T>> extends AbstractNode{	
	
	private List<AbstractEdge<T>> edges = new ArrayList<AbstractEdge<T>>();
	
	private InternalNodeState state;
		
	public InternalNode(int id, DecisionType state) {
		super(id);
		this.state = new InternalNodeState(state);
	}
	
	public InternalNode(int id, InternalNodeState state) {
		super(id);
		this.state = new InternalNodeState(state);
	}
		
	
	/**
	 * Edge<?> and InternalNode<T> must use the same type
	 * 
	 * @param edge
	 * @param child
	 */
	@SuppressWarnings("unchecked")
	public void addChild(AbstractEdge<?> edge, AbstractNode child) {
		if (child == null || edge.getIntervals().size() == 0)
			throw new RuntimeException("Cannot add null child or empty edge");
		
		edge.setChild(child);
		edges.add((AbstractEdge<T>)edge);
	}

	/**
	 * Create decision value from current internal state
	 * @return
	 */
	public Decision buildDecision() {
		return state.buildDecision();
	}

	@Override
	public AbstractNode clone() {
		InternalNode<?> n = IDDFactory.createInternalNode(this.getID(), 
				this.getState(),
				this.getType());
		
		for (AbstractEdge<?> e: this.edges) {
			AbstractNode child = e.getSubDiagram().clone();
			AbstractEdge<?> cloneEdge = IDDFactory.createEdge(e);
			n.addChild(cloneEdge, child);
		}
		return n;
	}

	/**
	 * Return the child of the current node with equivalent interval on its incoming edge.
	 * 
	 * @param interval The interval which is the subset of the interval in the incoming edge
	 * @return null if no matching edge found.
	 */
	public AbstractNode getChild(Interval<T> interval) {
		for (AbstractEdge<T> e:this.edges) {
			if (e.containsInterval(interval))
				return e.getSubDiagram();
		}
		return null;
	}

	public List<AbstractEdge<T>> getEdges() {
		return this.edges;
	}

	public InternalNodeState getState() {		
		return this.state;
	}
	
	public void setState(InternalNodeState state) {
		this.state = new InternalNodeState(state);
	}
	
	/**
	 * Collect all intervals of all outgoing edges
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<Interval> getIntervals() {

		List<Interval> lstIntervals = new ArrayList<Interval>();
		for(AbstractEdge<T> e : this.edges) {
			lstIntervals.addAll(e.getIntervals());
		}		
		return lstIntervals;
	}
	
	public DecisionType getStateIN() {
		return this.state.getStateIN();
	}

	@Override
	public abstract Class<?> getType();
	
	/**
	 * Return an edge to match with input value
	 * 
	 * @param value
	 * @return
	 * @throws UnmatchedException
	 */
	public AbstractEdge<T> match(T value) throws UnmatchedException {
		for(AbstractEdge<T> e:this.edges) {
			if (e.match(value))
				return e;
		}
		throw new UnmatchedException("No matching edge found for value " + value);
	}

	public void addAllEdges(List<AbstractEdge<T>> newEdges) {
		this.edges.addAll(newEdges);		
	}
	
	@Override
	public void print(OutputStream os) {
		String strNode = "ID:" + this.getID() + ";" + this.state.getStateIN();		
		PrintStream ps = new PrintStream(os);
		
		ps.println(strNode);
		
		for(AbstractEdge<?> e : this.edges) {
			AbstractNode child = e.getSubDiagram();
			ps.println(e.getIntervals());
			if (child != null)
				child.print(os);
		}
	}
//
//	/**
//	 * Retrieve the null-edge from the list of edges of the internal node.
//	 * 
//	 * @return Null if no null-edge found
//	 */
//	public NullEdge getNullEdge() {
//		for(AbstractEdge e : this.edges) {
//			if (e instanceof NullEdge)
//				return (NullEdge) e;
//		}
//		return null;
//	}
}
