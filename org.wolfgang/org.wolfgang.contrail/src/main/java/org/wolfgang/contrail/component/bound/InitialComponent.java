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

package org.wolfgang.contrail.component.bound;

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
 * The <code>InitialComponent</code> is capable to send event in the framework.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class InitialComponent<U, D> extends AbstractComponent implements SourceComponent<U, D> {

	/**
	 * Related up stream data handler after connection. Null otherwise
	 */
	private DestinationComponent<U, D> upStreamDestinationComponent;

	/**
	 * The data injection mechanism
	 */
	private final DataSender<U> dataEmitter;

	/**
	 * The internal down stream data handler
	 */
	private final DownStreamDataHandler<D> downStreamDataHandler;

	/**
	 * Constructor
	 * 
	 * @param dataFactory
	 *            The initial data receiver factory
	 */
	public InitialComponent(final SourceDataReceiverFactory<U, D> dataFactory) {
		super();

		this.dataEmitter = new DataSender<U>() {
			@Override
			public void sendData(U data) throws DataHandlerException {
				try {
					getUpStreamDataHandler().handleData(data);
				} catch (ComponentNotConnectedException e) {
					throw new DataHandlerException(e);
				}
			}
		};

 		this.downStreamDataHandler = new DownStreamDataReceiverHandler<D>(dataFactory.create(this));
		this.upStreamDestinationComponent = null;
	}

	/**
	 * Provides the data channel used for up stream communication facility
	 * 
	 * @return an UpStreamDataHandler (never <code>null</code>)
	 * @throws ComponentNotConnectedException
	 *             thrown if the handler is not yet available
	 */
	private UpStreamDataHandler<U> getUpStreamDataHandler() throws ComponentNotConnectedException {
		if (this.upStreamDestinationComponent == null) {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		} else {
			return upStreamDestinationComponent.getUpStreamDataHandler();
		}
	}

	@Override
	public void connect(DestinationComponent<U, D> handler) throws ComponentConnectedException {
		if (this.upStreamDestinationComponent == null) {
			this.upStreamDestinationComponent = handler;
		} else {
			throw new ComponentConnectedException(ALREADY_CONNECTED.format());
		}
	}

	@Override
	public void disconnect(DestinationComponent<U, D> handler) throws ComponentNotConnectedException {
		if (this.upStreamDestinationComponent != null
				&& this.upStreamDestinationComponent.getComponentId().equals(handler.getComponentId())) {
			this.upStreamDestinationComponent = null;
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	@Override
	public DownStreamDataHandler<D> getDownStreamDataHandler() {
		return this.downStreamDataHandler;
	}

	/**
	 * Method called whether the data injection mechanism is required
	 * 
	 * @return the data injection mechanism
	 * @throws DataHandlerException
	 *             thrown is the data can not be handled correctly
	 */
	public DataSender<U> getDataSender() {
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
