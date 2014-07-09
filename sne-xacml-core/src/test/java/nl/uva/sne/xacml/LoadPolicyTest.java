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
package nl.uva.sne.xacml;


import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import nl.uva.sne.xacml.util.XACMLUtil;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;


public class LoadPolicyTest {

    private static final String POLICY_FILE = "policies/xacml3-mli-interface.xml";

    private static final String POLICYSET_TYPE = "src/test/resources/xacml3-policyset-sli.xml";

    @Test
    public void loadPolicy() throws ParserConfigurationException, SAXException, IOException {
        PolicyType policy1 = XACMLUtil.unmarshalPolicyType(POLICY_FILE);

        print((new oasis.names.tc.xacml._3_0.core.schema.wd_17.ObjectFactory().createPolicy(policy1)), PolicyType.class);
    }

    @Test
    public void loadPolicyset() throws ParserConfigurationException, SAXException, IOException {
        PolicySetType ps = XACMLUtil.unmarshalPolicySetType(POLICYSET_TYPE);

        print((new oasis.names.tc.xacml._3_0.core.schema.wd_17.ObjectFactory().createPolicySet(ps)), PolicySetType.class);
    }

    private static <T> void print(JAXBElement<T> jaxbElement, Class<T> cls) {
        JAXBContext jc;
        try {
            jc = JAXBContext.newInstance(cls);
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            m.marshal(jaxbElement, System.out);
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
