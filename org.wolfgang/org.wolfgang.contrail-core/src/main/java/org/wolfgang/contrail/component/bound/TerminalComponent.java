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

import org.wolfgang.contrail.component.ComponentConnectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolfgang.contrail.link.ComponentLink;
import org.wolfgang.contrail.link.ComponentLinkFactory;
import org.wolfgang.contrail.link.SourceComponentLink;

/**
 * The <code>TerminalComponent</code> is capable to receive incoming events.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TerminalComponent<U, D> extends AbstractComponent implements DestinationComponent<U, D> {

	/**
	 * Related down stream data handler after connection. Null otherwise
	 */
	private SourceComponentLink<U, D> upStreamSourceComponentLink;

	/**
	 * The data injection mechanism
	 */
	private final DataSender<D> dataEmitter;

	/**
	 * The internal down stream data handler
	 */
	private final UpStreamDataHandler<U> upstreamDataHandler;

	{
		this.upStreamSourceComponentLink = ComponentLinkFactory.undefSourceComponentLink();
	}

	/**
	 * Provides the local data sender
	 * 
	 * @return a data sender (never <code>null</code>)
	 */
	private DataSender<D> getLocalDataSender() {
		return new DataSender<D>() {
			@Override
			public void sendData(D data) throws DataHandlerException {
				try {
					getDowntreamDataHandler().handleData(data);
				} catch (ComponentNotConnectedException e) {
					throw new DataHandlerException(e);
				}
			}

			@Override
			public void close() throws IOException {
				try {
					getDowntreamDataHandler().handleClose();
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
	 *            The terminal data receiver
	 */
	public TerminalComponent(final DataReceiver<U> receiver) {
		super();

		this.dataEmitter = this.getLocalDataSender();
		this.upstreamDataHandler = new UpStreamDataReceiverHandler<U>(receiver);
	}

	/**
	 * Constructor
	 * 
	 * @param dataFactory
	 *            The terminal data receiver factory
	 */
	public TerminalComponent(final DataReceiverFactory<U, D> dataFactory) {
		super();

		this.dataEmitter = this.getLocalDataSender();
		this.upstreamDataHandler = new UpStreamDataReceiverHandler<U>(dataFactory.create(this.dataEmitter));
	}

	/**
	 * Provides the data channel used for up stream communication facility
	 * 
	 * @return an UpStreamDataHandler (never <code>null</code>)
	 * @throws ComponentNotConnectedException
	 *             thrown if the handler is not yet available
	 */
	protected DownStreamDataHandler<D> getDowntreamDataHandler() throws ComponentNotConnectedException {
		if (this.upStreamSourceComponentLink.getSourceComponent() == null) {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		} else {
			return upStreamSourceComponentLink.getSourceComponent().getDownStreamDataHandler();
		}
	}

	@Override
	public boolean acceptSource(ComponentId componentId) {
		return this.upStreamSourceComponentLink.getSourceComponent() == null;
	}

	@Override
	public ComponentLink connectSource(SourceComponentLink<U, D> handler) throws ComponentConnectedException {
		final ComponentId componentId = handler.getSourceComponent().getComponentId();
		if (acceptSource(componentId)) {
			this.upStreamSourceComponentLink = handler;
			return new ComponentLink() {
				@Override
				public void dispose() throws ComponentDisconnectionRejectedException {
					disconnectSource(componentId);
				}
			};
		} else {
			throw new ComponentConnectedException(ALREADY_CONNECTED.format());
		}
	}

	private void disconnectSource(ComponentId componentId) throws ComponentDisconnectionRejectedException {
		final SourceComponent<U, D> sourceComponent = this.upStreamSourceComponentLink.getSourceComponent();
		if (sourceComponent != null && sourceComponent.getComponentId().equals(componentId)) {
			this.upStreamSourceComponentLink = ComponentLinkFactory.undefSourceComponentLink();
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	@Override
	public UpStreamDataHandler<U> getUpStreamDataHandler() {
		return this.upstreamDataHandler;
	}

	/**
	 * Method called whether the data injection mechanism is required
	 * 
	 * @return the data injection mechanism
	 * @throws DataHandlerException
	 *             thrown is the data can not be handled correctly
	 */
	public DataSender<D> getDataSender() {
		return this.dataEmitter;
	}

	@Override
	public void closeUpStream() throws DataHandlerCloseException {
		this.upstreamDataHandler.handleClose();
	}

	@Override
	public void closeDownStream() throws DataHandlerCloseException {
		if (this.upStreamSourceComponentLink.getSourceComponent() == null) {
			throw new DataHandlerCloseException(NOT_YET_CONNECTED.format());
		} else {
			this.upStreamSourceComponentLink.getSourceComponent().closeDownStream();
		}
	}
}
