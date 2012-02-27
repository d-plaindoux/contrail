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

import org.wolfgang.contrail.environment.EnvironmentHandler;
import org.wolfgang.contrail.exception.ComponentAlreadyConnected;
import org.wolfgang.contrail.exception.ComponentNotYetConnected;
import org.wolfgang.contrail.handler.DownStreamDataHandler;

/**
 * The <code>UpStreamSourceComponent</code> is capable to send event in the
 * framework. Sending events consists in propagating events to upper components
 * via the upstream network. In parallel a source is capable to receive messages
 * sent by destination using the interconnected destination handler based on the
 * downstream network.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface UpStreamSourceComponent<E> extends EnvironmentHandler {

	/**
	 * Provides the data channel used for down stream communication facility
	 * 
	 * @return a DownStreamDataChannel (never <code>null</code>)
	 */
	DownStreamDataHandler<E> getDownStreamDataHandler();

	/**
	 * Method called whether a {@link UpStreamDestinationComponent} shall be
	 * connected to the current component.
	 * 
	 * @param handler
	 *            The {@link Destination} component
	 */
	void connect(UpStreamDestinationComponent<E> handler) throws ComponentAlreadyConnected;

	/**
	 * Method called whether a {@link UpStreamDestinationComponent} shall be
	 * disconnected from the current component.
	 * 
	 * @param handler
	 *            The {@link Destination} component
	 */
	void disconnect(UpStreamDestinationComponent<E> handler) throws ComponentNotYetConnected;

}
