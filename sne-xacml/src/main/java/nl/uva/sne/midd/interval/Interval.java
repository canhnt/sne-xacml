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
/**
 * System and Network Engineering Group
 * University of Amsterdam
 *
 */
package nl.uva.sne.midd.interval;

import java.util.ArrayList;
import java.util.List;

import nl.uva.sne.midd.MIDDException;

/**
 * @author Canh Ngo (t.c.ngo@uva.nl)
 *
 * @version 
 * @date: Sep 7, 2012
 */
public class Interval <T extends Comparable<T>> {
	
//	private static final int SEED = 0;

	private EndPoint<T> lowerBound;
	
	private boolean lowerBoundClosed = false;
				
	private EndPoint<T> upperBound;
	
	private boolean upperBoundClosed = false;
	
	/**
	 * Interval construction with a single end-point
	 * 
	 * @param bound
	 */
	public Interval(EndPoint<T> bound) {
		this.lowerBound = this.upperBound = bound;
		this.lowerBoundClosed = this.upperBoundClosed = true;
	}
	
	public Interval(EndPoint<T> lowerBound, EndPoint<T> upperBound) throws MIDDException {
		this(lowerBound, upperBound, false, false);
	}

    @SuppressWarnings("unchecked")
	public Interval(EndPoint<T> lowerBound, EndPoint<T> upperBound, boolean isLowerBoundClosed, boolean isUpperBoundClosed) throws MIDDException {
		this.lowerBound = new EndPoint(lowerBound);
		this.lowerBoundClosed = isLowerBoundClosed;
		
		this.upperBound = new EndPoint(upperBound);
		this.upperBoundClosed = isUpperBoundClosed;
	}
	
	public Interval(Interval<T> interval) throws MIDDException {
		this(interval.lowerBound, interval.upperBound, interval.lowerBoundClosed, interval.upperBoundClosed);
	}
	
	public Interval(T bound) throws MIDDException {
		this.lowerBound = this.upperBound = new EndPoint<T>(bound);
		this.lowerBoundClosed = this.upperBoundClosed = true;
	}
	
	public Interval(T lowerBound, T upperBound) throws MIDDException {
		this(lowerBound, upperBound, false, false);
	}
	
	public Interval(T lowerBound, T upperBound, boolean isLowerBoundClosed, boolean isUpperBoundClosed) throws MIDDException {
		this.lowerBound = new EndPoint<T>(lowerBound);
		this.upperBound = new EndPoint<T>(upperBound);
		
		this.lowerBoundClosed = isLowerBoundClosed;
		this.upperBoundClosed = isUpperBoundClosed;
	}

	/**
	 * Return the complement section of the interval.
	 * 
	 * @param op
	 * @return
	 */
	public List<Interval<T>> complement(Interval<T> op) throws MIDDException {
		
		if (this.lowerBound.compareTo(op.upperBound) >= 0 ||
			this.upperBound.compareTo(op.lowerBound) <= 0) {
			Interval<T> newInterval = new Interval<T>(this.lowerBound, this.upperBound);
			
			if ((this.lowerBound.compareTo(op.upperBound) == 0)){
				newInterval.setLowerBoundClosed(this.lowerBoundClosed && !op.upperBoundClosed);
//				newInterval.setUpperBoundClosed(this.upperBoundClosed);
			} else {
				newInterval.setLowerBoundClosed(this.lowerBoundClosed);
			}
			
			if (this.upperBound.compareTo(op.lowerBound) == 0) {
//				newInterval.setLowerBoundClosed(this.lowerBoundClosed);
				newInterval.setUpperBoundClosed(this.upperBoundClosed && !op.upperBoundClosed);
			} else {
				newInterval.setUpperBoundClosed(this.upperBoundClosed);
			}
			
			if (!newInterval.validate())
				return null;
			List<Interval<T>> result = new ArrayList<Interval<T>>(); 
			result.add(newInterval);						
			return result;
		} else {	// (this.lowerBound.compareTo(op.upperBound) < 0 && this.upperBound.compareTo(op.lowerBound) > 0)
			Interval<T> interval1 = new Interval<T>(this.lowerBound, op.lowerBound);
			Interval<T> interval2 = new Interval<T>(op.upperBound, this.upperBound);
			
			interval1.setLowerBoundClosed(this.lowerBoundClosed);
			interval1.setUpperBoundClosed(!op.lowerBoundClosed);
			
			interval2.setLowerBoundClosed(!op.upperBoundClosed);
			interval2.setUpperBoundClosed(this.upperBoundClosed);
			
			List<Interval<T>> result = new ArrayList<Interval<T>>(); 
			if (interval1.validate())
				result.add(interval1);
			if (interval2.validate())
				result.add(interval2);
			if (result.size() > 0)
				return result;
			else
				return null;
		}
	}
	
	/**
	 * Return true if interval in  the argument is the subset of the current interval.
	 * 
	 * @param i
	 * @return
	 */
	public boolean contains(Interval<T> i) {
		
		int compareLow, compareUp;
		compareLow = this.lowerBound.compareTo(i.lowerBound);
		compareUp = this.upperBound.compareTo(i.upperBound);	
						
		if (compareLow < 0) {
			// check the upper bound
			if (compareUp > 0)
				return true;
			else if ((compareUp == 0) && 
					 (this.upperBoundClosed || !i.upperBoundClosed)){
				return true;
			}
		} else if (compareLow == 0) {
			if (this.lowerBoundClosed || !i.lowerBoundClosed) { // lowerbound satisfied
				 {
					// check upperbound
					 if (compareUp > 0)
						 return true;
					 else if ((compareUp == 0) && 
							  (this.upperBoundClosed || !i.upperBoundClosed)){
						return true;
					 }
				}								
			}
		}
		return false;		
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		@SuppressWarnings("unchecked")
		Interval<T> other = (Interval<T>) obj;
		
		if (lowerBound == null) {
			if (other.lowerBound != null)
				return false;
		} else if (!lowerBound.equals(other.lowerBound))
			return false;
		if (lowerBoundClosed != other.lowerBoundClosed)
			return false;
		if (upperBound == null) {
			if (other.upperBound != null)
				return false;
		} else if (!upperBound.equals(other.upperBound))
			return false;
		if (upperBoundClosed != other.upperBoundClosed)
			return false;
		return true;
	}
		
	public EndPoint<T> getLowerBound() {
		return this.lowerBound;
	}
		
	public EndPoint<T> getUpperBound() {
		return this.upperBound;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((lowerBound == null) ? 0 : lowerBound.hashCode());
		result = prime * result + (lowerBoundClosed ? 1231 : 1237);
		result = prime * result
				+ ((upperBound == null) ? 0 : upperBound.hashCode());
		result = prime * result + (upperBoundClosed ? 1231 : 1237);
		return result;
	}
	
	/**
	 * Check if the value is presenting in the interval.
	 * 
	 * @param value
	 * @return
	 */
	public boolean hasValue(T value) throws MIDDException {
		
		//special processing when missing attribute
		if (value == null) {
			return this.isLowerInfinite() || this.isUpperInfinite();				
		}
			
		
		EndPoint<T> epValue = new EndPoint<T>(value);
		
		int compareLow = this.lowerBound.compareTo(epValue);
		int compareUp = this.upperBound.compareTo(epValue);
		
		if ((compareLow < 0 || (compareLow == 0 && this.lowerBoundClosed)) &&
			(compareUp > 0 || (compareUp == 0 && this.upperBoundClosed))) 
				return true;
				
		return false;
	}
	
	/**
	 * Combine two interval, check if the bounds should be included
	 * Bug: not count use-cases when bounds are infinities.
	 * 
	 * @param <T>
	 * @param target
	 * @return
	 */
	public Interval<T> includeBound(Interval<T> target) {
		
		if (target.lowerBound.equals(target.upperBound)) { // target is a single-value interval
			if (!this.lowerBound.equals(this.upperBound)) {
				if (this.upperBound.equals(target.upperBound)) {
					this.upperBoundClosed = true;
					return this;
				}
				else if (this.lowerBound.equals(target.upperBound)){
					this.lowerBoundClosed = true;
					return this;
				} else {
					
					throw new RuntimeException("Error! Cannot combine two separated interval");
//					System.err.println("Error! Cannot combine two separated interval");
//					return this;
				}
			} else {
				throw new RuntimeException("Error! Only support combine single value interval");
			}			
		} else if (this.lowerBound.equals(this.upperBound)) { // (*this) is the single-value interval 
			if (target.lowerBound.equals(target.upperBound)) {
				throw new RuntimeException("Error! Only support combine single value interval");
			}
			
			if (target.lowerBound.equals(this.lowerBound)) {
				this.lowerBound = target.lowerBound;
				this.upperBound = target.upperBound;				
				this.lowerBoundClosed = true;
				this.upperBoundClosed = target.upperBoundClosed;
				
				return this;
			} else if (target.upperBound.equals(this.lowerBound)) {
				this.lowerBound = target.lowerBound;
				this.upperBound = target.upperBound;				
				this.lowerBoundClosed = target.lowerBoundClosed;
				this.upperBoundClosed = true;
				
				return this;
			}else {
				throw new RuntimeException("Error! Cannot combine two separated interval");
			} 
		}
		else {
			throw new RuntimeException("Error! Only support combine single value interval");
//			System.err.println("Error! Only support combine single value interval");
//			return this;
		}				
	}

	public boolean isIntersec(Interval<T> i) {		
		int c = this.upperBound.compareTo(i.lowerBound); 
		if (c < 0)
			return false;
		else if (c == 0) {
			return this.upperBoundClosed && i.lowerBoundClosed;
		}
		c = this.lowerBound.compareTo(i.upperBound);
		if (c > 0)
			return false;
		else if (c == 0) {
			return this.lowerBoundClosed && i.upperBoundClosed;
		}
		return true;
	}
	
	public boolean isLowerBoundClosed() {
		return this.lowerBoundClosed;
	}

	public boolean isLowerInfinite() {
		return lowerBound.getNegativeInfinity();
	}
	
	public boolean isUpperBoundClosed() {
		return this.upperBoundClosed;
	}
	
	public boolean isUpperInfinite() {
		return upperBound.getPositiveInfinity();
	}
	
	public void setLowerBound(EndPoint<T> lowerBound){
		this.lowerBound = lowerBound;
	}
	
//	/**
//	 * Compare two interval if they are the same.
//	 * 
//	 * @param interval
//	 * @return
//	 */
//	public boolean equals(Interval<T> interval) {
//		
//		boolean lowBoundEqual = false, upBoundEqual = false;
//		
//		lowBoundEqual = this.lowerBound.equals(interval.lowerBound) && this.lowerBoundClosed == interval.lowerBoundClosed;		
//		upBoundEqual = this.upperBound.equals(interval.upperBound) && this.upperBoundClosed == interval.upperBoundClosed;
//				
//		return lowBoundEqual && upBoundEqual;
//	}

	public void setLowerBound(T value) throws MIDDException {
		this.lowerBound = new EndPoint<T>(value);
	}
	
	public void setLowerBoundClosed(boolean b) {
		this.lowerBoundClosed = b;
	}

//	public Class<?> getType() {
//		throw new UnsupportedOperationException("Not support for generic interval type");
//	}

	public void setLowerInfinite(boolean b) {
		this.lowerBound.setNegativeInfinity(b);
		this.lowerBoundClosed = false;
		
	}

	public void setUpperBound(EndPoint<T> upperBound) {
		this.upperBound = upperBound;
	}
	
	public void setUpperBound(T upperBound) throws MIDDException {
		this.upperBound = new EndPoint<T>(upperBound);	
	}

	public void setUpperBoundClosed(boolean b) {
		this.upperBoundClosed = b;
	}

	public void setUpperInfnite(boolean b) {
		this.upperBound.setPositiveInfinity(b);
		this.upperBoundClosed = false;
		
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		if (lowerBoundClosed && upperBoundClosed && lowerBound.equals(this.upperBound))
			builder.append("["+ lowerBound.toString() + "]");
		else {
			if (this.lowerBound.getNegativeInfinity())
				builder.append("(-inf");
			else {
				builder.append(this.lowerBoundClosed ? "[" : "(");		
				builder.append(this.lowerBound.toString());
			}
			builder.append(",");
			
			if (this.upperBound.getPositiveInfinity())
				builder.append("inf)");
			else {
				builder.append(this.upperBound + (this.upperBoundClosed ? "]" : ")"));
			}						
		}
		
		
		return builder.toString();		
	}		
	
	/**
	 * Check if the interval is valid or an empty interval.
	 * 
	 * @return
	 */
	public boolean validate() {
		int compareBound = this.lowerBound.compareTo(this.upperBound);
		
		return (compareBound < 0) ||
			   (compareBound == 0 && this.lowerBoundClosed && this.upperBoundClosed);
//		if (compareBound < 0)
//			return true;
//		else if (compareBound == 0 && this.lowerBoundClosed && this.upperBoundClosed)
//			return true;
//		return false;
	}

	/**
	 * The interval contains only 1 value: upper==lower 
	 * @param value
	 */
	public void setSingleValue(EndPoint<T> value) {
		this.upperBound = this.lowerBound = value;
		this.lowerBoundClosed = this.upperBoundClosed = true;
		
	}

	public Class<T> getType() throws MIDDException {
		
		if (this.lowerBound.getType() != null)
			return this.lowerBound.getType();
		
		if (this.upperBound.getType() != null)
			return this.upperBound.getType();
		
		throw new MIDDException("Unsupported (-inf, +inf) interval");
	}	
}
