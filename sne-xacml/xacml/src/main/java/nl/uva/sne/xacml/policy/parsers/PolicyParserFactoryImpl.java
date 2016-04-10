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
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicyType;

/**
 * @author cngo
 * @version $Id$
 * @since 2016-04-10
 */
public class PolicyParserFactoryImpl implements PolicyParserFactory {
    private Provider<MIDDBuilder> middBuilderProvider;

    public PolicyParserFactoryImpl(Provider<MIDDBuilder> middBuilderProvider) {
        this.middBuilderProvider = middBuilderProvider;
    }

    @Override
    public PolicyParser create(final Node condition, final PolicyType policy, final AttributeMapper attrMapper) throws MIDDException {
        return new PolicyParser(middBuilderProvider.get(),
                condition, policy, attrMapper);
    }
}
