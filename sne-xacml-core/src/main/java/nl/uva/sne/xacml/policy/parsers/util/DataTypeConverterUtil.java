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
package nl.uva.sne.xacml.policy.parsers.util;

import nl.uva.sne.xacml.policy.parsers.XACMLParsingException;

/**
 * @author canhnt
 */
public class DataTypeConverterUtil {

    public static String XACML_3_0_DATA_TYPE_STRING = "http://www.w3.org/2001/XMLSchema#string";

    public static String XACML_3_0_DATA_TYPE_INTEGER = "http://www.w3.org/2001/XMLSchema#integer";

    public static String XACML_3_0_DATA_TYPE_DOUBLE = "http://www.w3.org/2001/XMLSchema#double";

    public static String XACML_3_0_DATA_TYPE_ANYURI = "http://www.w3.org/2001/XMLSchema#anyURI";

//	public static String[] DATA_TYPES = {
//			XACML_3_0_DATA_TYPE_STRING,
//			XACML_3_0_DATA_TYPE_INTEGER,
//			XACML_3_0_DATA_TYPE_DOUBLE,
//			XACML_3_0_DATA_TYPE_ANYURI
//	};

    public static Comparable<?> convert(String value, String dataType) throws XACMLParsingException {

        if (dataType.equalsIgnoreCase(XACML_3_0_DATA_TYPE_STRING)) {
            return value;
        } else if (dataType.equalsIgnoreCase(XACML_3_0_DATA_TYPE_INTEGER)) {
            return new Integer(Integer.parseInt(value));
        } else if (dataType.equalsIgnoreCase(XACML_3_0_DATA_TYPE_DOUBLE)) {
            return new Double(Double.parseDouble(value));
        } else if (dataType.equalsIgnoreCase(XACML_3_0_DATA_TYPE_ANYURI)) {
            return value;
        } else {
            throw new XACMLParsingException("Not supported data type: " + dataType);
        }
    }


}
