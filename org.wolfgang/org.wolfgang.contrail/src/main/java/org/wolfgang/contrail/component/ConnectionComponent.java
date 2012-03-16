/*
 * Copyright (C)2012 D. Plaindoux.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; see the file COPYING.  If not, write to
 * the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package org.wolfgang.contrail.component;

/**
 * The <code>ConnectionComponent</code> is capable to connect a source handler
 * and a destination handler. In addition the data can be transformed from a
 * type S to a type D or vice-versa depending if components communicate using
 * upstream or downstream network. The first type was the event type received
 * from the upstream network - denoted by the destination facet. The second type
 * was the event type sent to the upstream network - denoted by the source
 * facet.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface ConnectionComponent<S, D> extends DestinationComponent<S>, SourceComponent<D> {

	// Nothing

}
