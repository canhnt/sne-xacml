/**
 * SNE-XACML demo project: illustrate how to use SNE-XACML engine.
 * 
 * Copyright (C) 2013 Canh T. Ngo <canhnt@gmail.com>
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

package nl.uva.sne.evaltest.sunxacml;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import oasis.names.tc.xacml._2_0.context.schema.os.ObjectFactory;
import oasis.names.tc.xacml._2_0.context.schema.os.RequestType;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.sun.xacml.ParsingException;
import com.sun.xacml.ctx.ResponseCtx;

/**
 * Convert from OpenSAML datatypes to SunXACML datatypes
 * 
 * @author CanhNT <canhnt@gmail.com>
 *
 */
public class SunXACMLUtil {
	
	/**
	 * Unmarshall from DOM Element to a generic JAXBElement
	 * 
	 * @param <T>
	 * @param cls
	 * @param domRequest
	 * @return
	 */
	private static <T> T unmarshall(Class<T> cls,
			Element domRequest) {
		
		try {
			JAXBContext jc = JAXBContext.newInstance(cls);
			Unmarshaller unmarshaller = jc.createUnmarshaller();

			JAXBElement<T> jaxbObject = unmarshaller.unmarshal(domRequest, cls);
			
			return jaxbObject.getValue();			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		return null;
	}

	/**
	 * Marshall from JAXB RequestType in SunXACML to DOM Element
	 * 
	 * @param sunxacmlRequest
	 * @return
	 */
	public static Element marshall(
			oasis.names.tc.xacml._2_0.context.schema.os.RequestType sunxacmlRequest) {
		
		JAXBElement<oasis.names.tc.xacml._2_0.context.schema.os.RequestType> jaxbRequest = (new ObjectFactory()).createRequest(sunxacmlRequest);
		
		Element element = null;
		try {
			element = marshall(oasis.names.tc.xacml._2_0.context.schema.os.RequestType.class, jaxbRequest);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return element;
	}
	
	public static Element marshal(
			oasis.names.tc.xacml._2_0.context.schema.os.ResponseType response) {
		JAXBElement<oasis.names.tc.xacml._2_0.context.schema.os.ResponseType> jaxbRequest = (new ObjectFactory()).createResponse(response);
		
		Element element = null;
		try {
			element = marshall(oasis.names.tc.xacml._2_0.context.schema.os.ResponseType.class, jaxbRequest);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return element;

	}
	/**
	 * Marshall a generic JAXBElement to DOM Element
	 * 
	 * @param <T>
	 * @param cls
	 * @param jaxbObject
	 * @return
	 * @throws JAXBException
	 * @throws ParserConfigurationException 
	 */
	private static <T> Element marshall(Class<T> cls, JAXBElement<T> jaxbObject) throws JAXBException, ParserConfigurationException {
		JAXBContext jc = JAXBContext.newInstance(cls);
		Marshaller marshaller = jc.createMarshaller();

		marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT,
				   new Boolean(true));
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);

		DocumentBuilder db = dbf.newDocumentBuilder();		
		Document doc = db.newDocument();
		
		marshaller.marshal(jaxbObject, doc);
		
		return doc.getDocumentElement();
	}

	/**
	 * Unmarshal from DOM Element to oasis.names.tc.xacml._2_0.context.schema.os.RequestType
	 * 
	 * @param domRequest
	 * @return
	 */
	public static RequestType unmarshalRequestType(Element domRequest) {
		return unmarshall(RequestType.class, domRequest);
	}

	
	public static RequestType unmarshalRequestType(String xacmlFileName) throws ParserConfigurationException, SAXException, IOException {
		Document doc = readXML(xacmlFileName);
		Element xmlDom = doc.getDocumentElement();
		
		return unmarshalRequestType(xmlDom);	
	}
	
	public static Document readXML(String xmlFile) throws ParserConfigurationException, SAXException, IOException {
		javax.xml.parsers.DocumentBuilderFactory dbf =
			javax.xml.parsers.DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);

		javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();

		org.w3c.dom.Document doc = db.parse(xmlFile);

		return doc;
	}
	
	public static String toString(Element element) {
		try {
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			StringWriter buffer = new StringWriter();
			transformer.transform(new DOMSource(element), new StreamResult(
					buffer));

			return buffer.toString();

		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		return null;
	}
}
