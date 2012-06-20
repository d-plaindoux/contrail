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

import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolfgang.contrail.link.SourceComponentLink;

/**
 * The <code>DestinationComponent</code> is capable to receive event from the
 * framework. Receiving events consists in managing events from lower components
 * via the upstream network. In parallel a destination is capable to respond
 * sending messages to the interconnected source handler using the downstream
 * network.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface DestinationComponent<U, D> extends Component {

	/**
	 * Method called when the corresponding upstream data handler shall be
	 * retrieved.
	 * 
	 * @return an upstream data handler
	 */
	UpStreamDataHandler<U> getUpStreamDataHandler();

	/**
	 * Method called whether a connection must be performed
	 * 
	 * @param componentId
	 *            The component identifier
	 * @return true if the source can be connected; false otherwise
	 */
	boolean acceptSource(ComponentId componentId);

	/**
	 * Method called when the parametric upstream source component shall be
	 * connected to the current component.
	 * 
	 * @param handler
	 *            The source component
	 * @throws ComponentConnectionRejectedException
	 *             if the connection cannot be performed
	 */
	void connectSource(SourceComponentLink<U, D> handler) throws ComponentConnectionRejectedException;

	/**
	 * Method called when the connected upstream source component shall be
	 * disconnected from the current component.
	 * 
	 * @param handler
	 *            The source component
	 * @throws ComponentConnectionRejectedException
	 *             if the disconnection cannot be performed
	 */
	void disconnectSource(ComponentId componentId) throws ComponentDisconnectionRejectedException;
}
