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
/**
 * 
 */
package nl.uva.sne.xacml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nl.uva.sne.xacml.policy.parsers.MIDDParsingException;

/**
 * Mapping from a string-based attribute identifier in XACML policies to the number-based variable identifier in MIDD structure  
 *
 */
public class AttributeMapper {
	
	private Map<String, Integer> attributeMapper = null;
	
	private int varIdCounter = 0;
	
	public AttributeMapper() {
		attributeMapper = new HashMap<String, Integer>();
		varIdCounter = 0;
	}
	
	/**
	 * Add a new attribute-id to the map, return the variable id. If the attribute existed, throws exception
	 * 
	 * @param attrId
	 * @return
	 * @throws MIDDParsingException
	 */
	public int addAttribute(String attrId) {
		if (attributeMapper.containsKey(attrId))
			return attributeMapper.get(attrId);		// attribute-id has existed
		
		attributeMapper.put(attrId, varIdCounter);
		varIdCounter++;
		return attributeMapper.get(attrId);
	}
	
	/*
	 * Return the variable identifier from the attribute-id
	 */
	public int getVariableId(String attrId ) throws MIDDParsingException {
		if (!attributeMapper.containsKey(attrId))
			throw new MIDDParsingException("Attribute '" + attrId + "' not found");
		
		return attributeMapper.get(attrId).intValue();
	}

	/**
	 * Return the attribute identifier from the variable-id
	 * @param variableId
	 * @return
	 * @throws MIDDParsingException 
	 */
	public String getAttributeId(int variableId) throws MIDDParsingException {
		if (!attributeMapper.containsValue(variableId))
			throw new MIDDParsingException("Variable identifier '" + variableId + "' not found");
		
		for(String attrId: attributeMapper.keySet()) {
			if (attributeMapper.get(attrId) == variableId)
				return attrId;
		}
		
		throw new MIDDParsingException("Variable identifier '" + variableId + "' not found");
	}
	
	public void addAttributes(List<String> attributes) {
		for(String attr:attributes) {
			addAttribute(attr);
		}
		
	}
	
	public void combine(AttributeMapper outerMapper) {
		for(String attrId : outerMapper.attributeMapper.keySet()) {
			if (!this.attributeMapper.containsKey(attrId)) {
				// add new attribute-id
				
			} else {
				
			}
		}
	}
	
	public boolean hasVariableId(String attrId) {
		return attributeMapper.containsKey(attrId);
	}
}
