/*
 * *
 *  * Copyright (C) 2016 Canh Ngo <canhnt@gmail.com>
 *  * All rights reserved.
 *  *
 *  * This library is free software; you can redistribute it and/or
 *  * modify it under the terms of the GNU Lesser General Public
 *  * License as published by the Free Software Foundation; either
 *  * version 3.0 of the License, or any later version.
 *  *
 *  * This library is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  * Lesser General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU Lesser General Public
 *  * License along with this library; if not, write to the Free Software
 *  * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 *  * MA 02110-1301 USA
 *
 */
package nl.uva.sne.xacml.parsers;

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.nodes.Node;
import nl.uva.sne.xacml.AttributeMapper;
import nl.uva.sne.xacml.policy.parsers.MIDDParsingException;
import nl.uva.sne.xacml.policy.parsers.TargetExpression;
import nl.uva.sne.xacml.policy.parsers.XACMLParsingException;
import nl.uva.sne.xacml.util.XACMLUtil;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RuleType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TargetExpressionTest {

    //	private static final String SAMPLE_POLICY_FILE = "src/test/resources/xacml3-AnyOf.xml";
    private static final String SAMPLE_POLICY_FILE = "src/test/resources/xacml3-policyset-suppliers.xml";


    private TargetType readTarget() throws ParserConfigurationException,
            SAXException, IOException, FileNotFoundException {
        PolicyType p1 = XACMLUtil.unmarshalPolicyType(new FileInputStream(SAMPLE_POLICY_FILE));
        List<Object> objs = p1.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();

        assertNotNull(objs);
        assertTrue(objs.size() > 1);
        assertTrue(objs.get(0) instanceof RuleType);

        RuleType r1 = (RuleType) objs.get(0);

        TargetType target = r1.getTarget();
        return target;
    }

    @Test
    public void testParsingTarget() throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
        TargetType target = readTarget();
        assertNotNull(target);
        assertNotNull(target.getAnyOf());
        assertTrue(target.getAnyOf().size() > 0);

        AttributeMapper attrMapper = new AttributeMapper();
        TargetExpression te = new TargetExpression(attrMapper);
        te.addAll(target.getAnyOf());

        Node root;
        try {
            root = te.parse();
            root.print(System.out);
        } catch (XACMLParsingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MIDDParsingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MIDDException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
}
