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
package nl.uva.sne.xacml.policy.parsers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.xacml.algorithms.CombiningAlgorithm;
import nl.uva.sne.midd.builders.ConjunctiveBuilder;
import nl.uva.sne.xacml.builders.MIDDCombiner;
import nl.uva.sne.midd.nodes.ExternalNode;
import nl.uva.sne.midd.nodes.Node;
import nl.uva.sne.midd.nodes.internal.InternalNode;
import nl.uva.sne.midd.util.GenericUtils;
import nl.uva.sne.midd.util.MIDDUtils;
import nl.uva.sne.xacml.AttributeMapper;
import nl.uva.sne.xacml.policy.finder.PolicyFinder;
import nl.uva.sne.xacml.policy.parsers.util.CombiningAlgConverterUtil;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.IdReferenceType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.TargetType;

/**
 * Create MIDD from a XACML 3.0 PolicySet element.
 */
public class PolicySetParser {
    private static final Logger log = LoggerFactory.getLogger(PolicySetParser.class);

    private PolicySetType policyset;

    private Node preCondition;

    private AttributeMapper attrMapper;


    private List<Object> children;

    /**
     * The policy finder is to lookup policy/policyset from references
     */
    private PolicyFinder policyFinder;

    /**
     * Brief constructor not to use policy finder. It does not support references to policies or policysets.
     *
     * @param condition
     * @param policyset
     * @param attrMapper
     */
    public PolicySetParser(Node condition, PolicySetType policyset,
                           AttributeMapper attrMapper) throws MIDDException {
        this(condition, policyset, attrMapper, null);
    }

    /**
     * @param condition a MIDD that represents the target expression of the parents' policyset.
     * @param policy    a XACML 3.0 policy element.
     */
    public PolicySetParser(Node condition, PolicySetType policyset,
                           AttributeMapper attrMapper, PolicyFinder policyFinder) throws MIDDException {
        if (policyset == null) {
            throw new IllegalArgumentException("PolicySetType argument must not be null");
        }
        if (attrMapper == null) {
            throw new IllegalArgumentException("AttributeMapper argument must not be null");
        }

        this.policyFinder = policyFinder;

        this.policyset = policyset;

        this.attrMapper = attrMapper;

        if (condition == null) {
            this.preCondition = new ExternalNode();
        } else {
//			this.preCondition = condition;
            this.preCondition = GenericUtils.newInstance(condition);
        }
    }

    private Node combinePolicyMIDDs(List<Node> lstMIDDs,
                                            CombiningAlgorithm pca) throws MIDDException {
        log.debug("Combining policy set " + this.policyset.getPolicySetId());
        MIDDCombiner combiner = new MIDDCombiner(pca);

        Iterator<Node> it = lstMIDDs.iterator();
        Node root = null;

        while (it.hasNext()) {
            Node n = it.next();
            if (root == null) {
                root = n;
            } else {
                if (root instanceof InternalNode) {
                    log.debug("root size:" + MIDDUtils.countNodes((InternalNode) root));
                }
                if (n instanceof InternalNode) {
                    log.debug("child midd size:" + MIDDUtils.countNodes((InternalNode) n));
                }

                root = combiner.combine(root, n);

                if (root instanceof InternalNode) {
                    log.debug("Combined midd size:" + MIDDUtils.countNodes((InternalNode) root));
                }

            }
        }
        return root;
    }

    /**
     * Retrieve policy/policyset from the policy finder and add to the children policy or policyset lists
     *
     * @param idReference
     */
    private void addChildrenByRef(IdReferenceType idReference) {
        String id = idReference.getValue();
        if (id == null || id.isEmpty()) {
            log.debug("Invalid reference to policy or policyset ");
            return;
        }

        Object obj = policyFinder.lookup(id);

        if (obj instanceof PolicyType || obj instanceof PolicySetType) {
            children.add(obj);
        } else {
            log.debug("No policy/policyset found for the reference " + id);
        }
    }

    private void getChilden() throws XACMLParsingException {
        List<JAXBElement<?>> objs = policyset.getPolicySetOrPolicyOrPolicySetIdReference();

        if (objs == null || objs.size() == 0) {
            throw new XACMLParsingException("No children policy/policyset found in the policyset " + policyset.getPolicySetId());
        }

        children = new ArrayList<>();

        for (JAXBElement<?> obj : objs) {
            if (obj != null) {
                Object objValue = obj.getValue();
                if (objValue instanceof PolicyType || objValue instanceof PolicySetType) {
                    children.add(objValue);
                } else if (objValue instanceof IdReferenceType) {
                    if (policyFinder != null) {
                        addChildrenByRef((IdReferenceType) objValue);
                    } else {
                        log.debug("No policy finder found to lookup reference in the policy: " + policyset.getPolicySetId());
                    }
                } else {
                    // we ignore other types in this version: e.g. references to rule
                    log.info("Unsupported object type:" + objValue.getClass() + "inside the policyset '" + policyset.getPolicySetId() + "'");
                }
            }
        }

        if (children.size() == 0) {
            throw new XACMLParsingException("No children policy/policyset found in the policy: " + policyset.getPolicySetId());
        }
    }

    private Node getTargetCondition() throws XACMLParsingException, MIDDException, MIDDParsingException {
        TargetType target = policyset.getTarget();

        List<AnyOfType> lstAnyOf;
        if (target != null) {
            lstAnyOf = target.getAnyOf();
        } else {
            lstAnyOf = null;
        }

        TargetExpression te = new TargetExpression(lstAnyOf, attrMapper);
        return te.parse();
    }

    public Node parse() throws XACMLParsingException, MIDDException, MIDDParsingException {
        // Get a MIDD to represent the policy's target expression

        Node targetCondition = getTargetCondition();
        if (targetCondition == null) // no applicable MIDD extracted from Target
        {
            return null;
        }

        // Conjunctive join it with the MIDD representing preconditions of the policy
        Node condition = ConjunctiveBuilder.join(this.preCondition, targetCondition);

        getChilden();

        List<Node> lstMIDDs = new ArrayList<Node>();

        // Warning: must convert children policy/policyset in its natural order to compliant with some ordered-RCAs (e.g: First-Applicable)

        // Create MIDDs for children policies
        for (Object obj : this.children) {
            if (obj instanceof PolicyType) {
                PolicyType pol = (PolicyType) obj;
                PolicyParser policyParser = new PolicyParser(condition, pol, attrMapper);

                // return the MIDD with XACML decisions at the external nodes
                Node xacmlMIDD = policyParser.parse();

                if (xacmlMIDD == null) {// a never-applicable rule
                    System.err.println("Found a non-transformable MIDD policy:" + pol.getPolicyId());
                } else {
                    lstMIDDs.add(xacmlMIDD);
                }

            } else if (obj instanceof PolicySetType) {
                PolicySetType polset = (PolicySetType) obj;
                PolicySetParser psParser = new PolicySetParser(condition, polset, attrMapper, policyFinder);

                // return the MIDD with XACML decisions at the external nodes
                Node xacmlMIDD = psParser.parse();
                if (xacmlMIDD == null) {// a never-applicable rule
                    log.error("Found a non-transformable MIDD policy set:" + polset.getPolicySetId());
                } else {
                    lstMIDDs.add(xacmlMIDD);
                }

            } else {
                throw new MIDDParsingException("Unknown children policyset type");
            }
        }

        // combine MIDDs using policyset's policy-combining-algorithm
        CombiningAlgorithm pca = CombiningAlgConverterUtil.getAlgorithm(policyset.getPolicyCombiningAlgId());
        return combinePolicyMIDDs(lstMIDDs, pca);
    }
}
