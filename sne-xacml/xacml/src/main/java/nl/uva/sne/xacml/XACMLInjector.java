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

package nl.uva.sne.xacml;

import com.google.inject.assistedinject.FactoryModuleBuilder;

import nl.uva.sne.midd.MIDDInjector;
import nl.uva.sne.midd.builders.MIDDNodeFactory;
import nl.uva.sne.midd.nodes.NodeFactory;
import nl.uva.sne.xacml.builders.MIDDCombiner;
import nl.uva.sne.xacml.builders.MIDDCombinerFactory;
import nl.uva.sne.xacml.builders.MIDDCombinerImpl;
import nl.uva.sne.xacml.builders.XACMLNodeFactory;
import nl.uva.sne.xacml.builders.XNodeFactory;
import nl.uva.sne.xacml.policy.parsers.AnyOfExpression;
import nl.uva.sne.xacml.policy.parsers.AnyOfExpressionFactory;
import nl.uva.sne.xacml.policy.parsers.PolicyParser;
import nl.uva.sne.xacml.policy.parsers.PolicyParserFactory;
import nl.uva.sne.xacml.policy.parsers.PolicySetParser;
import nl.uva.sne.xacml.policy.parsers.PolicySetParserFactory;
import nl.uva.sne.xacml.policy.parsers.RuleParser;
import nl.uva.sne.xacml.policy.parsers.RuleParserFactory;
import nl.uva.sne.xacml.policy.parsers.TargetExpression;
import nl.uva.sne.xacml.policy.parsers.TargetExpressionFactory;

/**
 * @author cngo
 * @version $Id$
 * @since 2016-04-10
 */
public class XACMLInjector extends MIDDInjector {
    @Override
    protected void configure() {
        super.configure();

        bind(MIDDNodeFactory.class).to(XACMLNodeFactory.class);
        bind(XNodeFactory.class).to(XACMLNodeFactory.class);

        install(new FactoryModuleBuilder()
            .implement(MIDDCombiner.class, MIDDCombinerImpl.class)
            .build(MIDDCombinerFactory.class));

        install(new FactoryModuleBuilder()
                .implement(AnyOfExpression.class, AnyOfExpression.class)
                .build(AnyOfExpressionFactory.class));

        install(new FactoryModuleBuilder()
                .implement(PolicyParser.class, PolicyParser.class)
                .build(PolicyParserFactory.class));

        install(new FactoryModuleBuilder()
                .implement(PolicySetParser.class, PolicySetParser.class)
                .build(PolicySetParserFactory.class));

        install(new FactoryModuleBuilder()
                .implement(RuleParser.class, RuleParser.class)
                .build(RuleParserFactory.class));

        install(new FactoryModuleBuilder()
                .implement(TargetExpression.class, TargetExpression.class)
                .build(TargetExpressionFactory.class));
    }
}
