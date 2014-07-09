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
package nl.uva.sne.xacml.builders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.uva.sne.midd.Decision;
import nl.uva.sne.midd.DecisionType;
import nl.uva.sne.midd.obligations.Obligation;
import nl.uva.sne.xacml.policy.parsers.util.DecisionConverterUtil;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObjectFactory;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObligationsType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ResponseType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ResultType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.StatusCodeType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.StatusType;

public class ResponseTypeBuilder {
    private static final Logger logger = LoggerFactory.getLogger(ResponseTypeBuilder.class);

    private static final String STATUS_CODE_OK = "urn:oasis:names:tc:xacml:1.0:status:ok";

    private static final String STATUS_CODE_MISSING_ATTRIBUTE = "urn:oasis:names:tc:xacml:1.0:status:missing-attribute";

    private static final String STATUS_CODE_SYNTAX_ERROR = "urn:oasis:names:tc:xacml:1.0:status:syntax-error";

    private static final String STATUS_CODE_PROCESSING_ERROR = "urn:oasis:names:tc:xacml:1.0:status:processing-error";

    private boolean fXACMLParsingError;

    private boolean fProcessingError;

    public ResponseTypeBuilder(boolean xacmlParsingError,
                               boolean processiongError) {
        this.fXACMLParsingError = xacmlParsingError;
        this.fProcessingError = processiongError;
    }


    public ResponseType create(Decision middDecision) {
        if (middDecision == null) {
            return null;
        }
        ResponseType response = (new ObjectFactory()).createResponseType();

        // Create obligations object
        ObligationsType obligations = (new ObjectFactory()).createObligationsType();
        if (middDecision.getObligations() != null) {
            for (Obligation middObl : middDecision.getObligations()) {
                ObligationType o = (new ObjectFactory()).createObligationType();
                o.setObligationId(middObl.toString());
                obligations.getObligation().add(o);
            }
        }

        // Create status code object
        StatusCodeType statusCode = (new ObjectFactory()).createStatusCodeType();
        statusCode.setValue(getStatusCode(middDecision.getDecision()));
        StatusType status = (new ObjectFactory()).createStatusType();
        status.setStatusCode(statusCode);

        // Create result object
        ResultType result = (new ObjectFactory()).createResultType();
        result.setDecision(DecisionConverterUtil.convertMIDDDecision(middDecision.getDecision()));
        result.setObligations(obligations);
        result.setStatus(status);

        response.getResult().add(result);
        return response;
    }


    /**
     * Dummy convert
     *
     * @param decision
     * @return
     */
    private String getStatusCode(DecisionType decision) {
        if (fXACMLParsingError) {
            return STATUS_CODE_SYNTAX_ERROR;
        } else if (fProcessingError) {
            return STATUS_CODE_PROCESSING_ERROR;
        } else if (decision == DecisionType.Deny ||
                decision == DecisionType.Permit) {
            return STATUS_CODE_OK;
        } else if (decision == DecisionType.Indeterminate ||
                decision == DecisionType.Indeterminate_D ||
                decision == DecisionType.Indeterminate_P ||
                decision == DecisionType.Indeterminate_DP) {
            return STATUS_CODE_MISSING_ATTRIBUTE;
        } else {
            logger.debug("Unknown status code decision");
            return STATUS_CODE_PROCESSING_ERROR;
        }
    }

}
