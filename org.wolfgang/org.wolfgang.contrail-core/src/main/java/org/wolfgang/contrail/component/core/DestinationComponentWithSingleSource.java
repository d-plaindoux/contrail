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

import org.wolfgang.contrail.component.ComponentConnectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.flow.DataFlow;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.link.ComponentLinkFactory;
import org.wolfgang.contrail.link.DisposableLink;
import org.wolfgang.contrail.link.SourceComponentLink;

/**
 * <code>AbstractDestinationComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public abstract class DestinationComponentWithSingleSource<U, D> extends AbstractComponent implements DestinationComponent<U, D> {

	/**
	 * Related up stream data handler after connection. Null otherwise
	 */
	private SourceComponentLink<U, D> sourceComponentLink;

	{
		this.sourceComponentLink = ComponentLinkFactory.unboundSourceComponentLink();
	}

	/**
	 * Constructor
	 * 
	 * @param receiver
	 *            The initial data receiver
	 */
	public DestinationComponentWithSingleSource() {
		super();
	}

	/**
	 * Provides the data channel used for up stream communication facility
	 * 
	 * @return an UpStreamDataHandler (never <code>null</code>)
	 * @throws ComponentNotConnectedException
	 *             thrown if the handler is not yet available
	 */
	public DataFlow<D> getDownStreamDataFlow() throws ComponentNotConnectedException {
		if (ComponentLinkFactory.isUndefined(this.sourceComponentLink)) {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		} else {
			return sourceComponentLink.getSourceComponent().getDownStreamDataFlow();
		}
	}

	@Override
	public boolean acceptSource(ComponentId componentId) {
		return ComponentLinkFactory.isUndefined(this.sourceComponentLink);
	}

	@Override
	public DisposableLink connectSource(SourceComponentLink<U, D> handler) throws ComponentConnectedException {
		final ComponentId componentId = handler.getSourceComponent().getComponentId();
		if (acceptSource(componentId)) {
			this.sourceComponentLink = handler;
			return new DisposableLink() {
				@Override
				public void dispose() throws ComponentDisconnectionRejectedException {
					disconnectSource(componentId);
				}
			};
		} else {
			throw new ComponentConnectedException(ALREADY_CONNECTED.format());
		}
	}

	private void disconnectSource(ComponentId componentId) throws ComponentNotConnectedException {
		if (!acceptSource(componentId) && this.sourceComponentLink.getSourceComponent().getComponentId().equals(componentId)) {
			this.sourceComponentLink = ComponentLinkFactory.unboundSourceComponentLink();
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	@Override
	public void closeDownStream() throws DataFlowCloseException {
		if (ComponentLinkFactory.isUndefined(this.sourceComponentLink)) {
			throw new DataFlowCloseException(NOT_YET_CONNECTED.format());
		} else {
			this.sourceComponentLink.getSourceComponent().closeDownStream();
		}
	}

	/**
	 * Return the value of sourceComponentLink
	 * 
	 * @return the sourceComponentLink
	 */
	public SourceComponentLink<U, D> getSourceComponentLink() {
		return sourceComponentLink;
	}	
}
