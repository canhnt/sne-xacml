/*
 * Copyright (C) 2013 Canh T. Ngo <canhnt@gmail.com>
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
package nl.uva.sne.xacml.evaltest;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;

import com.sun.xacml.PDP;
import com.sun.xacml.UnknownIdentifierException;
import com.sun.xacml.ctx.ResponseCtx;

import org.junit.Test;
import org.xml.sax.SAXException;

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.xacml.evaltest.sunxacml.RequestGenerator23;
import nl.uva.sne.xacml.policy.parsers.MIDDParsingException;
import nl.uva.sne.xacml.policy.parsers.XACMLParsingException;
import oasis.names.tc.xacml._2_0.context.schema.os.RequestType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ResponseType;
import static org.junit.Assert.assertTrue;

public class SNEXACMLValidationTest extends AbstractEvaluationTest {
    public static final String[] STATIC_XACML2_POLICIES = {
            "policies/continue-a.xml"
    };
    public static final String[] STATIC_XACML2_REF_POLICIES = {
    };
    private static final String LOGGING_FILE = "sne-xacml-validation-logging.txt";
    // the policy that has 'resource-id' attribute, not 'resource-class'
    private static final String XACML3_POLICY_FILE = "policies/continue-a-xacml3.xml";
    private static final int RANDOM_REQUESTS = 1000;


    PrintWriter logger;

    // The generator to generate both identical XACML 2.0 & 3.0 requests
    private RequestGenerator23 generator;

    public SNEXACMLValidationTest() throws SecurityException, IOException {
        logger = new PrintWriter(LOGGING_FILE);
    }

    @Test
    public void runTest() throws ParserConfigurationException, SAXException, IOException, URISyntaxException, UnknownIdentifierException, XACMLParsingException, MIDDException {
        SNEXACMLValidationTest instance = new SNEXACMLValidationTest();

        assertTrue(instance.validate(RANDOM_REQUESTS));

    }

    /**
     * Validate random requests against two PDP: SunXACML & SNE-XACML
     *
     * @param NumberOfRequests
     * @throws URISyntaxException
     * @throws UnknownIdentifierException
     * @throws MIDDParsingException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XACMLParsingException
     * @throws MIDDException
     */
    private boolean validate(int NumberOfRequests) throws URISyntaxException, UnknownIdentifierException, ParserConfigurationException, SAXException, IOException, XACMLParsingException, MIDDException {
        turnOffLogger();

        initGenerator();
        //create a SunXACML PDP
        PDP pdp2 = initSunXACMLPDP(Arrays.asList(STATIC_XACML2_POLICIES), Arrays.asList(STATIC_XACML2_REF_POLICIES));

        // create a SNE-XACML PDP
        nl.uva.sne.xacml.PDP pdp3 = initSNEXACMLPDP(XACML3_POLICY_FILE);

        RequestType request2 = null;
        oasis.names.tc.xacml._3_0.core.schema.wd_17.RequestType request3 = null;
        ResponseCtx response2;
        ResponseType response3;

        int cntInvalidResponses = 0;

        for (int i = 0; i < NumberOfRequests; i++) {

            // Generate two identical requests
            request2 = this.generator.generate();
            request3 = generator.getRequest3();

            // get response from SunXACML PDP
            response2 = pdp2.evaluate(request2);

            // get response from SNE-XACML PDP
            response3 = pdp3.evaluate(request3);

            if (response2 == null) {
                throw new RuntimeException("Invalid SunXACML evaluation for the request:" + print(request3));
            } else if (response3 == null) {
                throw new RuntimeException("Invalid SNEXACML evaluation for the request:" + print(request3));
            } else if (!verifyResponses(response2, response3)) {
                logger.println("Inconsistent SNEXACML response for the request:\n" + print(request2) + "\n" + print(request3));
                cntInvalidResponses++;
            }
        }
        String outputMsg = "Performed validating " + NumberOfRequests + " random requests. Unmatched requests are: " + cntInvalidResponses;
        System.out.println(outputMsg);
        logger.println(outputMsg);
        return cntInvalidResponses == 0;
    }


    private void initGenerator() {
        generator = new RequestGenerator23();
        String[] ACTION_TYPE_VALUES = {
                "create",
                "delete",
                "read",
                "write"
        };

        String[] RESOURCE_CLASS_VALUES = {
                "conferenceInfo_rc",
                "conference_rc",
                "isMeetingFlag_rc",
                "paper-assignments_rc",
                "paper-conflicts_rc",
                "paper-decision_rc",
                "paper_rc",
                "paper-review-content-commentsAll_rc",
                "paper-review-content-commentsPc_rc",
                "paper-review-content-rating_rc",
                "paper-review-content_rc",
                "paper-review-info_rc",
                "paper-review-info-reviewer_rc",
                "paper-review-info-submissionStatus_rc",
                "paper-review_rc",
                "paper-submission-file_rc",
                "paper-submission-info_rc",
                "paper-submission_rc",
                "pcMember-assignmentCount_rc",
                "pcMember-assignments_rc",
                "pcMember-conflicts_rc",
                "pcMember-info-isChairFlag_rc",
                "pcMember-info-password_rc",
                "pcMember-info_rc",
                "pcMember_rc"
        };

        generator.addSubjectAttribute("role", "http://www.w3.org/2001/XMLSchema#string", Arrays.asList(new String[]{"admin", "pc-chair", "pc-member", "subreviewer"}));
        generator.addSubjectAttribute("subjReviewsThisResPaper", "http://www.w3.org/2001/XMLSchema#string", Arrays.asList(new String[]{"true", "false"}));
        generator.addSubjectAttribute("isSubjectsMeeting", "http://www.w3.org/2001/XMLSchema#string", Arrays.asList(new String[]{"true", "false"}));
        generator.addSubjectAttribute("isEq-subjUserId-resUserId", "http://www.w3.org/2001/XMLSchema#string", Arrays.asList(new String[]{"true", "false"}));
        generator.addSubjectAttribute("isMeeting", "http://www.w3.org/2001/XMLSchema#string", Arrays.asList(new String[]{"true", "false"}));
        generator.addSubjectAttribute("isConflicted", "http://www.w3.org/2001/XMLSchema#string", Arrays.asList(new String[]{"true", "false"}));
        generator.addSubjectAttribute("isReviewContentInPlace", "http://www.w3.org/2001/XMLSchema#string", Arrays.asList(new String[]{"true", "false"}));
        generator.addSubjectAttribute("hasSubmittedReviewForResPaper", "http://www.w3.org/2001/XMLSchema#string", Arrays.asList(new String[]{"true", "false"}));

        generator.addResourceAttribute("isEq-meetingPaper-resId", "http://www.w3.org/2001/XMLSchema#string", Arrays.asList(new String[]{"true", "false"}));
        generator.addResourceAttribute("isPending", "http://www.w3.org/2001/XMLSchema#string", Arrays.asList(new String[]{"true", "false"}));
        generator.addResourceAttribute("isSeeUnassignedAllowed", "http://www.w3.org/2001/XMLSchema#string", Arrays.asList(new String[]{"true", "false"}));
        generator.addResourceAttribute("phase", "http://www.w3.org/2001/XMLSchema#string", Arrays.asList(new String[]{"discussion", "non-discussion"}));
        generator.addResourceAttribute("urn:oasis:names:tc:xacml:1.0:resource:resource-id",
                "http://www.w3.org/2001/XMLSchema#string", Arrays.asList(RESOURCE_CLASS_VALUES));

        generator.addActionAttribute("urn:oasis:names:tc:xacml:1.0:action:action-id",
                "http://www.w3.org/2001/XMLSchema#string", Arrays.asList(ACTION_TYPE_VALUES));
    }

}
