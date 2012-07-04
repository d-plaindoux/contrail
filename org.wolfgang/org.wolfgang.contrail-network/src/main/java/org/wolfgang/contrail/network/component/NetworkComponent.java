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

import java.util.HashMap;
import java.util.Map;

import org.wolfgang.common.utils.Pair;
import org.wolfgang.contrail.component.ComponentConnectedException;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.MultipleSourceComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolfgang.contrail.link.ComponentLink;
import org.wolfgang.contrail.link.ComponentLinkFactory;
import org.wolfgang.contrail.link.DestinationComponentLink;
import org.wolfgang.contrail.link.SourceComponentLink;
import org.wolfgang.contrail.network.event.NetworkEvent;
import org.wolfgang.contrail.network.reference.DirectReference;

/**
 * <code>NetwortComponent</code> is a component able to manage NetworkEvent and
 * opening route on-demand.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class NetworkComponent extends AbstractComponent implements DestinationComponent<NetworkEvent, NetworkEvent>, MultipleSourceComponent<NetworkEvent, NetworkEvent> {

	/**
	 * The multiplexer component
	 */
	private final NetworkStreamStation networkStreamStation;

	/**
	 * The set of connected filtering destination component (can be empty)
	 */
	private DestinationComponentLink<NetworkEvent, NetworkEvent> destinationLink;

	/**
	 * The set of connected filtering destination component (can be empty)
	 */
	private final Map<ComponentId, DirectReference> sourceFilters;

	/**
	 * The set of connected filtering destination component (can be empty)
	 */
	private final Map<ComponentId, SourceComponentLink<NetworkEvent, NetworkEvent>> sourceLinks;

	/**
	 * Initialization
	 */
	{
		this.sourceFilters = new HashMap<ComponentId, DirectReference>();
		this.sourceLinks = new HashMap<ComponentId, SourceComponentLink<NetworkEvent, NetworkEvent>>();
	}

	/**
	 * Constructor
	 */
	NetworkComponent(NetworkTable table, DirectReference selfReference) {
		this.destinationLink = ComponentLinkFactory.undefDestinationComponentLink();
		this.networkStreamStation = new NetworkStreamStation(this, selfReference, table);
	}

	@Override
	public Pair<Class<NetworkEvent>, Class<NetworkEvent>> getUpStreamType() {
		return Pair.create(NetworkEvent.class, NetworkEvent.class);
	}

	/**
	 * @return the network table
	 */
	public NetworkTable getNetworkTable() {
		return this.networkStreamStation.getRouterTable();
	}

	/**
	 * @return the network component reference
	 */
	public DirectReference getSelfReference() {
		return this.networkStreamStation.getSelfReference();
	}

	/**
	 * Return the value ofdestinationLink
	 * 
	 * @return the destinationLink
	 * @throws ComponentNotConnectedException
	 */
	DestinationComponent<NetworkEvent, NetworkEvent> getDestination() throws ComponentNotConnectedException {
		if (destinationLink.getDestination() == null) {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		} else {
			return destinationLink.getDestination();
		}
	}

	@Override
	public boolean acceptDestination(ComponentId componentId) {
		return this.destinationLink.getDestination() == null;
	}

	@Override
	public ComponentLink connectDestination(DestinationComponentLink<NetworkEvent, NetworkEvent> handler) throws ComponentConnectionRejectedException {
		final ComponentId componentId = handler.getDestination().getComponentId();
		if (this.acceptDestination(componentId)) {
			this.destinationLink = handler;
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
		if (this.destinationLink.getDestination() != null) {
			this.destinationLink = ComponentLinkFactory.undefDestinationComponentLink();
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	@Override
	public void closeUpStream() throws DataHandlerCloseException {
		if (this.destinationLink.getDestination() != null) {
			this.destinationLink.getDestination().closeUpStream();
		}
	}

	@Override
	public void closeDownStream() throws DataHandlerCloseException {
		for (SourceComponentLink<NetworkEvent, NetworkEvent> source : this.sourceLinks.values()) {
			source.getSource().closeUpStream();
		}
	}

	@Override
	public boolean acceptSource(ComponentId componentId) {
		return !this.sourceLinks.containsKey(componentId);
	}

	@Override
	public ComponentLink connectSource(SourceComponentLink<NetworkEvent, NetworkEvent> handler) throws ComponentConnectedException {
		assert handler != null;

		final ComponentId componentId = handler.getSource().getComponentId();
		if (this.acceptSource(componentId)) {
			this.sourceLinks.put(componentId, handler);
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

	private void disconnectSource(ComponentId componentId) throws ComponentNotConnectedException {
		if (this.sourceLinks.containsKey(componentId)) {
			this.sourceLinks.remove(componentId);
			this.sourceFilters.remove(componentId);
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	@Override
	public UpStreamDataHandler<NetworkEvent> getUpStreamDataHandler() {
		return this.networkStreamStation;
	}

	@Override
	public DownStreamDataHandler<NetworkEvent> getDownStreamDataHandler() {
		return this.networkStreamStation;
	}

	/**
	 * Provide the existing filters
	 * 
	 * @return a map of filters
	 */
	public Map<ComponentId, DirectReference> getSourceFilters() {
		return this.sourceFilters;
	}

	/**
	 * Provide a component using it's identifier
	 * 
	 * @return an source component
	 * @throws ComponentNotConnectedException
	 */
	public SourceComponent<NetworkEvent, NetworkEvent> getSource(ComponentId componentId) throws ComponentNotConnectedException {
		final SourceComponentLink<NetworkEvent, NetworkEvent> sourceComponentLink = this.sourceLinks.get(componentId);

		if (sourceComponentLink == null) {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		} else {
			return sourceComponentLink.getSource();
		}
	}

	/**
	 * Method used to add a filter to a given destination. All destination
	 * without any filter are unreachable. A filter must be added if destination
	 * component must be used when data are managed.
	 * 
	 * @param componentId
	 *            The component identifier
	 * @param filter
	 *            The filter (can be <code>null</code>)
	 * @throws ComponentConnectedException
	 */
	public void filterSource(ComponentId componentId, DirectReference filter) throws ComponentConnectedException {
		assert componentId != null;

		if (!this.sourceLinks.containsKey(componentId)) {
			throw new ComponentConnectedException(NOT_YET_CONNECTED.format());
		} else if (filter == null) {
			this.sourceFilters.remove(componentId);
		} else {
			this.sourceFilters.put(componentId, filter);
		}
	}
}
