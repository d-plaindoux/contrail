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

import java.io.IOException;

import org.wolfgang.common.utils.Pair;
import org.wolfgang.contrail.component.ComponentConnectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolfgang.contrail.link.ComponentLink;
import org.wolfgang.contrail.link.ComponentLinkFactory;
import org.wolfgang.contrail.link.DestinationComponentLink;

/**
 * The <code>InitialComponent</code> is capable to send event in the framework.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class InitialComponent<U, D> extends AbstractComponent implements SourceComponent<U, D> {

	/**
	 * Types for initial up and down stream
	 */
	private final Pair<Class<U>, Class<D>> types;

	/**
	 * Related up stream data handler after connection. Null otherwise
	 */
	private DestinationComponentLink<U, D> destinationComponentLink;

	/**
	 * The data sender mechanism used by external components
	 */
	private final DataSender<U> dataSender;

	/**
	 * The internal down stream data handler
	 */
	private final DownStreamDataHandler<D> downStreamDataHandler;

	{
		this.destinationComponentLink = ComponentLinkFactory.undefDestinationComponentLink();
	}

	/**
	 * Provides the local data sender
	 * 
	 * @return a data sender (never <code>null</code>)
	 */
	private DataSender<U> getLocalDataSender() {
		return new DataSender<U>() {
			@Override
			public void sendData(U data) throws DataHandlerException {
				try {
					getUpStreamDataHandler().handleData(data);
				} catch (ComponentNotConnectedException e) {
					throw new DataHandlerException(e);
				}
			}

			@Override
			public void close() throws IOException {
				try {
					getUpStreamDataHandler().handleClose();
				} catch (ComponentNotConnectedException e) {
					throw new IOException(e);
				} catch (DataHandlerCloseException e) {
					throw new IOException(e);
				}
			}
		};
	}

	/**
	 * Constructor
	 * 
	 * @param receiver
	 *            The initial data receiver
	 */
	public InitialComponent(Pair<Class<U>, Class<D>> types, final DataReceiver<D> receiver) {
		super();

		this.types = types;
		this.dataSender = this.getLocalDataSender();
		this.downStreamDataHandler = new DownStreamDataReceiverHandler<D>(receiver);
	}

	/**
	 * Constructor
	 * 
	 * @param dataFactory
	 *            The initial data receiver factory
	 */
	public InitialComponent(Pair<Class<U>, Class<D>> types, final DataReceiverFactory<D, U> dataFactory) {
		super();

		this.types = types;
		this.dataSender = this.getLocalDataSender();
		this.downStreamDataHandler = new DownStreamDataReceiverHandler<D>(dataFactory.create(this.dataSender));
	}

	@Override
	public Pair<Class<U>, Class<D>> getUpStreamType() {
		return this.types;
	}

	/**
	 * Provides the data channel used for up stream communication facility
	 * 
	 * @return an UpStreamDataHandler (never <code>null</code>)
	 * @throws ComponentNotConnectedException
	 *             thrown if the handler is not yet available
	 */
	private UpStreamDataHandler<U> getUpStreamDataHandler() throws ComponentNotConnectedException {
		if (ComponentLinkFactory.isUndefined(this.destinationComponentLink)) {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		} else {
			return destinationComponentLink.getDestination().getUpStreamDataHandler();
		}
	}

	@Override
	public boolean acceptDestination(ComponentId componentId) {
		return ComponentLinkFactory.isUndefined(this.destinationComponentLink);
	}

	@Override
	public ComponentLink connectDestination(DestinationComponentLink<U, D> handler) throws ComponentConnectedException {
		final ComponentId componentId = handler.getDestination().getComponentId();
		if (acceptDestination(componentId)) {
			this.destinationComponentLink = handler;
			return new ComponentLink() {
				@Override
				public void dispose() throws ComponentDisconnectionRejectedException {
					disconnectDestination(componentId);
				}
			};
		} else {
			throw new ComponentConnectedException(ALREADY_CONNECTED.format());
		}
	}

	private void disconnectDestination(ComponentId componentId) throws ComponentNotConnectedException {
		if (!acceptDestination(componentId) && this.destinationComponentLink.getDestination().getComponentId().equals(componentId)) {
			this.destinationComponentLink = ComponentLinkFactory.undefDestinationComponentLink();
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
		return this.dataSender;
	}

	@Override
	public void closeUpStream() throws DataHandlerCloseException {
		if (ComponentLinkFactory.isUndefined(this.destinationComponentLink)) {
			throw new DataHandlerCloseException(NOT_YET_CONNECTED.format());
		} else {
			this.destinationComponentLink.getDestination().closeUpStream();
		}
	}

	@Override
	public void closeDownStream() throws DataHandlerCloseException {
		this.downStreamDataHandler.handleClose();
	}
}
