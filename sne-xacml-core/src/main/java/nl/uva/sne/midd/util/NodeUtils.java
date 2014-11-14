/*
 * SNE-XACML: A high performance XACML evaluation engine.
 *
 * Copyright (C) 2014 Canh Ngo <canhnt@gmail.com>
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
import nl.uva.sne.midd.nodes.*;
import nl.uva.sne.midd.obligations.InternalNodeState;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to create internal nodes.
 */
public class NodeUtils {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(NodeUtils.class);
    /**
     * Mapping from data types to node types
     */
    private static final Map<Class<? extends  Comparable>, Class<? extends InternalNode>> MAP_NODE_TYPES = new HashMap<>();

    static{
        MAP_NODE_TYPES.put(Integer.class, IntegerNode.class);
        MAP_NODE_TYPES.put(Double.class, DoubleNode.class);
        MAP_NODE_TYPES.put(String.class, StringNode.class);
        MAP_NODE_TYPES.put(AnyURI.class, AnyURINode.class);
    }

    public static InternalNode<?> createInternalNode(int id, InternalNodeState state, Class<?> clsDataType) throws MIDDException {
        Class<? extends InternalNode> nodeClsType = getNodeClassType(clsDataType);
        try {
            Constructor<? extends InternalNode> constructor  = nodeClsType.getConstructor(int.class, InternalNodeState.class);
            return constructor.newInstance(id, state);
        } catch (NoSuchMethodException e) {
            log.error("Cannot find the constructor for the class {}", clsDataType.getName(), e);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            log.error("Failed to construct object of type '{}'", clsDataType.getName(), e);
        }

        throw new MIDDException("Unsupported data type to create internal node of type " + clsDataType.getName());
    }

    private static Class<? extends InternalNode> getNodeClassType(Class<?> clsDataType) {
        if (MAP_NODE_TYPES.containsKey(clsDataType)){
            return MAP_NODE_TYPES.get(clsDataType);
        } else {
            throw new UnsupportedOperationException("Unsupported data type to create node " + clsDataType.getSimpleName());
        }
    }

    public static InternalNode<?> createInternalNode(InternalNode<?> n1, Class<?> type) throws MIDDException {
        return createInternalNode(n1.getID(), n1.getState(), type);
    }
}
