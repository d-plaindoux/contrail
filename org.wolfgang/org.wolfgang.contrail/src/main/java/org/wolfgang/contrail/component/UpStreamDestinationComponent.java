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

import javax.print.attribute.standard.Destination;

import org.wolfgang.contrail.exception.ComponentAlreadyConnected;
import org.wolfgang.contrail.exception.ComponentNotYetConnected;
import org.wolfgang.contrail.handler.UpStreamDataHandler;

/**
 * The <code>UpStreamDestinationComponent</code> is capable to receive event
 * from the framework. Receiving events consists in managing events from lower
 * components via the upstream network. In parallel a destination is capable to
 * respond sending messages to the interconnected source handler using the
 * downstream network.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface UpStreamDestinationComponent<E> {

	/**
	 * Method called when the corresponding upstream data handler shall be
	 * retrieved.
	 * 
	 * @return an upstream data handler
	 */
	UpStreamDataHandler<E> getUpStreamDataHandler();

	/**
	 * Method called whether a {@link UpStreamSourceComponent} shall be
	 * connected to the current component.
	 * 
	 * @param handler
	 *            The {@link Destination} component
	 */
	void connect(UpStreamSourceComponent<E> handler) throws ComponentAlreadyConnected;

	/**
	 * Method called whether a {@link UpStreamSourceComponent} shall be
	 * disconnected from the current component.
	 * 
	 * @param handler
	 *            The {@link Destination} component
	 */
	void disconnect(UpStreamSourceComponent<E> handler) throws ComponentNotYetConnected;
}
