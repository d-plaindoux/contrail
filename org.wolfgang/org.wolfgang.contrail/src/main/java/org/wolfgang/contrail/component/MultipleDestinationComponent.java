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
 * The <code>MultipleUpStreamDestinationComponent</code> is capable to manage
 * multiple sources producing the same kind of event and sending each message to
 * a given source. In addition the data can be transformed from a type S to a
 * type D or vice-versa depending if components communicate using upstream or
 * downstream network.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface MultipleDestinationComponent<U, D> extends SourceComponent<U, D>, DestinationComponent<U, D> {

	// No specific behaviors

}
