/*
 * Copyright (C) 2013-2016 Canh Ngo <canhnt@gmail.com>
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
package nl.uva.sne.midd.interval;

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.util.GenericUtils;

public class EndPoint<T extends Comparable<T>> implements Comparable<EndPoint<T>> {

    public enum Infinity {
        POSITIVE,
        NEGATIVE
    }

    public static final EndPoint NEGATIVE_INFINITY = new EndPoint(Infinity.NEGATIVE);
    public static final EndPoint POSITIVE_INFINITY = new EndPoint(Infinity.POSITIVE);

    private Infinity infinity;

    private T value;

    public EndPoint(Infinity infinity) {
        this.infinity = infinity;
    }

    public EndPoint(T value) throws MIDDException {
        this.value = GenericUtils.newInstance(value);
        this.infinity = null;
    }

    public EndPoint(EndPoint<T> p) throws MIDDException {
        this.infinity = p.infinity;

        // Perform deep copy
        this.value = GenericUtils.newInstance(p.value);
    }

    @Override
    public int compareTo(EndPoint<T> o) {
        if (positiveInfinity()) {
            return o.positiveInfinity() ? 0 : 1;
        } else if (negativeInfinity()) {
            return o.negativeInfinity() ? 0 : -1;
        } else {
            if (o.positiveInfinity()) {
                return -1;
            } else if (o.negativeInfinity()) {
                return 1;
            } else {
                return this.value.compareTo(o.value);
            }
        }
    }

    public boolean negativeInfinity() {
        return this.infinity == Infinity.NEGATIVE;
    }

    public boolean positiveInfinity() {
        return this.infinity == Infinity.POSITIVE;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof EndPoint) {
            return equals((EndPoint<T>) obj);
        }

        return false;
    }

    protected boolean equals(final EndPoint<T> other) {
        if (this.infinity == other.infinity) {
            if (value == other.value) {
                return true;
            } else if (value != null) {
                return value.equals(other.value);
            }
        }
        return false;
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (negativeInfinity() ? 1231 : 1237);
        result = prime * result + (positiveInfinity() ? 1231 : 1237);
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    public void setInfinity(Infinity infinity) {
        this.infinity = infinity;
        this.value = null;
    }

    public Infinity getInfinity() {
        return this.infinity;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String toString() {
        return value.toString();
    }

    public Class<T> getType() {

        if (this.value != null) {
            return (Class<T>) value.getClass();
        } else {
            return null;
        }
    }
}
