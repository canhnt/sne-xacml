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
package nl.uva.sne.xacml.policy.parsers.util;

import nl.uva.sne.midd.Variable;
import nl.uva.sne.xacml.AttributeMapper;
import nl.uva.sne.xacml.policy.parsers.MIDDParsingException;
import nl.uva.sne.xacml.policy.parsers.XACMLParsingException;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;

import java.util.List;

public class AttributeConverter {

    private AttributeMapper attrMapper = null;

    public AttributeConverter(AttributeMapper attrMapper) {
        if (attrMapper == null) {
            throw new IllegalArgumentException("Argument AttributeMapper must not be null");
        }

        this.attrMapper = attrMapper;
    }

    public Variable convert(AttributeType attr) throws MIDDParsingException, XACMLParsingException {
        if (attr == null) {
            throw new IllegalArgumentException("Argument AttributeType must not be null");
        }

        // Obtain or add new variable id in the attribute mapper.
        int varId;
        if (!attrMapper.hasVariableId(attr.getAttributeId())) {
            varId = attrMapper.addAttribute(attr.getAttributeId());
        } else {
            varId = this.attrMapper.getVariableId(attr.getAttributeId());
        }

        List<AttributeValueType> lstValues = attr.getAttributeValue();
        if (lstValues == null || lstValues.size() == 0) {
            throw new XACMLParsingException("Empty value in the attribute:" + attr.getAttributeId());
        }

        AttributeValueType attrValue = lstValues.get(0);
        if (attrValue == null || attrValue.getContent() == null || attrValue.getContent().size() == 0) {
            throw new XACMLParsingException("Empty value in the attribute:" + attr.getAttributeId());
        }

        String strValue = (String) attrValue.getContent().get(0);
        Comparable value = DataTypeConverterUtil.convert(strValue, attrValue.getDataType());

        return new Variable(varId, value);
    }

}
