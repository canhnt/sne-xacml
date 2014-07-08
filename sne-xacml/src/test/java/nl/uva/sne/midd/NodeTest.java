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
package nl.uva.sne.midd;

import org.junit.Test;

public class NodeTest {


	@Test
	public void buildTree() {
//		IntegerNode root = new IntegerNode(0);
//						
//		StringNode n10 = new StringNode(1);
//		StringNode n11 = new StringNode(1);
//		StringNode n12 = new StringNode(1);
//		
//		root.addChild(18, 20, n10);
//		root.addChild(20, 25, n11);
//		root.addChild(28, 30, n12);
//		
//		n10.addChild(new String[]{"AMS", "BCN"}, TerminalNode.TerminalNodeTrue);
//		n11.addChild(new String[]{"AMS","BCN","PAR"}, TerminalNode.TerminalNodeTrue);
//		n12.addChild(new String[]{"BCN","PAR"}, TerminalNode.TerminalNodeTrue);
	}
	
	/**
	 * (x1 in {Bob, Carol})^(x2 = report1)^(x3 in {read, write}) v 
	 * (x1=Dave) ^((x2=report2)v((x2=report1)^(x3=read)))
	 */
	@Test
	public void testSimpleIDD(){
//		StringNode root = new StringNode(0);
//		
//		StringNode n10 = new StringNode(1);
//		StringNode n11 = new StringNode(1);
//		
//		root.addChild(new String[]{"Bob", "Carol"}, n10);
//		root.addChild(new String[]{"Dave"}, n11);
//		
//		StringNode n20 = new StringNode(2);
//		StringNode n21 = new StringNode(2);
//		
//		n10.addChild("Report1", n20);
//		
//		n11.addChild("Report2", TerminalNode.TerminalNodeTrue);
//		n11.addChild("Report1", n21);
//		
//		n20.addChild(new String[]{"read", "write"}, TerminalNode.TerminalNodeTrue);
//		n21.addChild("read", TerminalNode.TerminalNodeTrue);		
	}
}
