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
package nl.uva.sne.xacml.parsers;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.edges.AbstractEdge;
import nl.uva.sne.midd.nodes.AbstractNode;
import nl.uva.sne.midd.nodes.InternalNode;
import nl.uva.sne.xacml.AttributeMapper;
import nl.uva.sne.xacml.policy.parsers.MIDDParsingException;
import nl.uva.sne.xacml.policy.parsers.PolicySetParser;
import nl.uva.sne.xacml.policy.parsers.XACMLParsingException;
import nl.uva.sne.xacml.util.XACMLUtil;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PolicySetParserTest {
    //	private static final String POLICYSET_FILE = "src/test/resources/xacml3-policyset-sli.xml";
    private static final String POLICYSET_FILE = "policies/sample-xacml3/continue-a-xacml3.xml";

    @Test
    public void testParse() throws ParserConfigurationException, SAXException, IOException, MIDDException {
        PolicySetType policySet = XACMLUtil.unmarshalPolicySetType(POLICYSET_FILE);
        assertNotNull(policySet);

        AttributeMapper attrMapper = new AttributeMapper();
        PolicySetParser parser = new PolicySetParser(null, policySet, attrMapper);


        try {
            AbstractNode root = parser.parse();
            root.print(System.out);
            System.out.println("Number of nodes:" + countNodes((InternalNode) root));

            assertTrue(true);
            return;
        } catch (XACMLParsingException | MIDDParsingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        fail("Exceptions occured");
    }

    public static int countNodes(InternalNode midd) throws MIDDException {
        Stack<InternalNode> stackNodes = new Stack<InternalNode>();

        stackNodes.push(midd);

        Set<AbstractNode> nodes = new HashSet<AbstractNode>();

        while (!stackNodes.empty()) {
            InternalNode n = stackNodes.pop();
            nodes.add(n);
            // search for all children of the poped internal node
            Iterator<AbstractEdge> it = n.getEdges().iterator();
            while (it.hasNext()) {
                AbstractEdge edge = it.next();
                AbstractNode child = edge.getSubDiagram();
                nodes.add(child);
                if (child instanceof InternalNode) {
                    stackNodes.push((InternalNode) child);
                }
            }
        }
        return nodes.size();
    }
}
