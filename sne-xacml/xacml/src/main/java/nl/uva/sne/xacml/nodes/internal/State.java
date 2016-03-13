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
 *
 */

package nl.uva.sne.xacml.nodes.internal;

import java.util.List;

import nl.uva.sne.xacml.Decision;
import nl.uva.sne.xacml.DecisionType;
import nl.uva.sne.xacml.nodes.ExternalNode3;
import nl.uva.sne.xacml.obligations.ObligationExpression;

public interface State {
    Decision buildDecision();

    ExternalNode3 getExternalNode();

    List<ObligationExpression> getObligationExpressions();

    DecisionType getStateIN();
}
