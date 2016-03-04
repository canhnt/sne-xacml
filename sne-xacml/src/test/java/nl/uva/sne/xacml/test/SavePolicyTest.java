/*
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
package nl.uva.sne.xacml.test;


import nl.uva.sne.xacml.profiles._2_0_.policy.ObjectFactory;
import nl.uva.sne.xacml.profiles._2_0_.policy.PolicySetCombinerParametersType;
import nl.uva.sne.xacml.profiles._2_0_.policy.PolicySetType;
import nl.uva.sne.xacml.profiles._2_0_.policy.PolicyType;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;


public class SavePolicyTest {

    private static final String POLICY_FILE = "policies/policyset-demo.xml";

    @Test
    public void savePolicy() throws ParserConfigurationException, SAXException, IOException {
        ObjectFactory of = new ObjectFactory();

        PolicyType p1 = of.createPolicyType();
        p1.setPolicyId("policy01");
        p1.setRuleCombiningAlgId("urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:deny-overrides");

        PolicySetType ps1 = of.createPolicySetType();
        ps1.getPolicySetOrPolicyOrPolicySetIdReference().add(p1);
        ps1.setPolicyCombiningAlgId("urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:permit-overrides");

        write(of.createPolicySetTypePolicySet(ps1));
    }

    private void write(JAXBElement<PolicySetType> jaxbElement) {
        try {
            JAXBContext jc = JAXBContext.newInstance(PolicySetType.class, PolicyType.class);
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            m.marshal(jaxbElement, System.out);

        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static Document readXML(String xmlFile) throws ParserConfigurationException, SAXException, IOException {
        javax.xml.parsers.DocumentBuilderFactory dbf =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();

        org.w3c.dom.Document doc = db.parse(xmlFile);

        return doc;
    }

    public static PolicySetType unmarshalPolicySetType(String policysetFile) throws ParserConfigurationException, SAXException, IOException {
        Document doc = readXML(policysetFile);
        Element xmlDom = doc.getDocumentElement();

        return unmarshalPolicySetType(xmlDom);
    }

    /**
     * @param domRequest
     * @return
     */
    public static PolicySetType unmarshalPolicySetType(Element dom) {
        return unmarshall(PolicySetType.class, dom);
    }

    private static <T> T unmarshall(Class<T> cls,
                                    Element dom) {

        try {
            JAXBContext jc = JAXBContext.newInstance(PolicySetType.class, PolicyType.class, PolicySetCombinerParametersType.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();

            JAXBElement<T> jaxbObject = unmarshaller.unmarshal(dom, cls);

            return jaxbObject.getValue();
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
