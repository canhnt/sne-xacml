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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import nl.uva.sne.midd.builders.ConjunctiveBuilder;
import nl.uva.sne.midd.builders.DisjunctiveBuilder;

@Deprecated
public class ServiceRegistry {


    public static final String NODE_FACTORY = "NODE_FACTORY";

    private ServiceRegistry(){ }

    private static final ServiceRegistry theInstance = new ServiceRegistry();

    public static final ServiceRegistry getInstance() {
        return theInstance;
    }

    public static void init() {
        final XACMLNodeFactory nodeFactory = new XACMLNodeFactory();

        theInstance.registerService(NODE_FACTORY, nodeFactory);
    }

    private final Map<String, Object> services = new HashMap<>();

    public List<String> getServices() {
        return ImmutableList.copyOf(services.keySet());
    }

    public void registerService(final String name, final Object serivce) {
        services.put(name, serivce);
    }

    public Object getService(final String name) {
        return services.get(name);
    }

    boolean contains(final String name) {
        return services.containsKey(name);
    }
}
