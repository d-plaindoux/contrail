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

package org.wolfgang.contrail.component.transducer;

import static org.wolfgang.common.message.MessagesProvider.message;

import org.wolfgang.common.message.Message;
import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.contrail.component.ComponentConnectedException;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.IntermediateComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;

/**
 * <code>TransducerComponent</code> is an implementation which requires data
 * transformations performed each time an upstream or downstream data go through
 * the connection component.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class TransducerComponent<U1, D1, U2, D2> extends AbstractComponent implements
		IntermediateComponent<U1, D1, U2, D2> {

	/**
	 * Static message definition for unknown transformation
	 */
	static final Message XDUCER_UNKNOWN;

	/**
	 * Static message definition for transformation error
	 */
	static final Message XDUCER_ERROR;

	static {
		final String category = "org.wolfgang.contrail.message";

		XDUCER_UNKNOWN = message(category, "transducer.upstream.unknown");
		XDUCER_ERROR = message(category, "transducer.transformation.error");
	}

	/**
	 * Internal upstream handler performing a data transformation for S to D
	 */
	private final UpStreamDataHandler<U1> upStreamDataHandler;

	/**
	 * Internal upstream handler performing a data transformation for D to S
	 */
	private final DownStreamDataHandler<D2> downStreamDataHandler;

	/**
	 * Related down stream data handler after connection. Null otherwise
	 */
	private SourceComponent<U1, D1> upStreamSourceComponent;

	/**
	 * Related up stream data handler after connection. Null otherwise
	 */
	private DestinationComponent<U2, D2> upStreamDestinationComponent;

	/**
	 * Constructor
	 * 
	 * @param upstreamXducer
	 *            The data transformation used for incoming data (upstream)
	 * @param streamXducer
	 *            The data transformation used for outgoing data (downstream)
	 */
	public TransducerComponent(DataTransducer<U1, U2> upstreamXducer, DataTransducer<D2, D1> downstreamXducer) {
		super();
		this.upStreamDataHandler = new TransducerUpStreamDataHandler<U1, U2>(this, upstreamXducer);
		this.downStreamDataHandler = new TransducerDownStreamDataHandler<D2, D1>(this, downstreamXducer);
		this.upStreamSourceComponent = null;
		this.upStreamDestinationComponent = null;
	}

	/**
	 * Provides the embedded upstream source component (internal use only)
	 * 
	 * @return the current up stream source component
	 */
	SourceComponent<U1, D1> getUpStreamSourceComponent() {
		return this.upStreamSourceComponent;
	}

	/**
	 * Provides the embedded upstream source component (internal use only)
	 * 
	 * @return the current up stream source component
	 */
	DestinationComponent<U2, D2> getUpStreamDestinationComponent() {
		return this.upStreamDestinationComponent;
	}

	@Override
	public void connect(SourceComponent<U1, D1> handler) throws ComponentConnectedException {
		if (this.upStreamSourceComponent == null) {
			this.upStreamSourceComponent = handler;
		} else {
			throw new ComponentConnectedException(ALREADY_CONNECTED.format());
		}
	}

	@Override
	public void disconnect(SourceComponent<U1, D1> handler) throws ComponentNotConnectedException {
		if (this.upStreamSourceComponent != null
				&& this.upStreamSourceComponent.getComponentId().equals(handler.getComponentId())) {
			this.upStreamSourceComponent = null;
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	@Override
	public void connect(DestinationComponent<U2, D2> handler) throws ComponentConnectedException {
		if (this.upStreamDestinationComponent == null) {
			this.upStreamDestinationComponent = handler;
		} else {
			throw new ComponentConnectedException(ALREADY_CONNECTED.format());
		}
	}

	@Override
	public void disconnect(DestinationComponent<U2, D2> handler) throws ComponentNotConnectedException {
		if (this.upStreamDestinationComponent != null
				&& this.upStreamDestinationComponent.getComponentId().equals(handler.getComponentId())) {
			this.upStreamDestinationComponent = null;
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	@Override
	public UpStreamDataHandler<U1> getUpStreamDataHandler() {
		return upStreamDataHandler;
	}

	@Override
	public DownStreamDataHandler<D2> getDownStreamDataHandler() {
		return downStreamDataHandler;
	}

	@Override
	public void closeUpStream() throws DataHandlerCloseException {
		try {
			this.upStreamDataHandler.handleClose();
		} finally {
			this.upStreamDestinationComponent.closeUpStream();
		}
	}

	@Override
	public void closeDownStream() throws DataHandlerCloseException {
		try {
			this.downStreamDataHandler.handleClose();
		} finally {
			this.upStreamSourceComponent.closeDownStream();
		}
	}
}
