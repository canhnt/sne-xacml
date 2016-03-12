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

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.edges.AbstractEdge;
import nl.uva.sne.midd.nodes.Node;
import nl.uva.sne.midd.nodes.internal.InternalNode;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

public class MIDDUtils {
    public static int countNodes(InternalNode midd) throws MIDDException {
        Stack<InternalNode> stackNodes = new Stack<InternalNode>();

        stackNodes.push(midd);

        Set<Node> nodes = new HashSet<Node>();

        while (!stackNodes.empty()) {
            InternalNode n = stackNodes.pop();
            nodes.add(n);
            // search for all children of the poped internal node
            Iterator<AbstractEdge> it = n.getEdges().iterator();
            while (it.hasNext()) {
                AbstractEdge edge = it.next();
                Node child = edge.getSubDiagram();
                nodes.add(child);
                if (child instanceof InternalNode) {
                    stackNodes.push((InternalNode) child);
                }
            }
        }
        return nodes.size();
    }
}
