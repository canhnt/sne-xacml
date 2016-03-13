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
package nl.uva.sne.xacml.policy.parsers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.builders.ConjunctiveBuilder;
import nl.uva.sne.midd.nodes.ExternalNode;
import nl.uva.sne.midd.nodes.Node;
import nl.uva.sne.xacml.AttributeMapper;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;

/**
 * TargetExpression: contains list of AnyOf expression combined by conjunctive operator
 *
 * @author Canh Ngo
 * @date: Sep 27, 2012
 */
public class TargetExpression {

    private List<AnyOfType> lstAnyOf;

    private AttributeMapper attrMapper = null;
    ;

    public TargetExpression(AttributeMapper attrMapper) {
        if (attrMapper == null) {
            throw new IllegalArgumentException("AttributeMapper argument must not be null");
        }

        this.lstAnyOf = new ArrayList<AnyOfType>();
        this.attrMapper = attrMapper;
    }

    public TargetExpression(List<AnyOfType> lstAnyOf, AttributeMapper attrMapper) {
//		if (lstAnyOf == null || lstAnyOf.size() == 0)
//			throw new IllegalArgumentException("lstAnyOf argument must not be null or empty");
        if (attrMapper == null) {
            throw new IllegalArgumentException("AttributeMapper argument must not be null");
        }

        if (lstAnyOf != null && lstAnyOf.size() > 0) {
            this.lstAnyOf = new ArrayList<AnyOfType>(lstAnyOf);
        } else {
            this.lstAnyOf = null;
        }

        this.attrMapper = attrMapper;
    }

    public void add(AnyOfType anyOf) {
        lstAnyOf.add(anyOf);
    }

    public void addAll(List<AnyOfType> lstAnyOf) {
        this.lstAnyOf.addAll(lstAnyOf);
    }

    public AttributeMapper getAttributeMapper() {
        return this.attrMapper;
    }

    public Node parse() throws XACMLParsingException, MIDDParsingException, MIDDException {

        if (lstAnyOf == null || lstAnyOf.size() == 0) {
            return ExternalNode.newInstance();    // return a true-value external node if there's no AnyOf expression.
        }

        Node root = null;

        Iterator<AnyOfType> itAnyOf = lstAnyOf.iterator();

        while (itAnyOf.hasNext()) {
            AnyOfType currentAnyOfExp = itAnyOf.next();
            AnyOfExpression aoe = new AnyOfExpression(currentAnyOfExp, attrMapper);

            Node currentMIDD = aoe.parse();

            if (root != null) {
                // Conjunctive join current AnyOf expressions
                root = ConjunctiveBuilder.join(root, currentMIDD);
            } else {
                root = currentMIDD;
            }
        }
        return root;
    }
}
