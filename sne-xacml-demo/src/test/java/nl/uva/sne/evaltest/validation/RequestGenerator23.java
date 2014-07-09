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

package nl.uva.sne.evaltest.validation;

import java.util.List;
import java.util.Random;

import nl.uva.sne.evaltest.sunxacml.RequestGenerator2;

import oasis.names.tc.xacml._2_0.context.schema.os.ActionType;
import oasis.names.tc.xacml._2_0.context.schema.os.AttributeType;
import oasis.names.tc.xacml._2_0.context.schema.os.AttributeValueType;
import oasis.names.tc.xacml._2_0.context.schema.os.EnvironmentType;
import oasis.names.tc.xacml._2_0.context.schema.os.RequestType;
import oasis.names.tc.xacml._2_0.context.schema.os.ResourceType;
import oasis.names.tc.xacml._2_0.context.schema.os.SubjectType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributesType;



/**
 * Generate both XACML 2.0 and XACML 3.0 requests with the same attribute values.
 * 
 * @author CanhNT
 *
 */
public class RequestGenerator23 extends RequestGenerator2{
	private RequestType request2;
	
	private oasis.names.tc.xacml._3_0.core.schema.wd_17.RequestType request3;
	
	protected oasis.names.tc.xacml._3_0.core.schema.wd_17.ObjectFactory factory3;
	
	
	public RequestGenerator23() {
		super();
		factory3 = new oasis.names.tc.xacml._3_0.core.schema.wd_17.ObjectFactory();
	}
	
	@Override
	public RequestType generate() {
		request2 = factory.createRequestType();
		request3 = factory3.createRequestType();
				
		SubjectType subj = factory.createSubjectType();
		for(String attrId: subjAttrMapperValues.keySet() ) {			
			Object value = getRandomValue(attrId, subjAttrMapperValues.get(attrId));
			
			AttributeType attr2 = createAttribute2(attrId, value);
			AttributesType attr3 = createAttributes3(attrId, value);
			subj.getAttribute().add(attr2);
			request3.getAttributes().add(attr3);
		}
		request2.getSubject().add(subj);
		
		ResourceType res = factory.createResourceType();
		for(String attrId: resAttrMapperValues.keySet() ) {
			Object value = getRandomValue(attrId, resAttrMapperValues.get(attrId));
			
			AttributeType attr2 = createAttribute2(attrId, value);
			AttributesType attr3 = createAttributes3(attrId, value);
			res.getAttribute().add(attr2);
			request3.getAttributes().add(attr3);		
		}
		request2.getResource().add(res);
		
		ActionType act = factory.createActionType();
		for(String attrId: actAttrMapperValues.keySet() ) {
			Object value = getRandomValue(attrId, actAttrMapperValues.get(attrId));
			
			AttributeType attr2 = createAttribute2(attrId, value);
			AttributesType attr3 = createAttributes3(attrId, value);
			act.getAttribute().add(attr2);
			request3.getAttributes().add(attr3);
		}
		request2.setAction(act);
		
		EnvironmentType env = factory.createEnvironmentType();
		for(String attrId: envAttrMapperValues.keySet() ) {
			Object value = getRandomValue(attrId, envAttrMapperValues.get(attrId));
			
			AttributeType attr2 = createAttribute2(attrId, value);
			AttributesType attr3 = createAttributes3(attrId, value);
			act.getAttribute().add(attr2);
			request3.getAttributes().add(attr3);
		}
		request2.setEnvironment(env);
		
		return request2;			
	}
	
	/**
	 * Return a random value from the list
	 * 
	 * @param attrId
	 * @param lstValues
	 * @return
	 */
	private Object getRandomValue(String attrId, List<String> lstValues) {
		Random rnd = this.attrRnds.get(attrId);		
		Object rndValue = lstValues.get(rnd.nextInt(lstValues.size()));		
		return rndValue;
	}

	public RequestType getRequest2() {
		return request2;
	}
	
	public oasis.names.tc.xacml._3_0.core.schema.wd_17.RequestType getRequest3() {
		return request3;
	}

	protected oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributesType createAttributes3(String attrId, Object value) {
		oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributesType attrs = factory3.createAttributesType();
		
		oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType attrValue = factory3.createAttributeValueType();
		attrValue.setDataType(this.attrMapperDataTypes.get(attrId));
		

		attrValue.getContent().add(value);
		
		oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeType attr = factory3.createAttributeType();
		attr.setAttributeId(attrId);		
		attr.getAttributeValue().add(attrValue);
				
		attrs.getAttribute().add(attr);
		return attrs;
	}

	protected AttributeType createAttribute2(String attrId, Object value) {
		AttributeType attr = factory.createAttributeType();
		attr.setAttributeId(attrId);
		attr.setDataType(attrMapperDataTypes.get(attrId));
		
		AttributeValueType attrVal = factory.createAttributeValueType();
		attrVal.getContent().add(value);
		
		attr.getAttributeValue().add(attrVal);

		return attr;
	}
}
