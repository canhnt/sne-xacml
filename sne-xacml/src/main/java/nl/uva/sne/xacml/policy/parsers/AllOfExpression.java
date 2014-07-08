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
package nl.uva.sne.xacml.policy.parsers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.interval.EndPoint;
import nl.uva.sne.midd.interval.Interval;
import nl.uva.sne.xacml.policy.parsers.util.DataTypeConverterUtil;
import nl.uva.sne.xacml.policy.parsers.util.MatchIdConverterUtil;
import nl.uva.sne.xacml.policy.parsers.util.MatchIdConverterUtil.OperatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AllOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MatchType;

/**
 * AllOfExpression class is to parse the AllOf XACML 3.0 element, which returns the map of parsed intervals for each
 * variable identifiers.
 * <p/>
 * Note: AllOf XACML3 element contains conjunctive sequence of Match expressions.
 * <p/>
 * AllOfType := SEQUENCE_OF<MatchExpression> MatchExpression := (operator, value, varId) // statement: <value> <op>
 * <varId> operator := {gt, ge, lt, le, eq}
 *
 * @author Canh Ngo (t.c.ngo@uva.nl)
 * @date: Sep 27, 2012
 */
public class AllOfExpression {
    @SuppressWarnings("rawtypes")
    private Map<String, AttributeInfo> variables;


    public AllOfExpression() {
        variables = new HashMap<String, AttributeInfo>();
    }

    /**
     * @return List of variables with their equivalent intervals.
     */
    public Map<String, AttributeInfo> getAttributeInfos() {
        return this.variables;
    }

    private EndPoint getValue(AttributeValueType attributeValue)
            throws XACMLParsingException, MIDDException {
        List<Object> objects = attributeValue.getContent();
        if (objects == null || objects.size() == 0) {
            throw new XACMLParsingException("Cannot extract attribute value");
        }

        // only support 1-value at the moment.

        String strValue = (String) objects.get(0);
        Comparable value = DataTypeConverterUtil.convert(strValue, attributeValue.getDataType());

        return new EndPoint(value);
    }

    public void parse(AllOfType allOf) throws XACMLParsingException, MIDDException {
        List<MatchType> lstMatches = allOf.getMatch();

        // Collect all intervals in the allOf expression and put to each attribute
        Map<String, AttributeInfo> intervals = new HashMap<String, AttributeInfo>();

        for (MatchType match : lstMatches) {
            // validating the match expression
            validate(match);

            String varId = match.getAttributeDesignator().getAttributeId();
            boolean isMustBePresent = match.getAttributeDesignator().isMustBePresent();

            if (!intervals.containsKey(varId)) {
                // initialize interval as (-inf, +inf)
                Interval newInterval = new Interval(new EndPoint(true, false),
                        new EndPoint(false, true));
                intervals.put(varId, new AttributeInfo(isMustBePresent, newInterval));
            }

            // update mustBePresent state of the variable: if it is set before in the expression.
            intervals.get(varId).isMustBePresent = intervals.get(varId).isMustBePresent ^ isMustBePresent;

            Interval interval = intervals.get(varId).getInterval();

            OperatorType operator = MatchIdConverterUtil.getOperator(match
                    .getMatchId());
            EndPoint value = getValue(match.getAttributeValue());

            switch (operator) {
                case LESS_THAN:
                case LESS_THAN_OR_EQUAL: {
                    // find the largest lower-bound
                    if (value.compareTo(interval.getLowerBound()) >= 0) {
                        interval.setLowerBound(value);
                        interval.setLowerBoundClosed(operator == OperatorType.LESS_THAN_OR_EQUAL);
                    }
                    break;
                }
                case GREATER_THAN:
                case GREATER_THAN_OR_EQUAL: {
                    int c = value.compareTo(interval.getUpperBound());
                    if (c <= 0) {
                        interval.setUpperBound(value);
                        interval.setUpperBoundClosed(operator == OperatorType.GREATER_THAN_OR_EQUAL);
                    }
                    break;
                }
                case EQUAL:
                    interval.setSingleValue(value);
                    break;
            }
        }

        // validating extracted intervals
        Map<String, AttributeInfo> validIntervals = new HashMap<String, AttributeInfo>();
        for (String varId : intervals.keySet()) {
            Interval interval = intervals.get(varId).getInterval();

            if (interval.validate()) {
//				System.err.println("Invalid AllOf expression: unable to extract satisfied interval");
                validIntervals.put(varId, intervals.get(varId));
            } else {
//				throw new XACMLParsingException("Invalid AllOf expression: unable to extract satisfied interval");
                System.err.println("Invalid AllOf expression: unable to extract satisfied interval");
            }
        }

//		variables = intervals;
        variables = validIntervals;
    }

    private void validate(MatchType match) throws XACMLParsingException {
        AttributeValueType attrValue = match.getAttributeValue();
        AttributeDesignatorType attrDesignator = match.getAttributeDesignator();
        if (null == attrValue) {
            throw new XACMLParsingException("No attribute value found");
        }
        if (null == attrDesignator) {
            throw new XACMLParsingException("No attribute designator found");
        }

        String attrValueDataType = attrValue.getDataType();
        if (attrValueDataType == null
                || !attrValueDataType.equals(attrDesignator.getDataType())) {
            throw new XACMLParsingException(
                    "Data types in match expression do not valid");
        }
    }


}
