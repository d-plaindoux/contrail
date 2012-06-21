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

import org.wolfgang.contrail.component.ComponentConnectedException;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.MultipleDestinationComponent;
import org.wolfgang.contrail.component.MultipleSourceComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.component.multiple.DataFilter;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolfgang.contrail.link.ComponentLink;
import org.wolfgang.contrail.link.DestinationComponentLink;
import org.wolfgang.contrail.link.SourceComponentLink;
import org.wolgang.contrail.network.event.NetworkEvent;
import org.wolgang.contrail.network.reference.DirectReference;

/**
 * <code>NetwortRouterComponent</code> is a component able to manage
 * NetworkEvent
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class NetworkRouterComponent extends AbstractComponent implements
		MultipleDestinationComponent<NetworkEvent, NetworkEvent>, MultipleSourceComponent<NetworkEvent, NetworkEvent> {

	/**
	 * The multiplexer component
	 */
	private final NetworkRouterStreamDataHandler dataHandler;

	/**
	 * The set of connected filtering destination component (can be empty)
	 */
	private final Map<ComponentId, DataFilter<NetworkEvent>> destinationFilters;

	/**
	 * The set of connected filtering destination component (can be empty)
	 */
	private final Map<ComponentId, DestinationComponentLink<NetworkEvent, NetworkEvent>> destinationComponents;

	/**
	 * The set of connected filtering destination component (can be empty)
	 */
	private final Map<ComponentId, DataFilter<NetworkEvent>> sourceFilters;

	/**
	 * The set of connected filtering destination component (can be empty)
	 */
	private final Map<ComponentId, SourceComponentLink<NetworkEvent, NetworkEvent>> sourceComponents;

	/**
	 * The network component identification
	 */
	private final DirectReference endPoint;

	/**
	 * Initialization
	 */
	{
		this.destinationFilters = new HashMap<ComponentId, DataFilter<NetworkEvent>>();
		this.destinationComponents = new HashMap<ComponentId, DestinationComponentLink<NetworkEvent, NetworkEvent>>();
		this.sourceFilters = new HashMap<ComponentId, DataFilter<NetworkEvent>>();
		this.sourceComponents = new HashMap<ComponentId, SourceComponentLink<NetworkEvent, NetworkEvent>>();
	}

	/**
	 * Constructor
	 */
	NetworkRouterComponent(DirectReference endPoint) {
		this.endPoint = endPoint;
		this.dataHandler = new NetworkRouterStreamDataHandler(this);
	}

	@Override
	public boolean acceptDestination(ComponentId componentId) {
		return !this.destinationComponents.containsKey(componentId);
	}

	@Override
	public ComponentLink connectDestination(DestinationComponentLink<NetworkEvent, NetworkEvent> handler)
			throws ComponentConnectionRejectedException {
		final ComponentId componentId = handler.getDestinationComponent().getComponentId();
		if (this.acceptDestination(componentId)) {
			this.destinationComponents.put(componentId, handler);
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
		if (this.destinationComponents.containsKey(componentId)) {
			this.destinationComponents.remove(componentId);
			this.destinationFilters.remove(componentId);
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	@Override
	public void closeUpStream() throws DataHandlerCloseException {
		for (DestinationComponentLink<NetworkEvent, NetworkEvent> source : this.destinationComponents.values()) {
			source.getDestinationComponent().closeUpStream();
		}
	}

	@Override
	public void closeDownStream() throws DataHandlerCloseException {
		for (SourceComponentLink<NetworkEvent, NetworkEvent> source : this.sourceComponents.values()) {
			source.getSourceComponent().closeUpStream();
		}
	}

	@Override
	public boolean acceptSource(ComponentId componentId) {
		return !this.sourceComponents.containsKey(componentId);
	}

	@Override
	public ComponentLink connectSource(SourceComponentLink<NetworkEvent, NetworkEvent> handler)
			throws ComponentConnectedException {
		assert handler != null;

		final ComponentId componentId = handler.getSourceComponent().getComponentId();
		if (this.acceptSource(componentId)) {
			this.sourceComponents.put(componentId, handler);
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
		if (this.sourceComponents.containsKey(componentId)) {
			this.sourceComponents.remove(componentId);
			this.sourceFilters.remove(componentId);
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	/**
	 * Provide the existing filters
	 * 
	 * @return a map of filters
	 */
	public Map<ComponentId, DataFilter<NetworkEvent>> getDestinationFilters() {
		return this.destinationFilters;
	}

	/**
	 * Provide a component using it's identifier
	 * 
	 * @return an destination component
	 * @throws ComponentNotConnectedException
	 */
	public DestinationComponent<NetworkEvent, NetworkEvent> getDestinationComponent(ComponentId componentId)
			throws ComponentNotConnectedException {
		final DestinationComponentLink<NetworkEvent, NetworkEvent> destinationComponent = this.destinationComponents
				.get(componentId);

		if (destinationComponent == null) {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		} else {
			return destinationComponent.getDestinationComponent();
		}
	}

	@Override
	public UpStreamDataHandler<NetworkEvent> getUpStreamDataHandler() {
		return this.dataHandler;
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
	public void filterDestination(ComponentId componentId, DataFilter<NetworkEvent> filter) throws ComponentConnectedException {
		assert componentId != null;

		if (!this.destinationComponents.containsKey(componentId)) {
			throw new ComponentConnectedException(NOT_YET_CONNECTED.format());
		} else if (filter == null) {
			this.destinationFilters.remove(componentId);
		} else {
			this.destinationFilters.put(componentId, filter);
		}
	}

	@Override
	public DownStreamDataHandler<NetworkEvent> getDownStreamDataHandler() {
		return this.dataHandler;
	}

	/**
	 * Provide the existing filters
	 * 
	 * @return a map of filters
	 */
	public Map<ComponentId, DataFilter<NetworkEvent>> getSourceFilters() {
		return this.sourceFilters;
	}

	/**
	 * Provide a component using it's identifier
	 * 
	 * @return an source component
	 * @throws ComponentNotConnectedException
	 */
	public SourceComponent<NetworkEvent, NetworkEvent> getSourceComponent(ComponentId componentId)
			throws ComponentNotConnectedException {
		final SourceComponentLink<NetworkEvent, NetworkEvent> destinationComponent = this.sourceComponents.get(componentId);

		if (destinationComponent == null) {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		} else {
			return destinationComponent.getSourceComponent();
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
	public void filterSource(ComponentId componentId, DataFilter<NetworkEvent> filter) throws ComponentConnectedException {
		assert componentId != null;

		if (!this.sourceComponents.containsKey(componentId)) {
			throw new ComponentConnectedException(NOT_YET_CONNECTED.format());
		} else if (filter == null) {
			this.sourceFilters.remove(componentId);
		} else {
			this.sourceFilters.put(componentId, filter);
		}
	}
}
