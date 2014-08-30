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
package nl.uva.sne.midd.nodes;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * A true-value external node.
 *
 * @author canhnt
 */
public class ExternalNode extends AbstractNode {

    public static final int EXTERNAL_NODE_ID = -1;

    private static ExternalNode instance;

    public ExternalNode() {
        super(EXTERNAL_NODE_ID);
    }

    public static ExternalNode newInstance(){
        if (instance == null) {
            instance = new ExternalNode();
        }
        return instance;
    }

    @Override
    public Class<?> getType() {
        return Boolean.class;
    }

    @Override
    public void print(OutputStream os) {
        PrintStream ps = new PrintStream(os);
        ps.println("TrueNode\n");
    }

}
