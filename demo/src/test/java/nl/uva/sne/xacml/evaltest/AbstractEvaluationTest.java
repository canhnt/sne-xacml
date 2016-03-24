package nl.uva.sne.xacml.evaltest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import com.sun.xacml.PDP;
import com.sun.xacml.PDPConfig;
import com.sun.xacml.UnknownIdentifierException;
import com.sun.xacml.combine.PermitOverridesPolicyAlg;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.ctx.Result;
import com.sun.xacml.finder.AttributeFinder;
import com.sun.xacml.finder.AttributeFinderModule;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.PolicyFinderModule;
import com.sun.xacml.finder.impl.CurrentEnvModule;
import com.sun.xacml.finder.impl.SelectorModule;
import com.sun.xacml.support.finder.StaticPolicyFinderModule;
import com.sun.xacml.support.finder.StaticRefPolicyFinderModule;

import org.junit.Before;
import org.xml.sax.SAXException;

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.xacml.builders.ServiceRegistry;
import nl.uva.sne.xacml.evaltest.sunxacml.SunXACMLUtil;
import nl.uva.sne.xacml.policy.parsers.MIDDParsingException;
import nl.uva.sne.xacml.policy.parsers.XACMLParsingException;
import nl.uva.sne.xacml.util.XACMLUtil;
import oasis.names.tc.xacml._2_0.context.schema.os.RequestType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.DecisionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ObjectFactory;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ResponseType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ResultType;

/**
 * @author cngo
 * @version $Id$
 * @since 2016-03-24
 */
public class AbstractEvaluationTest {

    @Before
    public void setUp() {
        ServiceRegistry.init();
    }

    protected nl.uva.sne.xacml.PDP initSNEXACMLPDP(final String policyFile) throws ParserConfigurationException, SAXException, IOException, MIDDParsingException, XACMLParsingException, MIDDException {
        PolicySetType policyset = XACMLUtil.unmarshalPolicySetType(policyFile);
        nl.uva.sne.xacml.PDP pdp = new nl.uva.sne.xacml.PDP(policyset, null);
        pdp.initialize();

        return pdp;
    }

    protected PDP initSunXACMLPDP(final List<String> staticPolicies, final List<String> staticRefPolicies) throws URISyntaxException, UnknownIdentifierException {
        final StaticPolicyFinderModule staticModule = new StaticPolicyFinderModule(PermitOverridesPolicyAlg.algId, staticPolicies);
        final StaticRefPolicyFinderModule staticRefModule = new StaticRefPolicyFinderModule(staticRefPolicies);

        final Set<PolicyFinderModule> policyModules = new HashSet<>();
        policyModules.add(staticModule);
        policyModules.add(staticRefModule);

        final PDPConfig config = createPDPConfig(policyModules);
        return new PDP(config);
    }

    public void turnOffLogger() {
        LogManager.getLogManager().reset();
        Logger globalLogger = Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);
        globalLogger.setLevel(java.util.logging.Level.OFF);
    }


    /**
     * Create a basic PDPConfig object from a set of PolicyFinderModule
     *
     * @param policyModules
     * @return
     */
    protected PDPConfig createPDPConfig(
            Set<PolicyFinderModule> policyModules) {
        AttributeFinder attributeFinder = new AttributeFinder();

        List<AttributeFinderModule> attrModules = new ArrayList<AttributeFinderModule>();
        attrModules.add(new CurrentEnvModule());
        attrModules.add(new SelectorModule());
        attributeFinder.setModules(attrModules);

        PolicyFinder policyFinder = new PolicyFinder();
        policyFinder.setModules(policyModules);

        return new PDPConfig(null, policyFinder, null);
    }

    public static boolean verifyResponses(ResponseCtx response2,
                                 ResponseType response3) {
        if (response2 == null || response3 == null) {
            return false;
        }

        if (response3.getResult() == null || response3.getResult().isEmpty()) {
            return false;
        }

        final ResultType result3 = response3.getResult().get(0);

        final Iterator it = response2.getResults().iterator();
        // only use the first result
        if (!it.hasNext()) {
//            System.err.println("Not Decision found in response2");
            return false;
        }

        final Result result2 = (Result) it.next();

        if (result3.getDecision() == DecisionType.PERMIT) {
            return result2.getDecision() == Result.DECISION_PERMIT;
        } else if (result3.getDecision() == DecisionType.DENY) {
            return result2.getDecision() == Result.DECISION_DENY;
        } else if (result3.getDecision() == DecisionType.NOT_APPLICABLE) {
            return result2.getDecision() == Result.DECISION_NOT_APPLICABLE;
        } else if (result3.getDecision() == DecisionType.INDETERMINATE) {
            return result2.getDecision() == Result.DECISION_INDETERMINATE;
        }

        return false;
    }

    public static String print(RequestType request2) {
        return SunXACMLUtil.toString(SunXACMLUtil.marshall(request2));
    }

    public static String print(oasis.names.tc.xacml._3_0.core.schema.wd_17.RequestType request3) {
        final OutputStream os = new ByteArrayOutputStream();
        XACMLUtil.print((new ObjectFactory()).createRequest(request3),
                oasis.names.tc.xacml._3_0.core.schema.wd_17.RequestType.class, os);

        return os.toString();
    }
}
