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
package nl.uva.sne.midd.util;

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.datatype.AnyURI;
import nl.uva.sne.midd.edges.*;
import nl.uva.sne.midd.interval.Interval;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to create edges.
 */
public class EdgeUtils {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(EdgeUtils.class);

    /**
     * Mapping from data type to edge types
     */
    private static final Map<Class<? extends  Comparable>, Class<? extends AbstractEdge>> MAP_EDGE_TYPES = new HashMap<>();
    static{
        MAP_EDGE_TYPES.put(Integer.class, IntegerEdge.class);
        MAP_EDGE_TYPES.put(Double.class, DoubleEdge.class);
        MAP_EDGE_TYPES.put(String.class, StringEdge.class);
        MAP_EDGE_TYPES.put(AnyURI.class, AnyURIEdge.class);
    }

    public static AbstractEdge<?> cloneEdge(AbstractEdge<?> e) throws MIDDException {
        final Class<?> clsType = e.getType();
        List<? extends Interval> intervals = e.getIntervals();

        return createEdge(intervals, clsType);
    }

    public static AbstractEdge<?> createEdge(Interval interval, Class<?> clsDataType) throws MIDDException {
        Class<? extends AbstractEdge> edgeClsType = getEdgeClassType(clsDataType);
        try {
            Constructor<? extends AbstractEdge> constructor  = edgeClsType.getConstructor(Interval.class);
            return constructor.newInstance(interval);
        } catch (NoSuchMethodException e) {
            log.error("Cannot find the constructor for the class {}", clsDataType.getName(), e);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            log.error("Failed to construct object of type '{}'", clsDataType.getName(), e);
        }
        throw new MIDDException("Unsupported data type to create edge of type " + clsDataType.getName());
    }

    public static AbstractEdge<?> createEdge(Interval<?> interval) throws MIDDException {
        return createEdge(interval, interval.getType());
    }

    private static Class<? extends AbstractEdge> getEdgeClassType(Class<?> clsDataType) {
        if (MAP_EDGE_TYPES.containsKey(clsDataType)){
            return MAP_EDGE_TYPES.get(clsDataType);
        }else {
            throw new UnsupportedOperationException("Unsupported data type to create edge " + clsDataType.getSimpleName());
        }
    }

    /**
     * Return the new edge created from the list of intervals.
     *
     * @param intervals
     * @param type
     * @param <T>
     * @return
     * @throws nl.uva.sne.midd.MIDDException
     */
    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> AbstractEdge<?> createEdge(List<? extends Interval> intervals, Class<?> type) throws MIDDException {
        Class<AbstractEdge<T>> clsEdgeType = (Class<AbstractEdge<T>>) getEdgeClassType(type);
        List<Interval<T>> lst = new ArrayList<>();
        for(Interval i : intervals) {
            lst.add(new Interval<T>(i));
        }
        try {
            Constructor<AbstractEdge<T>> constructor = clsEdgeType.getConstructor(List.class);
            return constructor.newInstance(lst);
        } catch (NoSuchMethodException e) {
            log.error("Cannot find the constructor for the class {}", type.getName(), e);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            log.error("Failed to construct object of type '{}'", type.getName(), e);
        }

        throw new MIDDException("Unsupported data type to create edge of type " + type.getName());
    }
}
