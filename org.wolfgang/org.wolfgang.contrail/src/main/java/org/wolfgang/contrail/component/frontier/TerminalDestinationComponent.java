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

import org.wolfgang.contrail.component.ComponentConnectedException;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;

/**
 * The <code>TerminalUpStreamDestinationComponent</code> is capable to receive
 * incoming events.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TerminalDestinationComponent<E> extends AbstractComponent implements DestinationComponent<E> {

	/**
	 * Related down stream data handler after connection. Null otherwise
	 */
	private SourceComponent<E> upStreamSourceComponent;

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
	public TerminalDestinationComponent(final TerminalDataReceiverFactory<E> dataFactory) {
		super();

		this.dataEmitter = new DataSender<E>() {
			@Override
			public void sendData(E data) throws DataHandlerException {
				try {
					getDowntreamDataHandler().handleData(data);
				} catch (ComponentNotConnectedException e) {
					throw new DataHandlerException(e);
				}
			}
		};

		this.upstreamDataHandler = new UpStreamDataReceiverConnector<E>(dataFactory.create(this));
	}

	/**
	 * Provides the data channel used for up stream communication facility
	 * 
	 * @return an UpStreamDataHandler (never <code>null</code>)
	 * @throws ComponentNotConnectedException
	 *             thrown if the handler is not yet available
	 */
	protected DownStreamDataHandler<E> getDowntreamDataHandler() throws ComponentNotConnectedException {
		if (this.upStreamSourceComponent == null) {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		} else {
			return upStreamSourceComponent.getDownStreamDataHandler();
		}
	}

	@Override
	public void connect(SourceComponent<E> handler) throws ComponentConnectedException {
		if (this.upStreamSourceComponent == null) {
			this.upStreamSourceComponent = handler;
		} else {
			throw new ComponentConnectedException(ALREADY_CONNECTED.format());
		}
	}

	@Override
	public void disconnect(SourceComponent<E> handler) throws ComponentNotConnectedException {
		if (this.upStreamSourceComponent != null
				&& this.upStreamSourceComponent.getComponentId().equals(handler.getComponentId())) {
			this.upStreamSourceComponent = null;
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
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
	public void closeUpStream() throws DataHandlerCloseException {
		this.upstreamDataHandler.handleClose();
	}

	@Override
	public void closeDownStream() throws DataHandlerCloseException {
		this.upStreamSourceComponent.closeDownStream();
	}
}
