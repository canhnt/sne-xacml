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
package nl.uva.sne.midd.util;

import nl.uva.sne.midd.Decision;
import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.UnmatchedException;
import nl.uva.sne.midd.Variable;
import nl.uva.sne.midd.edges.AbstractEdge;
import nl.uva.sne.midd.nodes.AbstractNode;
import nl.uva.sne.midd.nodes.InternalNodeImpl;
import nl.uva.sne.xacml.ExternalNode3;

import java.util.Map;

/**
 * @author Canh Ngo
 * @date: Sep 11, 2012
 */
public class EvaluationUtils {
    /**
     * Evaluate a map of attributes (from id to variable) against a multitype interval decision diagram (MIDD).
     *
     * @param midd  the DAG which has its root is the first attribute (x0)
     * @param variables vector of attributes, starting from x0. If there's missing any attribute, its value is null.
     * @return The external node that holds effect value and obligations (optional).
     */
    @SuppressWarnings({"unchecked"})
    public static Decision eval(InternalNodeImpl<?> midd, Map<Integer, Variable<?>> variables) {
        AbstractNode currentNode = midd;

        while (currentNode instanceof InternalNodeImpl) {
            InternalNodeImpl currentInternalNode = (InternalNodeImpl) currentNode;

            Variable<?> currentVar = null;
            // attribute not found:
            if (!variables.containsKey(currentInternalNode.getID())) {
                // create a null variable
                currentVar = createVariable(currentInternalNode.getID(), null, currentInternalNode.getType());
            } else {
                currentVar = variables.get(currentInternalNode.getID());
            }

            if (currentVar.getType() != currentInternalNode.getType()) {
                throw new RuntimeException("Error evaluation, either tree or values have error: same attribute with different variable identifiers");
            }

            try {
                AbstractEdge<?> e = currentInternalNode.match(currentVar.getValue());
                currentNode = e.getSubDiagram();
            } catch (UnmatchedException ex) {
                return currentInternalNode.buildDecision();
            } catch (MIDDException e) {
                throw new RuntimeException(e);
            }
        }
        if (currentNode == null) {
            throw new RuntimeException("Incorrect MIDD: leaf node must not be null");
        }

        return ((ExternalNode3) currentNode).buildDecision();
    }

    private static <T extends Comparable<T>> Variable<?> createVariable(int id, T value, Class<T> type) {
        return new Variable<>(id, value, type);
    }
}
