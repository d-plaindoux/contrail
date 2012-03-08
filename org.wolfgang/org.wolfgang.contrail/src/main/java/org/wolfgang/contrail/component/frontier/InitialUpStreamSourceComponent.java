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

package org.wolfgang.contrail.component.frontier;

import org.wolfgang.common.message.Message;
import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.contrail.component.ComponentConnectedException;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.UpStreamDestinationComponent;
import org.wolfgang.contrail.component.UpStreamSourceComponent;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.DownStreamDataHandlerClosedException;
import org.wolfgang.contrail.handler.UpStreamDataHandler;

/**
 * The <code>InitialUpStreamSourceComponent</code> is capable to send event in
 * the framework.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class InitialUpStreamSourceComponent<E> extends AbstractComponent implements UpStreamSourceComponent<E> {

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
	 * The source data type
	 */
	private final Class<E> type;

	/**
	 * Constructor
	 * 
	 * @param dataFactory
	 *            The initial data receiver factory
	 */
	public InitialUpStreamSourceComponent(final Class<E> type, final InitialDataReceiverFactory<E> dataFactory) {
		super();

		this.type = type;

		this.dataEmitter = new DataSender<E>() {
			@Override
			public void sendData(E data) throws DataHandlerException {
				try {
					getUpStreamDataHandler().handleData(data);
				} catch (ComponentNotConnectedException e) {
					throw new DataHandlerException(e);
				}
			}
		};

		final DataReceiver<E> receiver = dataFactory.create(this);

		this.downStreamDataHandler = new DownStreamDataHandler<E>() {
			private volatile boolean closed = false;

			@Override
			public void handleData(E data) throws DataHandlerException {
				if (closed) {
					throw new DownStreamDataHandlerClosedException();
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

	@Override
	public Class<E> getUpStreamSourceType() {
		return this.type;
	}

	/**
	 * Provides the data channel used for up stream communication facility
	 * 
	 * @return an UpStreamDataHandler (never <code>null</code>)
	 * @throws ComponentNotConnectedException
	 *             thrown if the handler is not yet available
	 */
	private UpStreamDataHandler<E> getUpStreamDataHandler() throws ComponentNotConnectedException {
		if (this.upStreamDestinationComponent == null) {
			final Message message = MessagesProvider.get("org.wolfgang.contrail.message", "not.yet.connected");
			throw new ComponentNotConnectedException(message.format());
		} else {
			return upStreamDestinationComponent.getUpStreamDataHandler();
		}
	}

	@Override
	public void connect(UpStreamDestinationComponent<E> handler) throws ComponentConnectedException {
		if (this.upStreamDestinationComponent == null) {
			this.upStreamDestinationComponent = handler;
		} else {
			final Message message = MessagesProvider.get("org.wolfgang.contrail.message", "already.connected");
			throw new ComponentConnectedException(message.format());
		}
	}

	@Override
	public void disconnect(UpStreamDestinationComponent<E> handler) throws ComponentNotConnectedException {
		if (this.upStreamDestinationComponent != null
				&& this.upStreamDestinationComponent.getComponentId().equals(handler.getComponentId())) {
			this.upStreamDestinationComponent = null;
		} else {
			final Message message = MessagesProvider.get("org.wolfgang.contrail.message", "not.yet.connected");
			throw new ComponentNotConnectedException(message.format());
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
	 * @throws DataHandlerException
	 *             thrown is the data can not be handled correctly
	 */
	public DataSender<E> getDataSender() {
		return this.dataEmitter;
	}

	@Override
	public void closeUpStream() throws DataHandlerCloseException {
		this.upStreamDestinationComponent.closeUpStream();
	}

	@Override
	public void closeDownStream() throws DataHandlerCloseException {
		this.downStreamDataHandler.handleClose();
	}
}
