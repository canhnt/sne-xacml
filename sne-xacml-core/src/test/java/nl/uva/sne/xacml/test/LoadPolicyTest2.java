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
package nl.uva.sne.xacml.test;


import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import nl.uva.sne.xacml.profiles._2_0_.context.RequestType;
import nl.uva.sne.xacml.profiles._2_0_.policy.PolicySetType;


public class LoadPolicyTest2 {

    //	private static final String POLICY_FILE = "policies/sample-policy.xml";
//	private static final String POLICY_FILE = "policies/policyset-demo-1.xml";
    private static final String POLICY_FILE = "src/test/resources/PPS-VIO-Role.xml";
    private static final String REQUEST_TYPE = "requests/demo-request.xml";


    private RequestType unmarshalRequestType(String requestType) throws ParserConfigurationException, SAXException, IOException {
        Document doc = readXML(requestType);
        Element xmlDom = doc.getDocumentElement();

        return unmarshalRequestType(xmlDom);
    }

    private RequestType unmarshalRequestType(Element xmlDom) {
        return unmarshall(RequestType.class, xmlDom);
    }

    @Test
    public void loadPolicy() throws ParserConfigurationException, SAXException, IOException {
        PolicySetType ps1 = unmarshalPolicySetType(POLICY_FILE);

        print((new nl.uva.sne.xacml.profiles._2_0_.policy.ObjectFactory().createPolicySetTypePolicySet(ps1)), PolicySetType.class);
    }

//	@Test
//	public void loadRequest() throws ParserConfigurationException, SAXException, IOException {
//		RequestType r = unmarshalRequestType(REQUEST_TYPE);
//
//		print((new nl.uva.sne.xacml.profiles._2_0_.context.ObjectFactory()).createRequest(r), RequestType.class);
//
//	}

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
    private static PolicySetType unmarshalPolicySetType(Element dom) {
        return unmarshall(PolicySetType.class, dom);
    }

    private static <T> T unmarshall(Class<T> cls,
                                    Element dom) {

        try {
            JAXBContext jc = JAXBContext.newInstance(cls);
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
