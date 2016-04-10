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

package nl.uva.sne.xacml.policy.parsers;

import com.google.inject.Provider;

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.builders.MIDDBuilder;
import nl.uva.sne.midd.nodes.Node;
import nl.uva.sne.xacml.AttributeMapper;
import nl.uva.sne.xacml.policy.finder.PolicyFinder;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySetType;

/**
 * @author cngo
 * @version $Id$
 * @since 2016-04-10
 */
public class PolicySetParserFactoryImpl implements PolicySetParserFactory {
    private final Provider<MIDDBuilder> middBuilderProvider;

    public PolicySetParserFactoryImpl(Provider<MIDDBuilder> middBuilderProvider) {
        this.middBuilderProvider = middBuilderProvider;
    }

    @Override
    public PolicySetParser create(final Node condition,
                                  final PolicySetType policyset,
                                  final AttributeMapper attrMapper) throws MIDDException {
        return create(condition, policyset, attrMapper, null);
    }

    @Override
    public PolicySetParser create(final Node condition,
                                  final PolicySetType policyset,
                                  final AttributeMapper attrMapper,
                                  final PolicyFinder policyFinder) throws MIDDException {
        return new PolicySetParser(middBuilderProvider.get(),
                condition, policyset, attrMapper, policyFinder);
    }
}
