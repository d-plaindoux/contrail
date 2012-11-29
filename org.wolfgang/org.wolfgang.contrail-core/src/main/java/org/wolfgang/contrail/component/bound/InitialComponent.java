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
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.flow.CannotCreateDataFlowException;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.DownStreamDataFlowFactory;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.link.ComponentLinkFactory;
import org.wolfgang.contrail.link.DestinationComponentLink;
import org.wolfgang.contrail.link.DisposableLink;

/**
 * The <code>InitialComponent</code> is capable to send only event in the
 * framework.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class InitialComponent<U, D> extends AbstractComponent implements SourceComponent<U, D> {

	/**
	 * Related up stream data handler after connection. Null otherwise
	 */
	private DestinationComponentLink<U, D> destinationComponentLink;

	/**
	 * The internal down stream data handler
	 */
	private final DownStreamDataFlow<D> downStreamDataHandler;

	{
		this.destinationComponentLink = ComponentLinkFactory.unboundDestinationComponentLink();
	}

	/**
	 * Constructor
	 * 
	 * @param receiver
	 *            The initial data receiver
	 */
	public InitialComponent(final DownStreamDataFlow<D> receiver) {
		super();
		this.downStreamDataHandler = receiver;
	}

	/**
	 * Constructor
	 * 
	 * @param receiver
	 *            The initial data receiver
	 */
	public InitialComponent(final DownStreamDataFlowFactory<U, D> receiver) throws CannotCreateDataFlowException {
		super();
		this.downStreamDataHandler = receiver.create(InitialUpStreamDataFlow.<U> create(this));
	}

	/**
	 * Provides the data channel used for up stream communication facility
	 * 
	 * @return an UpStreamDataHandler (never <code>null</code>)
	 * @throws ComponentNotConnectedException
	 *             thrown if the handler is not yet available
	 */
	public UpStreamDataFlow<U> getUpStreamDataFlow() throws ComponentNotConnectedException {
		if (ComponentLinkFactory.isUndefined(this.destinationComponentLink)) {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		} else {
			return destinationComponentLink.getDestinationComponent().getUpStreamDataFlow();
		}
	}

	@Override
	public boolean acceptDestination(ComponentId componentId) {
		return ComponentLinkFactory.isUndefined(this.destinationComponentLink);
	}

	@Override
	public DisposableLink connectDestination(DestinationComponentLink<U, D> handler) throws ComponentConnectedException {
		final ComponentId componentId = handler.getDestinationComponent().getComponentId();
		if (acceptDestination(componentId)) {
			this.destinationComponentLink = handler;
			return new DisposableLink() {
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
		if (!acceptDestination(componentId) && this.destinationComponentLink.getDestinationComponent().getComponentId().equals(componentId)) {
			this.destinationComponentLink = ComponentLinkFactory.unboundDestinationComponentLink();
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	@Override
	public DownStreamDataFlow<D> getDownStreamDataFlow() {
		return this.downStreamDataHandler;
	}

	@Override
	public void closeUpStream() throws DataFlowCloseException {
		if (!ComponentLinkFactory.isUndefined(this.destinationComponentLink)) {
			this.destinationComponentLink.getDestinationComponent().closeUpStream();
		}
	}

	@Override
	public void closeDownStream() throws DataFlowCloseException {
		this.downStreamDataHandler.handleClose();
	}
}
