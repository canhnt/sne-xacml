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

package nl.uva.sne.midd.builders;

import nl.uva.sne.midd.MIDDException;
import nl.uva.sne.midd.nodes.Node;

/**
 * @author cngo
 * @version $Id$
 * @since 2016-03-13
 */
public interface MIDDBuilder {

    /**
     * Join two MIDD using conjunctive operator.
     *
     * @param midd1
     * @param midd2
     * @return
     * @throws MIDDException
     */
    Node and(Node midd1, Node midd2) throws MIDDException;

    /**
     * Join two MIDD using disjunctive operator.
     *
     * @param midd1
     * @param midd2
     * @return
     * @throws MIDDException
     */
    Node or(Node midd1, Node midd2) throws MIDDException;
}
