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

package nl.uva.sne.xacml.evaltest.sunxacml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import oasis.names.tc.xacml._2_0.context.schema.os.ActionType;
import oasis.names.tc.xacml._2_0.context.schema.os.AttributeType;
import oasis.names.tc.xacml._2_0.context.schema.os.AttributeValueType;
import oasis.names.tc.xacml._2_0.context.schema.os.EnvironmentType;
import oasis.names.tc.xacml._2_0.context.schema.os.ObjectFactory;
import oasis.names.tc.xacml._2_0.context.schema.os.RequestType;
import oasis.names.tc.xacml._2_0.context.schema.os.ResourceType;
import oasis.names.tc.xacml._2_0.context.schema.os.SubjectType;

/**
 * Generate XACML request v2.0
 * 
 * @author CanhNT <canhnt@gmail.com>
 *
 */
public class RequestGenerator2 {
	protected Map<String, List<String>> subjAttrMapperValues;
	protected Map<String, List<String>> resAttrMapperValues;
	protected Map<String, List<String>> actAttrMapperValues;
	protected Map<String, List<String>> envAttrMapperValues;
	
	protected Map<String, String> attrMapperDataTypes;
	protected Map<String, Random> attrRnds;
	
	protected ObjectFactory factory;
	
	public RequestGenerator2() {
		subjAttrMapperValues = new HashMap<String, List<String>>();
		resAttrMapperValues = new HashMap<String, List<String>>();
		actAttrMapperValues = new HashMap<String, List<String>>();
		envAttrMapperValues = new HashMap<String, List<String>>();
				
		attrMapperDataTypes = new HashMap<String, String>();
		attrRnds = new HashMap<String, Random>();
		
		factory = new ObjectFactory();
	}
	
	public void addSubjectAttribute(String attrId, String dataType, List<String> attrValues ) {
		List<String> values = new ArrayList<String>(attrValues);
		
		subjAttrMapperValues.put(attrId, values);
		attrMapperDataTypes.put(attrId, dataType);
		attrRnds.put(attrId,  new Random());
	}

	public void addResourceAttribute(String attrId, String dataType, List<String> attrValues ) {
		List<String> values = new ArrayList<String>(attrValues);
		
		resAttrMapperValues.put(attrId, values);
		attrMapperDataTypes.put(attrId, dataType);
		attrRnds.put(attrId,  new Random());
	}
	
	public void addActionAttribute(String attrId, String dataType, List<String> attrValues ) {
		List<String> values = new ArrayList<String>(attrValues);
		
		actAttrMapperValues.put(attrId, values);
		attrMapperDataTypes.put(attrId, dataType);
		attrRnds.put(attrId,  new Random());
	}
	
	public void addEnvAttribute(String attrId, String dataType, List<String> attrValues ) {
		List<String> values = new ArrayList<String>(attrValues);
		
		envAttrMapperValues.put(attrId, values);
		attrMapperDataTypes.put(attrId, dataType);
		attrRnds.put(attrId,  new Random());
	}
	
	public RequestType generate() {
		 
		RequestType request = factory.createRequestType();
		
		SubjectType subj = factory.createSubjectType();
		for(String attrId: subjAttrMapperValues.keySet() ) {
			AttributeType attr = createAttribute(attrId, subjAttrMapperValues.get(attrId));
			subj.getAttribute().add(attr);
		}
		request.getSubject().add(subj);
		
		ResourceType res = factory.createResourceType();
		for(String attrId: resAttrMapperValues.keySet() ) {
			AttributeType attr = createAttribute(attrId, resAttrMapperValues.get(attrId));
			res.getAttribute().add(attr);
		}
		request.getResource().add(res);
		
		ActionType act = factory.createActionType();
		for(String attrId: actAttrMapperValues.keySet() ) {
			AttributeType attr = createAttribute(attrId, actAttrMapperValues.get(attrId));
			act.getAttribute().add(attr);
		}
		request.setAction(act);
		
		EnvironmentType env = factory.createEnvironmentType();
		for(String attrId: envAttrMapperValues.keySet() ) {
			AttributeType attr = createAttribute(attrId, envAttrMapperValues.get(attrId));
			env.getAttribute().add(attr);
		}
		request.setEnvironment(env);
		
		return request;	
	}

	protected AttributeType createAttribute(String attrId, List<String> lstValues) {
		AttributeType attr = factory.createAttributeType();
		attr.setAttributeId(attrId);
		attr.setDataType(attrMapperDataTypes.get(attrId));
		
		Random rnd = attrRnds.get(attrId);
		Object rndValue = lstValues.get(rnd.nextInt(lstValues.size()));

		AttributeValueType attrVal = factory.createAttributeValueType();
		attrVal.getContent().add(rndValue);
		
		attr.getAttributeValue().add(attrVal);

		return attr;
	}
}
