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

package org.wolfgang.contrail.component.core;

import org.wolfgang.common.message.Message;
import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.contrail.component.ComponentAlreadyConnectedException;
import org.wolfgang.contrail.component.ComponentNotYetConnectedException;
import org.wolfgang.contrail.component.UpStreamDestinationComponent;
import org.wolfgang.contrail.component.UpStreamSourceComponent;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.HandleDataException;
import org.wolfgang.contrail.handler.UpStreamDataHandler;

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
public class InitialUpStreamSourceComponent<E> implements UpStreamSourceComponent<E> {

	/**
	 * Related up stream data handler after connection. Null otherwise
	 */
	private UpStreamDestinationComponent<E> upStreamDestinationComponent;

	/**
	 * The data injection mechanism
	 */
	private final DataSender<E> dataEmitter;

	/**
	 * The internal down stream data handler
	 */
	private final DownStreamDataHandler<E> downStreamDataHandler;

	/**
	 * Constructor
	 * 
	 * @param downStreamDataHandler
	 */
	protected InitialUpStreamSourceComponent(final DataReceiverFactory<E,InitialUpStreamSourceComponent<E>> dataFactory) {
		this.dataEmitter = new DataSender<E>() {
			@Override
			public void sendData(E data) throws HandleDataException {
				try {
					getUpStreamDataHandler().handleData(data);
				} catch (ComponentNotYetConnectedException e) {
					throw new HandleDataException(e);
				}
			}
		};

		final DataReceiver<E> receiver = dataFactory.create(this);
		
		this.downStreamDataHandler = new DownStreamDataHandler<E>() {
			@Override
			public void handleData(E data) throws HandleDataException {
				receiver.receiveData(data);
			}

			@Override
			public void handleClose() {
				// Nothing
			}

			@Override
			public void handleLost() {
				// Nothing
			}
		};
	}

	/**
	 * Provides the data channel used for up stream communication facility
	 * 
	 * @return an UpStreamDataHandler (never <code>null</code>)
	 * @throws ComponentNotYetConnectedException
	 *             thrown if the handler is not yet available
	 */
	private UpStreamDataHandler<E> getUpStreamDataHandler() throws ComponentNotYetConnectedException {
		if (this.upStreamDestinationComponent == null) {
			final Message message = MessagesProvider.get("org.wolfgang.contrail.message", "not.yet.connected");
			throw new ComponentNotYetConnectedException(message.format());
		} else {
			return upStreamDestinationComponent.getUpStreamDataHandler();
		}
	}

	@Override
	public void connect(UpStreamDestinationComponent<E> handler) throws ComponentAlreadyConnectedException {
		if (this.upStreamDestinationComponent == null) {
			this.upStreamDestinationComponent = handler;
		} else {
			final Message message = MessagesProvider.get("org.wolfgang.contrail.message", "already.connected");
			throw new ComponentAlreadyConnectedException(message.format());
		}
	}

	@Override
	public void disconnect(UpStreamDestinationComponent<E> handler) throws ComponentNotYetConnectedException {
		if (this.upStreamDestinationComponent != null) {
			this.upStreamDestinationComponent = null;
		} else {
			final Message message = MessagesProvider.get("org.wolfgang.contrail.message", "not.yet.connected");
			throw new ComponentNotYetConnectedException(message.format());
		}
	}

	@Override
	public DownStreamDataHandler<E> getDownStreamDataHandler() {
		return this.downStreamDataHandler;
	}

	/**
	 * Method called whether the data injection mechanism is required
	 * 
	 * @return the data injection mechanism
	 * @throws HandleDataException
	 *             thrown is the data can not be handled correctly
	 */
	public DataSender<E> getDataEmitter() {
		return this.dataEmitter;
	}
}
