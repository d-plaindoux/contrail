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

import org.wolfgang.contrail.flow.DataFlow;
import org.wolfgang.contrail.link.DestinationComponentLink;
import org.wolfgang.contrail.link.DisposableLink;

/**
 * The <code>SourceComponent</code> is capable to send event in the framework.
 * Sending events consists in propagating events to upper components via the
 * upstream network. In parallel a source is capable to receive messages sent by
 * destination using the interconnected destination handler based on the
 * downstream data handler.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface SourceComponent<U, D> extends Component {

	/**
	 * Provides the data channel used for down stream communication facility
	 * 
	 * @return a down stream data channel (never <code>null</code>)
	 */
	DataFlow<D> getDownStreamDataFlow() throws ComponentNotConnectedException;

	/**
	 * Method called whether a connection must be performed
	 * 
	 * @param componentId
	 * @return true if the destination can be connected; false otherwise
	 */
	boolean acceptDestination(ComponentId componentId);

	/**
	 * Method called when the parametric destination component shall be
	 * connected to the current component.
	 * 
	 * @param handler
	 *            The destination component
	 * @return a component link reflecting the connection
	 * @throws ComponentConnectionRejectedException
	 *             is the connection cannot be performed
	 */
	DisposableLink connectDestination(DestinationComponentLink<U, D> handler) throws ComponentConnectionRejectedException;

}
