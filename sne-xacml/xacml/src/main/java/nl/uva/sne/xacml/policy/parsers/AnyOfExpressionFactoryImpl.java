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

import nl.uva.sne.midd.builders.MIDDBuilder;
import nl.uva.sne.xacml.AttributeMapper;
import nl.uva.sne.xacml.builders.XACMLNodeFactory;
import nl.uva.sne.xacml.builders.XNodeFactory;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AnyOfType;

/**
 * @author cngo
 * @version $Id$
 * @since 2016-04-10
 */
public class AnyOfExpressionFactoryImpl implements AnyOfExpressionFactory {
    private final Provider<MIDDBuilder> middBuilderProvider;
    private final Provider<XNodeFactory> xNodeFactoryProvider;

    public AnyOfExpressionFactoryImpl(final Provider<MIDDBuilder> middBuilderProvider,
                                      final Provider<XNodeFactory> xNodeFactoryProvider) {
        this.middBuilderProvider = middBuilderProvider;
        this.xNodeFactoryProvider = xNodeFactoryProvider;
    }

    @Override
    public AnyOfExpression create(final AnyOfType anyOf, final AttributeMapper attrMapper) {
        return new AnyOfExpression(middBuilderProvider.get(),
                xNodeFactoryProvider.get(),
                anyOf, attrMapper);
    }
}
