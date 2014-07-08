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
package nl.uva.sne.xacml.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObjectFactory;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RequestType;

/**
 * @author canhnt
 */
public class XACMLUtil {

    public static PolicySetType unmarshalPolicySetType(InputStream istream)
            throws ParserConfigurationException, SAXException, IOException {
        Document doc = readXML(istream);
        Element xmlDom = doc.getDocumentElement();

        PolicySetType policyset = unmarshalPolicySetType(xmlDom);
        if (policyset != null && policyset.getPolicySetId() != null) {
            return policyset;
        }

        return null;
    }

    public static PolicySetType unmarshalPolicySetType(String policysetFile)
            throws ParserConfigurationException, SAXException, IOException {
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

    private static <T> T unmarshall(Class<T> cls, Element dom) {

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

    public static Document readXML(InputStream istream)
            throws ParserConfigurationException, SAXException, IOException {
        javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory
                .newInstance();
        dbf.setNamespaceAware(true);

        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();

        org.w3c.dom.Document doc = db.parse(istream);

        return doc;
    }

    public static Document readXML(String xmlFile)
            throws ParserConfigurationException, SAXException, IOException {
        javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory
                .newInstance();
        dbf.setNamespaceAware(true);

        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();

        org.w3c.dom.Document doc = db.parse(new FileInputStream(xmlFile));

        return doc;
    }


    public static RequestType unmarshalRequestType(InputStream istream)
            throws ParserConfigurationException, SAXException, IOException {
        Document doc = readXML(istream);
        Element xmlDom = doc.getDocumentElement();

        return unmarshalRequestType(xmlDom);
    }

    public static RequestType unmarshalRequestType(Element xmlDom) {
        return unmarshall(RequestType.class, xmlDom);
    }

    public static PolicyType unmarshalPolicyType(InputStream istream)
            throws ParserConfigurationException, SAXException, IOException {
        Document doc = readXML(istream);
        Element xmlDom = doc.getDocumentElement();

        return unmarshalPolicyType(xmlDom);
    }

    public static PolicyType unmarshalPolicyType(String policyFile)
            throws ParserConfigurationException, SAXException, IOException {
        Document doc = readXML(policyFile);
        Element xmlDom = doc.getDocumentElement();

        PolicyType policy = unmarshalPolicyType(xmlDom);
        if (policy != null && policy.getPolicyId() != null) {
            return policy;
        }

        return null;
    }

    private static PolicyType unmarshalPolicyType(Element xmlDom) {
        return unmarshall(PolicyType.class, xmlDom);
    }

    public static RequestType unmarshalRequestType(String xmlFile)
            throws ParserConfigurationException, SAXException, IOException {
        Document doc = readXML(xmlFile);
        Element xmlDom = doc.getDocumentElement();

        return unmarshalRequestType(xmlDom);
    }

    public static <T> void print(JAXBElement<T> jaxbElement, Class<T> cls,
                                 OutputStream os) {
        JAXBContext jc;
        try {
            jc = JAXBContext.newInstance(cls);
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            m.marshal(jaxbElement, os);
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String print(RequestType request) {

        OutputStream os = new ByteArrayOutputStream();

        print((new ObjectFactory()).createRequest(request),
                oasis.names.tc.xacml._3_0.core.schema.wd_17.RequestType.class, os);

        return os.toString();
    }
}
