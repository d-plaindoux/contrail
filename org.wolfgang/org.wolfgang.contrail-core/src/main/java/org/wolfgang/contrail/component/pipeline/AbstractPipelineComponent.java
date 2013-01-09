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

package org.wolfgang.contrail.component.pipeline;

import org.wolfgang.contrail.component.ComponentConnectedException;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.PipelineComponent;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.flow.exception.DataFlowCloseException;
import org.wolfgang.contrail.link.ComponentLinkFactory;
import org.wolfgang.contrail.link.DestinationComponentLink;
import org.wolfgang.contrail.link.DisposableLink;
import org.wolfgang.contrail.link.SourceComponentLink;

/**
 * <code>TransducerComponent</code> is an implementation which requires data
 * transformations performed each time an upstream or downstream data go through
 * the connection component.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public abstract class AbstractPipelineComponent<U1, D1, U2, D2> extends AbstractComponent implements PipelineComponent<U1, D1, U2, D2> {
	/**
	 * Related down stream data handler after connection. Null otherwise
	 */
	private SourceComponentLink<U1, D1> sourceComponentLink;

	/**
	 * Related up stream data handler after connection. Null otherwise
	 */
	private DestinationComponentLink<U2, D2> destinationComponentLink;

	/**
	 * Constructor
	 * 
	 * @param upstreamXducer
	 *            The data transformation used for incoming data (upstream)
	 * @param streamXducer
	 *            The data transformation used for outgoing data (downstream)
	 */
	protected AbstractPipelineComponent() {
		super();
		this.sourceComponentLink = ComponentLinkFactory.unboundSourceComponentLink();
		this.destinationComponentLink = ComponentLinkFactory.unboundDestinationComponentLink();
	}

	/**
	 * Provides the embedded upstream source component (internal use only)
	 * 
	 * @return the current up stream source component
	 */
	public SourceComponentLink<U1, D1> getSourceComponentLink() {
		return this.sourceComponentLink;
	}

	/**
	 * Provides the embedded upstream source component (internal use only)
	 * 
	 * @return the current up stream source component
	 */
	public DestinationComponentLink<U2, D2> getDestinationComponentLink() {
		return this.destinationComponentLink;
	}

	@Override
	public boolean acceptSource(ComponentId componentId) {
		return ComponentLinkFactory.isUndefined(this.sourceComponentLink);
	}

	@Override
	public DisposableLink connectSource(SourceComponentLink<U1, D1> handler) throws ComponentConnectionRejectedException {
		final ComponentId componentId = handler.getSourceComponent().getComponentId();
		if (this.acceptSource(componentId)) {
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

	private void disconnectSource(ComponentId componentId) throws ComponentDisconnectionRejectedException {
		if (!acceptSource(componentId) && sourceComponentLink.getSourceComponent().getComponentId().equals(componentId)) {
			this.sourceComponentLink = ComponentLinkFactory.unboundSourceComponentLink();
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	@Override
	public boolean acceptDestination(ComponentId componentId) {
		return ComponentLinkFactory.isUndefined(this.destinationComponentLink);
	}

	@Override
	public DisposableLink connectDestination(DestinationComponentLink<U2, D2> handler) throws ComponentConnectionRejectedException {
		final ComponentId componentId = handler.getDestinationComponent().getComponentId();
		if (this.acceptDestination(componentId)) {
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
		if (!this.acceptDestination(componentId) && destinationComponentLink.getDestinationComponent().getComponentId().equals(componentId)) {
			this.destinationComponentLink = ComponentLinkFactory.unboundDestinationComponentLink();
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	@Override
	public void closeUpStream() throws DataFlowCloseException {
		if (!ComponentLinkFactory.isUndefined(this.destinationComponentLink)) {
			try {
				this.getUpStreamDataFlow().handleClose();
			} catch (ComponentNotConnectedException e) {
				// TODO
			} finally {
				this.destinationComponentLink.getDestinationComponent().closeUpStream();
			}
		}
	}

	@Override
	public void closeDownStream() throws DataFlowCloseException {
		if (!ComponentLinkFactory.isUndefined(this.sourceComponentLink)) {
			try {
				this.getDownStreamDataFlow().handleClose();
			} catch (ComponentNotConnectedException e) {
				// TODO
			} finally {
				this.sourceComponentLink.getSourceComponent().closeDownStream();
			}
		}
	}
}
