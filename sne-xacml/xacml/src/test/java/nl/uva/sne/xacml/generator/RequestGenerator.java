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
package nl.uva.sne.xacml.generator;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.*;

import java.util.*;

public class RequestGenerator {

    private static final String SUBJECT_CATEGORY = "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject";
    private static final String RESOURCE_CATEGORY = "urn:oasis:names:tc:xacml:3.0:attribute-category:resource";
    private static final String ACTION_CATEGORY = "urn:oasis:names:tc:xacml:3.0:attribute-category:action";
    /**
     * Map of attribute-id to their possible values
     */
    Map<String, List<String>> attrMapperValues;
    Map<String, String> attrMapperDataTypes;
    Map<String, String> attrMapperCategories;
    Map<String, Random> attrRnd;

    ObjectFactory factory;

    public RequestGenerator() {
        attrMapperValues = new HashMap<String, List<String>>();
        attrMapperDataTypes = new HashMap<String, String>();
        attrMapperCategories = new HashMap<String, String>();

        attrRnd = new HashMap<String, Random>();

        factory = new ObjectFactory();
    }

    public void addAttribute(String category, String attrId, String dataType, List<String> attrValues) {
        List<String> values = new ArrayList<String>(attrValues);

        attrMapperValues.put(attrId, values);
        attrMapperDataTypes.put(attrId, dataType);
        attrMapperCategories.put(attrId, category);
        attrRnd.put(attrId, new Random());
    }

    public void addSubjectAttribute(String attrId, String dataType, List<String> attrValues) {
        addAttribute(SUBJECT_CATEGORY, attrId, dataType, attrValues);
    }

    public void addResourceAttribute(String attrId, String dataType, List<String> attrValues) {
        addAttribute(RESOURCE_CATEGORY, attrId, dataType, attrValues);
    }

    public void addActionAttribute(String attrId, String dataType, List<String> attrValues) {
        addAttribute(ACTION_CATEGORY, attrId, dataType, attrValues);
    }

    public RequestType generate() {

        RequestType request = factory.createRequestType();

        for (String attrId : attrMapperValues.keySet()) {
            AttributesType attrs = createAttributes(attrId, attrMapperValues.get(attrId));
            request.getAttributes().add(attrs);
        }

        return request;

    }

    protected AttributesType createAttributes(String attrId, List<String> lstValues) {
        AttributesType attrs = factory.createAttributesType();
        attrs.setCategory(attrMapperCategories.get(attrId));

        AttributeValueType attrValue = factory.createAttributeValueType();
        attrValue.setDataType(this.attrMapperDataTypes.get(attrId));

        Random rnd = this.attrRnd.get(attrId);
        Object rndValue = lstValues.get(rnd.nextInt(lstValues.size()));
        attrValue.getContent().add(rndValue);

        AttributeType attr = factory.createAttributeType();
        attr.setAttributeId(attrId);
        attr.getAttributeValue().add(attrValue);

        attrs.getAttribute().add(attr);
        return attrs;
    }

}
