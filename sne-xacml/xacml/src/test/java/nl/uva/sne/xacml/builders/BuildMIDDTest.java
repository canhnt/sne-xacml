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
package nl.uva.sne.xacml.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import nl.uva.sne.xacml.Decision;
import nl.uva.sne.xacml.DecisionType;
import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.Variable;
import nl.uva.sne.xacml.algorithms.DenyOverridesAlg;
import nl.uva.sne.xacml.algorithms.PermitOverridesAlg;
import nl.uva.sne.midd.edges.DoubleEdge;
import nl.uva.sne.midd.edges.StringEdge;
import nl.uva.sne.midd.interval.Interval;
import nl.uva.sne.xacml.nodes.DoubleNode;
import nl.uva.sne.midd.nodes.Node;
import nl.uva.sne.xacml.nodes.StringNode;
import nl.uva.sne.xacml.nodes.internal.InternalXACMLNode;
import nl.uva.sne.xacml.nodes.internal.StateImpl;
import nl.uva.sne.xacml.obligations.Obligation;
import nl.uva.sne.xacml.obligations.ObligationExpression;
import nl.uva.sne.xacml.util.EvaluationUtils;
import nl.uva.sne.xacml.nodes.ExternalNode3;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BuildMIDDTest {

    private InternalXACMLNode<?> midd1;
    private InternalXACMLNode<?> midd2;

    private ObligationExpression oe1 = new ObligationExpression(DecisionType.Permit, new Obligation("O1"));
//    private ObligationExpression oe2 = new ObligationExpression(DecisionType.Deny, new Obligation("O2"));
    private ObligationExpression oe3 = new ObligationExpression(DecisionType.Deny, new Obligation("O3"));
//    private ObligationExpression oe4 = new ObligationExpression(DecisionType.Permit, new Obligation("O4"));

    @Before
    public void setUp() {
        ServiceRegistry.init();
    }

    public void buildMIDD1() throws MIDDException {

        DoubleNode n0 = new DoubleNode(0, StateImpl.of(DecisionType.Indeterminate_P));
        DoubleEdge e00 = new DoubleEdge(new Interval<>(1.0, 2.0, true, true));
        DoubleEdge e01 = new DoubleEdge(new Interval<>(3.0, 4.0, true, true));

        DoubleEdge e10 = new DoubleEdge(new Interval<>(3.0, 4.0, true, true));
        DoubleEdge e11 = new DoubleEdge(new Interval<>(1.0, 2.0, true, true));

        DoubleNode n10 = new DoubleNode(1, StateImpl.of(DecisionType.Indeterminate_P));
        n0.addChild(e00, n10);

        DoubleNode n20 = new DoubleNode(2, StateImpl.of(DecisionType.Indeterminate_P));
        DoubleNode n21 = new DoubleNode(2, StateImpl.of(DecisionType.Indeterminate_P));

        n0.addChild(e01, n21);
        n10.addChild(e10, n20);
        n10.addChild(e11, n21);

        List<ObligationExpression> oe = Arrays.asList((new ObligationExpression[]{oe1}));
        ExternalNode3 n3 = new ExternalNode3(DecisionType.Permit, oe);

        DoubleEdge e20 = new DoubleEdge(new Interval<>(3.0, 4.0, true, true));
        DoubleEdge e21 = new DoubleEdge(new Interval<>(1.0, 2.0, true, true));

        n20.addChild(e20, n3);
        n21.addChild(e21, n3);

        midd1 = n0;
    }

    @Test
    public void testEvalMIDD1() throws MIDDException {
        buildMIDD1();
        // (1.5, 2, 1) -> P,O1
        // (1.5, 2, null) -> IN_P
        // (1.5, 2, 3) -> NA
        Variable<?> request1[] = new Variable<?>[]{
                new Variable<>(0, 1.5),
                new Variable<>(1, 2.0),
                new Variable<>(2, 1.0)
        };

        Variable<?> request2[] = new Variable<?>[]{
                new Variable<>(0, 1.5),
                new Variable<>(1, 2.0),
                new Variable<>(2, 0.0)
        };

        Variable<?> request3[] = new Variable<?>[]{
                new Variable<>(0, 1.5),
                new Variable<>(1, 2.0),
                new Variable<>(2, 3.0)
        };


        long startTime = System.currentTimeMillis();
        Decision result1 = null;
        for (int i = 0; i < 100000; i++) {
            result1 = EvaluationUtils.eval(midd1, createRequest(request1));
        }
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Runtime 1:(microsec)" + totalTime / 100.0);

        assertTrue(result1.getDecision() == DecisionType.Permit);
        assertTrue(result1.getObligations().get(0).equals(oe1.getObligation()));

//		startTime = System.currentTimeMillis();
        Decision result2 = EvaluationUtils.eval(midd1, createRequest(request2));
//		endTime = System.currentTimeMillis();
//		totalTime += endTime - startTime;
        assertTrue(result2.getDecision() == DecisionType.Indeterminate_P);

//		startTime = System.currentTimeMillis();
        Decision result3 = EvaluationUtils.eval(midd1, createRequest(request3));
//		endTime = System.currentTimeMillis();
//		totalTime += endTime - startTime;
        assertTrue(result3.getDecision() == DecisionType.Indeterminate_P);

//		System.out.println("Runtime average:" + totalTime/3.0);
    }

    private Map<Integer, Variable<?>> createRequest(Variable<?>[] request) {
        Map<Integer, Variable<?>> variables = new HashMap<>();
        for (Variable<?> var : request) {
            variables.put(var.getID(), var);
        }
        return variables;
    }

    public void buildMIDD2() throws MIDDException {
        DoubleNode n0 = new DoubleNode(0, StateImpl.of(DecisionType.Indeterminate_D));
        DoubleEdge e00 = new DoubleEdge(new Interval<>(1.0));
        DoubleEdge e01 = new DoubleEdge(new Interval<>(2.0, 3.0, true, true));
        DoubleEdge e02 = new DoubleEdge(new Interval<>(4.0));

        DoubleNode n10 = new DoubleNode(1, StateImpl.of(DecisionType.Indeterminate_D));
        DoubleNode n11 = new DoubleNode(1, StateImpl.of(DecisionType.Indeterminate_D));
        DoubleNode n12 = new DoubleNode(1, StateImpl.of(DecisionType.Indeterminate_D));

        n0.addChild(e00, n10);
        n0.addChild(e01, n11);
        n0.addChild(e02, n12);


        List<ObligationExpression> oe = Arrays.asList((new ObligationExpression[]{
                oe3}));

        ExternalNode3 n2 = new ExternalNode3(DecisionType.Deny, oe);

        DoubleEdge e10 = new DoubleEdge(new Interval<>(4.0));
        DoubleEdge e11 = new DoubleEdge(new Interval<>(2.0));
        DoubleEdge e12 = new DoubleEdge(new Interval<>(3.0));

        n10.addChild(e10, n2);
        n11.addChild(e11, n2);

        n12.addChild(e12, n2);

        midd2 = n0;
    }


    /**
     * Combining two MIDDs using Permit-override algorithm
     */
    @Test
    public void combinePermitOverrideTest() throws MIDDException {
        System.out.println("Combining IDDs with Permit-override");
        buildMIDD1();
        buildMIDD2();

        MIDDCombiner combiner = new MIDDCombiner(new PermitOverridesAlg());
        Node root = combiner.combine(midd1, midd2);

        if (root instanceof InternalXACMLNode<?>) {
            InternalXACMLNode<?> n = (InternalXACMLNode<?>) root;

            // (1, 4, 3.5) -> P,O1O2
            // (1, 4, null) -> IN_DP
            // (1, 4, 4.5) -> D,O2
            Variable<?> request1[] = new Variable<?>[]{
                    new Variable<>(0, 1.0),
                    new Variable<>(1, 4.0),
                    new Variable<>(2, 3.5)
            };
            Decision result1 = EvaluationUtils.eval(n, createRequest(request1));
            assertTrue(result1.getDecision() == DecisionType.Permit);
            List<Obligation> obligations1 = result1.getObligations();
            assertEquals(obligations1.size(), 1);
            assertTrue(obligations1.contains(oe1.getObligation()));
//			assertTrue(obligations1.contains(oe4.getObligation()));

            Variable<?> request2[] = new Variable<?>[]{
                    new Variable<>(0, 1.0),
                    new Variable<>(1, 4.0),
                    new Variable<>(2, 0.0)
            };
            Decision result2 = EvaluationUtils.eval(n, createRequest(request2));
            assertTrue(result2.getDecision() == DecisionType.Deny);

            Variable<?> request3[] = new Variable<?>[]{
                    new Variable<>(0, 1.0),
                    new Variable<>(1, 4.0),
                    new Variable<>(2, 4.5)
            };
            Decision result3 = EvaluationUtils.eval(n, createRequest(request3));
            assertTrue(result3.getDecision() == DecisionType.Deny);
            assertTrue(result3.getObligations().get(0).equals(oe3.getObligation()));

            Variable<?> request4[] = new Variable<?>[]{
                    new Variable<>(0, 3.0),
                    new Variable<>(1, 2.0),
                    new Variable<>(2, 2.0)
            };
            Decision result4 = EvaluationUtils.eval(n, createRequest(request4));
            System.out.println(result4);

        } else {
            fail("Combining two MIDD failed");
        }
    }


    /**
     * Combining two MIDDs using Deny-override algorithm
     */
    @Test
    public void combineDenyOverrideTest() throws MIDDException {
        System.out.println("Combining IDDs with Deny-override");
        buildMIDD1();
        buildMIDD2();

        MIDDCombiner combiner = new MIDDCombiner(new DenyOverridesAlg());
        Node root = combiner.combine(midd1, midd2);

        if (root instanceof InternalXACMLNode<?>) {
            InternalXACMLNode<?> n = (InternalXACMLNode<?>) root;

            // (1, 4, 3.5) -> P,O1O2
            // (1, 4, null) -> IN_DP
            // (1, 4, 4.5) -> D,O2
            Variable<?> request1[] = new Variable<?>[]{
                    new Variable<>(0, 1.0),
                    new Variable<>(1, 4.0),
                    new Variable<>(2, 3.5)
            };
            Decision result1 = EvaluationUtils.eval(n, createRequest(request1));
            assertTrue(result1.getDecision() == DecisionType.Deny);
            List<Obligation> obligations1 = result1.getObligations();
            assertEquals(obligations1.size(), 1);
//			assertTrue(obligations1.contains(oe2.getObligation()));
            assertTrue(obligations1.contains(oe3.getObligation()));

            Variable<?> request2[] = new Variable<?>[]{
                    new Variable<>(0, 1.0),
                    new Variable<>(1, 4.0),
                    new Variable<>(2, 0.0)
            };
            Decision result2 = EvaluationUtils.eval(n, createRequest(request2));
            assertTrue(result2.getDecision() == DecisionType.Deny);
            assertTrue(obligations1.contains(oe3.getObligation()));
            System.out.println(result2);

            Variable<?> request3[] = new Variable<?>[]{
                    new Variable<>(0, 1.0),
                    new Variable<>(1, 4.0),
                    new Variable<>(2, 4.5)
            };
            Decision result3 = EvaluationUtils.eval(n, createRequest(request3));
            assertTrue(result3.getDecision() == DecisionType.Deny);
            assertTrue(result3.getObligations().get(0).equals(oe3.getObligation()));

            Variable<?> request4[] = new Variable<?>[]{
                    new Variable<>(0, 2.0),
                    new Variable<>(1, 2.0),
                    new Variable<>(2, 1.0)
            };
            Decision result4 = EvaluationUtils.eval(n, createRequest(request4));
            System.out.println(result4);

        } else {
            fail("Combining two MIDD failed");
        }
    }

    @Test
    public void buildMIDD3() throws MIDDException {
        System.out.println("Eval with string IDD");
        StringNode root = new StringNode(0, StateImpl.of(DecisionType.Indeterminate_P));
        StringEdge role01 = new StringEdge(new Interval<>("VIO"));

        StringNode resourceType = new StringNode(1, StateImpl.of(DecisionType.Indeterminate_P));
        StringEdge resType01 = new StringEdge(new Interval<>("VI"));

        StringNode action = new StringNode(2, StateImpl.of(DecisionType.Indeterminate_P));
        List<Interval<String>> lstActions = new ArrayList<>();

        lstActions.add(new Interval<>("MLI:Request-VI"));
        lstActions.add(new Interval<>("MLI:Instantiate-VI"));
        lstActions.add(new Interval<>("MLI:Decommission-VI"));
        StringEdge a01 = new StringEdge(lstActions);

        List<ObligationExpression> oe = Arrays.asList((new ObligationExpression[]{oe1}));
        ExternalNode3 externalNode = new ExternalNode3(DecisionType.Permit, oe);

        root.addChild(role01, resourceType);

        resourceType.addChild(resType01, action);

        action.addChild(a01, externalNode);


        Variable<?> request1[] = new Variable<?>[]{
                new Variable<>(0, "VIO"),
                new Variable<>(1, "VI"),
                new Variable<>(2, "MLI:Request-VI")
        };
        long startTime = System.currentTimeMillis();
        Decision result1 = null;
        for (int i = 0; i < 1000; i++) {
            result1 = EvaluationUtils.eval(root, createRequest(request1));
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        System.out.println("Runtime 1:(microsec)" + totalTime);

        System.out.println(result1);

    }
}
