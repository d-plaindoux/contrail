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

package org.wolfgang.contrail.component.impl;

import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.contrail.component.UpStreamDestinationComponent;
import org.wolfgang.contrail.component.UpStreamSourceComponent;
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
public abstract class AbstractUpStreamDestinationComponent<E> implements UpStreamDestinationComponent<E> {

	/**
	 * Related down stream data handler after connection. Null otherwise
	 */
	private UpStreamSourceComponent<E> upStreamSourceComponent;

	/**
	 * Constructor
	 * 
	 * @param upStreamDataHandler
	 */
	protected AbstractUpStreamDestinationComponent() {
		// Nothing
	}

	/**
	 * Provides the data channel used for up stream communication facility
	 * 
	 * @return an UpStreamDataHandler (never <code>null</code>)
	 * @throws ComponentNotYetConnected
	 *             thrown if the handler is not yet available
	 */
	public DownStreamDataHandler<E> getDowntreamDataHandler() throws ComponentNotYetConnected {
		if (this.upStreamSourceComponent == null) {
			throw new ComponentNotYetConnected(MessagesProvider.get("org.wolfgang.contrail.message", "not.yet.connected").format());
		} else {
			return upStreamSourceComponent.getDownStreamDataHandler();
		}
	}

	@Override
	public void connect(UpStreamSourceComponent<E> handler) throws ComponentAlreadyConnected {
		if (this.upStreamSourceComponent == null) {
			this.upStreamSourceComponent = handler;
		} else {
			throw new ComponentAlreadyConnected(MessagesProvider.get("org.wolfgang.contrail.message", "already.connected").format());
		}
	}

	@Override
	public void disconnect(UpStreamSourceComponent<E> handler) throws ComponentNotYetConnected {
		if (this.upStreamSourceComponent != null) {
			this.upStreamSourceComponent = null;
		} else {
			throw new ComponentNotYetConnected(MessagesProvider.get("org.wolfgang.contrail.message", "not.yet.connected").format());
		}
	}

}
