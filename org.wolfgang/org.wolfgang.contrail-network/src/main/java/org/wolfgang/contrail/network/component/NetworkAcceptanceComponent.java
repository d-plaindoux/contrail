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

package org.wolfgang.contrail.network.component;

import org.wolfgang.common.utils.Coercion;
import org.wolfgang.common.utils.Pair;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.PipelineComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolfgang.contrail.link.ComponentLink;
import org.wolfgang.contrail.link.ComponentLinkFactory;
import org.wolfgang.contrail.link.ComponentLinkManager;
import org.wolfgang.contrail.link.DestinationComponentLink;
import org.wolfgang.contrail.link.SourceComponentLink;
import org.wolfgang.contrail.network.event.NetworkEvent;
import org.wolfgang.contrail.network.reference.DirectReference;

/**
 * <code>NetworkLinkComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class NetworkAcceptanceComponent extends AbstractComponent implements PipelineComponent<NetworkEvent, NetworkEvent, NetworkEvent, NetworkEvent> {

	private SourceComponentLink<NetworkEvent, NetworkEvent> sourceComponentLink;
	private DestinationComponentLink<NetworkEvent, NetworkEvent> destinationComponentLink;
	private UpStreamDataHandler<NetworkEvent> intermediateUpStreamHandler;

	private NetworkComponent networkComponent;

	{
		this.sourceComponentLink = ComponentLinkFactory.undefSourceComponentLink();
		this.destinationComponentLink = ComponentLinkFactory.undefDestinationComponentLink();
	}

	/**
	 * Constructor
	 */
	public NetworkAcceptanceComponent() {
		super();
	}

	@Override
	public void closeUpStream() throws DataHandlerCloseException {
		// TODO Auto-generated method stub
	}

	@Override
	public void closeDownStream() throws DataHandlerCloseException {
		// TODO Auto-generated method stub
	}

	@Override
	public UpStreamDataHandler<NetworkEvent> getUpStreamDataHandler() {
		if (intermediateUpStreamHandler == null) {
			intermediateUpStreamHandler = new UpStreamDataHandler<NetworkEvent>() {
				@Override
				public void handleData(NetworkEvent data) throws DataHandlerException {
					try {
						// Retrieve the component reference
						final DirectReference senderReference = data.getSender();
						final DirectReference receiverReference = networkComponent.getSelfReference();
						final ComponentLinkManager destinationComponentLinkManager = destinationComponentLink.getComponentLinkManager();
						final SourceComponent<NetworkEvent, NetworkEvent> source = sourceComponentLink.getSource();
						final DestinationComponent<NetworkEvent, NetworkEvent> destination = destinationComponentLink.getDestination();

						sourceComponentLink.dispose();
						destinationComponentLink.dispose();

						destinationComponentLinkManager.connect(source, destination);

						if (senderReference == null || senderReference.equals(receiverReference)) {
							source.closeDownStream();
						} else {
							networkComponent.filterSource(source.getComponentId(), senderReference);
						}

						// Re-send the event to the network component
						networkComponent.getDownStreamDataHandler().handleData(data);
					} catch (Throwable e) {
						e.printStackTrace();
						throw new DataHandlerException(e);
					}
				}

				@Override
				public void handleClose() throws DataHandlerCloseException {
					// TODO Auto-generated method stub
				}

				@Override
				public void handleLost() throws DataHandlerCloseException {
					// TODO Auto-generated method stub
				}
			};
		}

		return this.intermediateUpStreamHandler;
	}

	@Override
	public boolean acceptSource(ComponentId componentId) {
		return ComponentLinkFactory.isUndefined(this.sourceComponentLink);
	}

	@Override
	public ComponentLink connectSource(SourceComponentLink<NetworkEvent, NetworkEvent> handler) throws ComponentConnectionRejectedException {
		final SourceComponent<NetworkEvent, NetworkEvent> source = handler.getSource();
		final ComponentId componentId = source.getComponentId();
		if (this.acceptSource(componentId)) {
			this.sourceComponentLink = handler;
			return new ComponentLink() {
				@Override
				public void dispose() throws ComponentDisconnectionRejectedException {
					disconnectSource(componentId);
				}
			};
		} else {
			// TODO - Add a specific message
			throw new ComponentConnectionRejectedException("TODO");
		}
	}

	@Override
	public Pair<Class<NetworkEvent>, Class<NetworkEvent>> getUpStreamType() {
		return Pair.create(NetworkEvent.class, NetworkEvent.class);
	}

	@Override
	public DownStreamDataHandler<NetworkEvent> getDownStreamDataHandler() {
		return this.sourceComponentLink.getSource().getDownStreamDataHandler();
	}

	@Override
	public boolean acceptDestination(ComponentId componentId) {
		return ComponentLinkFactory.isUndefined(this.destinationComponentLink);
	}

	@Override
	public ComponentLink connectDestination(DestinationComponentLink<NetworkEvent, NetworkEvent> handler) throws ComponentConnectionRejectedException {
		final DestinationComponent<NetworkEvent, NetworkEvent> destination = handler.getDestination();
		final ComponentId componentId = destination.getComponentId();
		if (this.acceptDestination(componentId) && Coercion.canCoerce(destination, NetworkComponent.class)) {
			this.destinationComponentLink = handler;
			this.networkComponent = Coercion.coerce(destination, NetworkComponent.class);
			return new ComponentLink() {
				@Override
				public void dispose() throws ComponentDisconnectionRejectedException {
					disconnectDestination(componentId);
				}
			};
		} else {
			// TODO - Add a specific message
			throw new ComponentConnectionRejectedException("TODO");
		}
	}

	private void disconnectDestination(ComponentId componentId) throws ComponentNotConnectedException {
		if (!this.acceptDestination(componentId) && destinationComponentLink.getDestination().getComponentId().equals(componentId)) {
			this.destinationComponentLink = ComponentLinkFactory.undefDestinationComponentLink();
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	private void disconnectSource(ComponentId componentId) throws ComponentDisconnectionRejectedException {
		if (!acceptSource(componentId) && this.sourceComponentLink.getSource().getComponentId().equals(componentId)) {
			this.sourceComponentLink = ComponentLinkFactory.undefSourceComponentLink();
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}
}
