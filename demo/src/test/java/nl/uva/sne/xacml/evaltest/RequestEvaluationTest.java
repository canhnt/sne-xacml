/*
 * Copyright (C) 2016 Canh Ngo <canhnt@gmail.com>
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
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;

import com.sun.xacml.PDP;
import com.sun.xacml.UnknownIdentifierException;
import com.sun.xacml.ctx.ResponseCtx;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.xacml.policy.parsers.XACMLParsingException;
import oasis.names.tc.xacml._2_0.context.schema.os.RequestType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ResponseType;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RequestEvaluationTest extends AbstractEvaluationTest {

    public static final String[] STATIC_XACML2_POLICIES = {
            "policies/continue-a.xml"
    };
    public static final String[] STATIC_XACML2_REF_POLICIES = {
    };

    private static final String LOGGING_FILE = "sne-xacml-validation-logging.txt";
    // the policy that has 'resource-id' attribute, not 'resource-class'
    private static final String XACML3_POLICY_FILE = "policies/continue-a-xacml3.xml";

    private PDP sunPDP;
    private nl.uva.sne.xacml.PDP snePDP;

    @Before
    @Override
    public void setUp() {
        super.setUp();

        //create a SunXACML PDP
        try {
            sunPDP = initSunXACMLPDP(Arrays.asList(STATIC_XACML2_POLICIES), Arrays.asList(STATIC_XACML2_REF_POLICIES));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail("Cannot load policies");
        } catch (UnknownIdentifierException e) {
            e.printStackTrace();
            fail();
        }

        // create a SNE-XACML PDP
        try {
            snePDP = initSNEXACMLPDP(XACML3_POLICY_FILE);
        } catch (MIDDException e) {
            e.printStackTrace();
            fail("SNE-XACML initialization error");
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void validateResults() throws URISyntaxException, UnknownIdentifierException, ParserConfigurationException, SAXException, IOException, XACMLParsingException, MIDDException {
        turnOffLogger();

        final RequestType request2 = loadXACML2Request("src/test/resources/xacml2-request.xml");
        final oasis.names.tc.xacml._3_0.core.schema.wd_17.RequestType request3 = loadXACML3Request("src/test/resources/xacml3-request.xml");

        assertNotNull(request2);
        assertNotNull(request3);

        // get response from SunXACML PDP
        final ResponseCtx response2 = sunPDP.evaluate(request2);

        // get response from SNE-XACML PDP
        final ResponseType response3 = snePDP.evaluate(request3);

        assertNotNull("Invalid SunXACML evaluation", response2);
        assertNotNull("Invalid SNE-XACML evaluation", response3);

        assertTrue(verifyResponses(response2, response3));
    }

    private oasis.names.tc.xacml._3_0.core.schema.wd_17.RequestType loadXACML3Request(final String path) {
        throw new NotImplementedException();
    }

    private RequestType loadXACML2Request(final String requestPath) {
        throw new NotImplementedException();
    }
}
