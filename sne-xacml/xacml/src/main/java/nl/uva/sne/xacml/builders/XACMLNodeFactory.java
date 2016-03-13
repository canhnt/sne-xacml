/*
 * Copyright (C) 2016 Canh Ngo <canhnt@gmail.com>
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

package nl.uva.sne.xacml.builders;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.datatype.AnyURI;
import nl.uva.sne.midd.datatype.XMLDateTime;
import nl.uva.sne.xacml.nodes.AnyURINode;
import nl.uva.sne.xacml.nodes.BooleanNode;
import nl.uva.sne.xacml.nodes.DateTimeNode;
import nl.uva.sne.xacml.nodes.DoubleNode;
import nl.uva.sne.xacml.nodes.IntegerNode;
import nl.uva.sne.xacml.nodes.StringNode;
import nl.uva.sne.xacml.nodes.internal.InternalXACMLNode;
import nl.uva.sne.xacml.nodes.internal.State;

public class XACMLNodeFactory implements XNodeFactory {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(XACMLNodeFactory.class);

    /**
     * Mapping from data types to node types
     */
    private static final Map<Class<? extends  Comparable>, Class<? extends InternalXACMLNode>> MAP_NODE_TYPES = new HashMap<>();

    static{
        MAP_NODE_TYPES.put(Integer.class, IntegerNode.class);
        MAP_NODE_TYPES.put(Double.class, DoubleNode.class);
        MAP_NODE_TYPES.put(String.class, StringNode.class);
        MAP_NODE_TYPES.put(AnyURI.class, AnyURINode.class);
        MAP_NODE_TYPES.put(Boolean.class, BooleanNode.class);
        MAP_NODE_TYPES.put(XMLDateTime.class, DateTimeNode.class);

    }

    @Override
    public InternalXACMLNode create(final InternalXACMLNode source) throws MIDDException {
        return create(source.getID(), source.getState(), source.getType());
    }

    @Override
    public InternalXACMLNode create(final int id, final State state, final Class<?> clsDataType) throws MIDDException {
        final Class<? extends InternalXACMLNode> nodeClsType = getNodeClassType(clsDataType);
        try {
            Constructor<? extends InternalXACMLNode> constructor  = nodeClsType.getConstructor(int.class, State.class);
            return constructor.newInstance(id, state);
        } catch (NoSuchMethodException e) {
            log.error("Cannot find the constructor for the class {}", clsDataType.getName(), e);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            log.error("Failed to construct object of type '{}'", clsDataType.getName(), e);
        }

        throw new MIDDException("Unsupported data type to create internal node of type " + clsDataType.getName());
    }

    private static Class<? extends InternalXACMLNode> getNodeClassType(Class<?> clsDataType) {
        if (MAP_NODE_TYPES.containsKey(clsDataType)){
            return MAP_NODE_TYPES.get(clsDataType);
        } else {
            throw new UnsupportedOperationException("Unsupported data type to create node " + clsDataType.getSimpleName());
        }
    }
}
