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
package nl.uva.sne.midd;

/**
 * @author Canh Ngo (t.c.ngo@uva.nl)
 *
 * @version 
 * @date: Sep 11, 2012
 */
public class Variable<T extends Comparable<T>> implements Comparable<T> {
    final int BEFORE = -1;
    final int EQUAL = 0;
    final int AFTER = 1;

	public int id;
	public T value;
	
	public Class<?> type;
	
	public Variable(int id, T value) {
		this.id = id;
		this.value = value;
	}
	
	public Variable(int id, T value, Class<?> type) {
		this(id, value);
		this.type = type;
	}
	
	public int getID() {
		return id;
	}

	public Class<?> getType() {
		if (value != null)
			return value.getClass();
		else
			return type;
	}

	public Comparable<?> getValue() {
        return value;
	}

	@Override
	public int compareTo(T arg0) {
		Variable<?> var = (Variable<?>)arg0;
		if (id < var.id) {
            return BEFORE;
        }
		else if (id == var.id) {
            return EQUAL;
        }
		else if (id > var.id) {
            return AFTER;
        }
		else
            throw new RuntimeException("Unknown comparison");
	}	
}
