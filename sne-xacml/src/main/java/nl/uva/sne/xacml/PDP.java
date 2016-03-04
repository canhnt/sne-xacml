/*
 * Copyright (C) 2013-2016 Canh Ngo <canhnt@gmail.com>
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
package nl.uva.sne.xacml;

import nl.uva.sne.midd.Decision;
import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.Variable;
import nl.uva.sne.midd.nodes.AbstractNode;
import nl.uva.sne.midd.nodes.InternalNodeImpl;
import nl.uva.sne.midd.util.EvaluationUtils;
import nl.uva.sne.xacml.builders.ResponseTypeBuilder;
import nl.uva.sne.xacml.policy.finder.PolicyFinder;
import nl.uva.sne.xacml.policy.parsers.MIDDParsingException;
import nl.uva.sne.xacml.policy.parsers.PolicyParser;
import nl.uva.sne.xacml.policy.parsers.PolicySetParser;
import nl.uva.sne.xacml.policy.parsers.XACMLParsingException;
import nl.uva.sne.xacml.policy.parsers.util.AttributeConverter;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class PDP {
    private static final Logger logger = LoggerFactory.getLogger(PDP.class);

    protected AttributeMapper attrMapper;

    protected InternalNodeImpl middRoot = null;

    protected boolean fXACMLParsingError;

    protected boolean fProcessiongError;

    protected PolicySetType policyset;

    protected PolicyType policy;

    private PolicyFinder policyFinder;

    public PDP(PolicySetType policyset, PolicyFinder policyFinder) throws MIDDParsingException, XACMLParsingException, MIDDException {
        if (policyset == null) {
            throw new IllegalArgumentException("Policyset argument must not be null");
        }

        this.policyset = policyset;
        this.policy = null;
        this.policyFinder = policyFinder;

    }

    public void initialize()
            throws MIDDParsingException, XACMLParsingException, MIDDException {
        attrMapper = new AttributeMapper();

        AbstractNode n;
        if (policyset != null) {
            n = buildMIDD(policyset);
        } else if (policy != null) {
            n = buildMIDD(policy);
        } else {
            throw new RuntimeException("Invalid constructing PDP");
        }

        if (!(n instanceof InternalNodeImpl)) {
            throw new MIDDException("Invalid parsing policies to MIDD tree");
        } else {
            this.middRoot = (InternalNodeImpl) n;
        }
    }


    public PDP(PolicyType policy) throws MIDDParsingException, XACMLParsingException, MIDDException {
        if (policy == null) {
            throw new IllegalArgumentException("Policy argument must not be null");
        }

        this.policy = policy;
        policyset = null;
    }

    public ResponseType evaluate(RequestType request) {

        resetErrorFlags();

        Map<Integer, Variable<?>> variables;
        Decision result = null;
        ResponseType response = null;

        try {
            variables = convertRequest(request);

            result = EvaluationUtils.eval(this.middRoot, variables);
        } catch (MIDDParsingException e) {
            logger.error(e.getMessage());
            fProcessiongError = true;
        } catch (XACMLParsingException e) {
            logger.error(e.getMessage());
            fXACMLParsingError = true;
        }

        response = createResponse(result);
        return response;
    }


    protected void resetErrorFlags() {
        fXACMLParsingError = false;
        fProcessiongError = false;
    }

    public Decision evaluate(Map<Integer, Variable<?>> variables) {
        return EvaluationUtils.eval(this.middRoot, variables);
    }

    protected ResponseType createResponse(Decision middDecision) {

        ResponseTypeBuilder builder = new ResponseTypeBuilder(fXACMLParsingError, fProcessiongError);

        return builder.create(middDecision);
    }

    private AbstractNode buildMIDD(PolicySetType policyset) throws MIDDParsingException, XACMLParsingException, MIDDException {
        PolicySetParser parser = new PolicySetParser(null, policyset, this.attrMapper, policyFinder);
        return parser.parse();
    }

    private AbstractNode buildMIDD(PolicyType policy) throws MIDDParsingException, XACMLParsingException, MIDDException {
        PolicyParser parser = new PolicyParser(null, policy, this.attrMapper);
        return parser.parse();
    }

    protected Map<Integer, Variable<?>> convertRequest(RequestType request) throws MIDDParsingException, XACMLParsingException {
        Map<Integer, Variable<?>> variables = new HashMap<Integer, Variable<?>>();

        AttributeConverter attrConverter = new AttributeConverter(this.attrMapper);

        for (AttributesType attrs : request.getAttributes()) {

            if (attrs != null) {
                for (AttributeType attr : attrs.getAttribute()) {
                    if (attr != null) {
                        Variable var = attrConverter.convert(attr);
                        variables.put(var.getID(), var);
                    }
                }
            }
        }
        return variables;
    }

    public void print(OutputStream os) throws MIDDException {
        middRoot.print(os);
    }
}
