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
import org.wolfgang.contrail.component.AbstractComponent;
import org.wolfgang.contrail.component.ComponentAlreadyConnectedException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotYetConnectedException;
import org.wolfgang.contrail.component.UpStreamDestinationComponent;
import org.wolfgang.contrail.component.UpStreamSourceComponent;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandlerClosedException;

/**
 * The <code>TerminalUpStreamDestinationComponent</code> is capable to receive
 * incoming events.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TerminalUpStreamDestinationComponent<E> extends AbstractComponent implements UpStreamDestinationComponent<E> {

	/**
	 * Related down stream data handler after connection. Null otherwise
	 */
	private UpStreamSourceComponent<E> upStreamSourceComponent;

	/**
	 * The data injection mechanism
	 */
	private final DataSender<E> dataEmitter;

	/**
	 * The internal down stream data handler
	 */
	private final UpStreamDataHandler<E> upstreamDataHandler;

	/**
	 * Data receiver
	 */

	/**
	 * Constructor
	 * 
	 * @param dataFactory
	 *            The terminal data receiver factory
	 */
	public TerminalUpStreamDestinationComponent(final TerminalDataReceiverFactory<E> dataFactory) {
		super(new ComponentId());

		this.dataEmitter = new DataSender<E>() {
			@Override
			public void sendData(E data) throws DataHandlerException {
				try {
					getDowntreamDataHandler().handleData(data);
				} catch (ComponentNotYetConnectedException e) {
					throw new DataHandlerException(e);
				}
			}
		};

		final DataReceiver<E> receiver = dataFactory.create(this);

		this.upstreamDataHandler = new UpStreamDataHandler<E>() {
			private volatile boolean closed = false;

			@Override
			public void handleData(E data) throws DataHandlerException {
				if (closed) {
					throw new UpStreamDataHandlerClosedException();
				} else {
					receiver.receiveData(data);
				}
			}

			@Override
			public void handleClose() {
				this.closed = true;
			}

			@Override
			public void handleLost() {
				this.closed = true;
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
	protected DownStreamDataHandler<E> getDowntreamDataHandler() throws ComponentNotYetConnectedException {
		if (this.upStreamSourceComponent == null) {
			final Message message = MessagesProvider.get("org.wolfgang.contrail.message", "not.yet.connected");
			throw new ComponentNotYetConnectedException(message.format());
		} else {
			return upStreamSourceComponent.getDownStreamDataHandler();
		}
	}

	@Override
	public void connect(UpStreamSourceComponent<E> handler) throws ComponentAlreadyConnectedException {
		if (this.upStreamSourceComponent == null) {
			this.upStreamSourceComponent = handler;
		} else {
			final Message message = MessagesProvider.get("org.wolfgang.contrail.message", "already.connected");
			throw new ComponentAlreadyConnectedException(message.format());
		}
	}

	@Override
	public void disconnect(UpStreamSourceComponent<E> handler) throws ComponentNotYetConnectedException {
		if (this.upStreamSourceComponent != null) {
			this.upStreamSourceComponent = null;
		} else {
			final Message message = MessagesProvider.get("org.wolfgang.contrail.message", "not.yet.connected");
			throw new ComponentNotYetConnectedException(message.format());
		}
	}

	@Override
	public UpStreamDataHandler<E> getUpStreamDataHandler() {
		return this.upstreamDataHandler;
	}

	/**
	 * Method called whether the data injection mechanism is required
	 * 
	 * @return the data injection mechanism
	 * @throws DataHandlerException
	 *             thrown is the data can not be handled correctly
	 */
	public DataSender<E> getDataSender() {
		return this.dataEmitter;
	}

	@Override
	public void closeUpStream() {
		this.upstreamDataHandler.handleClose();
	}

	@Override
	public void closeDownStream() {
		this.upStreamSourceComponent.closeDownStream();
	}
}
