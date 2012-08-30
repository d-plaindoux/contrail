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
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
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
	private SourceComponentLink<U, D> sourceComponentLink;

	/**
	 * The internal down stream data handler
	 */
	private final UpStreamDataHandler<U> upstreamDataHandler;

	{
		this.sourceComponentLink = ComponentLinkFactory.undefSourceComponentLink();
	}

	/**
	 * Constructor
	 * 
	 * @param receiver
	 *            The terminal data receiver
	 */
	public TerminalComponent(final UpStreamDataHandler<U> receiver) {
		super();

		this.upstreamDataHandler = receiver;
	}

	/**
	 * Constructor
	 * 
	 * @param receiver
	 *            The terminal data receiver
	 * @throws CannotCreateDataHandlerException
	 */
	public TerminalComponent(final UpStreamDataHandlerFactory<U, D> receiver) throws CannotCreateDataHandlerException {
		super();

		this.upstreamDataHandler = receiver.create(new TerminalDownStreamDataHandler<D>(this));
	}

	/**
	 * Provides the data channel used for up stream communication facility
	 * 
	 * @return an UpStreamDataHandler (never <code>null</code>)
	 * @throws ComponentNotConnectedException
	 *             thrown if the handler is not yet available
	 */
	public DownStreamDataHandler<D> getDownStreamDataHandler() throws ComponentNotConnectedException {
		if (ComponentLinkFactory.isUndefined(this.sourceComponentLink)) {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		} else {
			return sourceComponentLink.getSource().getDownStreamDataHandler();
		}
	}

	@Override
	public boolean acceptSource(ComponentId componentId) {
		return ComponentLinkFactory.isUndefined(this.sourceComponentLink);
	}

	@Override
	public ComponentLink connectSource(SourceComponentLink<U, D> handler) throws ComponentConnectedException {
		final ComponentId componentId = handler.getSource().getComponentId();
		if (acceptSource(componentId)) {
			this.sourceComponentLink = handler;
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
		if (!acceptSource(componentId) && this.sourceComponentLink.getSource().getComponentId().equals(componentId)) {
			this.sourceComponentLink = ComponentLinkFactory.undefSourceComponentLink();
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	@Override
	public UpStreamDataHandler<U> getUpStreamDataHandler() {
		return this.upstreamDataHandler;
	}

	@Override
	public void closeUpStream() throws DataHandlerCloseException {
		this.upstreamDataHandler.handleClose();
	}

	@Override
	public void closeDownStream() throws DataHandlerCloseException {
		if (this.sourceComponentLink.getSource() == null) {
			throw new DataHandlerCloseException(NOT_YET_CONNECTED.format());
		} else {
			this.sourceComponentLink.getSource().closeDownStream();
		}
	}
}
