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
import org.wolfgang.contrail.component.ComponentConnectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.IntermediateComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolfgang.contrail.link.ComponentLinkFactory;
import org.wolfgang.contrail.link.DestinationComponentLink;
import org.wolfgang.contrail.link.SourceComponentLink;

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
	private SourceComponentLink<U1, D1> upStreamSourceComponentLink;

	/**
	 * Related up stream data handler after connection. Null otherwise
	 */
	private DestinationComponentLink<U2, D2> upStreamDestinationComponentLink;

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
		this.upStreamSourceComponentLink = ComponentLinkFactory.undefSourceComponentLink();
		this.upStreamDestinationComponentLink = ComponentLinkFactory.undefDestinationComponentLink();
	}

	/**
	 * Provides the embedded upstream source component (internal use only)
	 * 
	 * @return the current up stream source component
	 */
	SourceComponent<U1, D1> getUpStreamSourceComponent() {
		return this.upStreamSourceComponentLink.getSourceComponent();
	}

	/**
	 * Provides the embedded upstream source component (internal use only)
	 * 
	 * @return the current up stream source component
	 */
	DestinationComponent<U2, D2> getUpStreamDestinationComponent() {
		return this.upStreamDestinationComponentLink.getDestinationComponent();
	}

	@Override
	public boolean acceptSource(ComponentId componentId) {
		return this.upStreamSourceComponentLink.getSourceComponent() == null;
	}

	@Override
	public void connectSource(SourceComponentLink<U1, D1> handler) throws ComponentConnectedException {
		if (this.acceptSource(handler.getSourceComponent().getComponentId())) {
			this.upStreamSourceComponentLink = handler;
		} else {
			throw new ComponentConnectedException(ALREADY_CONNECTED.format());
		}
	}

	@Override
	public void disconnectSource(ComponentId componentId) throws ComponentDisconnectionRejectedException {
		final SourceComponent<U1, D1> sourceComponent = upStreamSourceComponentLink.getSourceComponent();
		if (sourceComponent != null && sourceComponent.getComponentId().equals(componentId)) {
			this.upStreamSourceComponentLink = ComponentLinkFactory.undefSourceComponentLink();
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	@Override
	public boolean acceptDestination(ComponentId componentId) {
		return this.upStreamDestinationComponentLink.getDestinationComponent() == null;
	}

	@Override
	public void connectDestination(DestinationComponentLink<U2, D2> handler) throws ComponentConnectedException {
		if (this.acceptDestination(handler.getDestinationComponent().getComponentId())) {
			this.upStreamDestinationComponentLink = handler;
		} else {
			throw new ComponentConnectedException(ALREADY_CONNECTED.format());
		}
	}

	@Override
	public void disconnectDestination(ComponentId componentId) throws ComponentNotConnectedException {
		final DestinationComponent<U2, D2> destinationComponent = upStreamDestinationComponentLink.getDestinationComponent();
		if (destinationComponent != null && destinationComponent.getComponentId().equals(componentId)) {
			this.upStreamDestinationComponentLink = ComponentLinkFactory.undefDestinationComponentLink();
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
			if (this.upStreamDestinationComponentLink.getDestinationComponent() != null) {
				this.upStreamDestinationComponentLink.getDestinationComponent().closeUpStream();
			}
		}
	}

	@Override
	public void closeDownStream() throws DataHandlerCloseException {
		try {
			this.downStreamDataHandler.handleClose();
		} finally {
			if (this.upStreamSourceComponentLink.getSourceComponent() != null) {
				this.upStreamSourceComponentLink.getSourceComponent().closeDownStream();
			}
		}
	}
}
