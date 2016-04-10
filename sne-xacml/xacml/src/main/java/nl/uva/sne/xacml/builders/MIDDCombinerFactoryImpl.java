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

package nl.uva.sne.xacml.builders;

import com.google.inject.Provider;

import nl.uva.sne.xacml.algorithms.CombiningAlgorithm;

/**
 * @author cngo
 * @version $Id$
 * @since 2016-04-10
 */
public class MIDDCombinerFactoryImpl implements MIDDCombinerFactory {
    private Provider<XNodeFactory> xNodeFactoryProvider;

    @Override
    public MIDDCombiner create(final CombiningAlgorithm algorithm) {
        return new MIDDCombiner(xNodeFactoryProvider.get(), algorithm);
    }
}
