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
package nl.uva.sne.midd;

import com.sun.istack.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Canh Ngo
 * @date: Sep 11, 2012
 */
public class Variable<T extends Comparable<T>> implements Comparable<T> {
    private static final Logger log = LoggerFactory.getLogger(Variable.class);

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
        if (value != null) {
            return value.getClass();
        } else {
            return type;
        }
    }

    public T getValue() {
        return value;
    }

    @Override
    public int compareTo(@NotNull T arg0) {
        if (arg0 == null) {
            throw new NullPointerException("Unknown comparison");
        }

        Variable<?> var = (Variable<?>) arg0;
        if (id < var.id) {
            return BEFORE;
        } else if (id == var.id) {
            return EQUAL;
        } else if (id > var.id) {
            return AFTER;
        } else {
            log.error("Unknown comparison");
            return EQUAL;
        }
    }

    public static <T extends Comparable<T>> Variable<T> of(final int id, final Object value, final Class<?> type) {
        return new Variable<>(id, (T)value, type);
    }
}
