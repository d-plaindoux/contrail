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

package org.wolfgang.contrail.component.router;

import java.util.HashMap;
import java.util.Map;

import org.wolfgang.contrail.component.ComponentConnectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.MultipleDestinationComponent;
import org.wolfgang.contrail.component.MultipleSourceComponent;
import org.wolfgang.contrail.component.annotation.ContrailDownType;
import org.wolfgang.contrail.component.annotation.ContrailUpType;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.event.Event;
import org.wolfgang.contrail.handler.DataHandler;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolfgang.contrail.link.ComponentLink;
import org.wolfgang.contrail.link.ComponentLinkFactory;
import org.wolfgang.contrail.link.DestinationComponentLink;
import org.wolfgang.contrail.link.SourceComponentLink;
import org.wolfgang.contrail.reference.DirectReference;

/**
 * <code>RouterSourceComponent</code> is a component able to manage NetworkEvent
 * and opening route on-demand.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@ContrailUpType(in = Event.class, out = Event.class)
@ContrailDownType(in = Event.class, out = Event.class)
public class RouterComponent extends AbstractComponent implements MultipleDestinationComponent<Event, Event>, MultipleSourceComponent<Event, Event> {

	/**
	 * The multiplexer component
	 */
	private final RouterDataHandlerStation streamStation;

	/**
	 * The set of connected filtering destination component (can be empty)
	 */
	private final Map<ComponentId, DirectReference> filters;

	/**
	 * The set of connected filtering destination component (can be empty)
	 */
	private Map<ComponentId, DestinationComponentLink<Event, Event>> destinationLinks;

	/**
	 * The set of connected filtering destination component (can be empty)
	 */
	private final Map<ComponentId, SourceComponentLink<Event, Event>> sourceLinks;

	/**
	 * Initialization
	 */
	{
		this.filters = new HashMap<ComponentId, DirectReference>();
		this.sourceLinks = new HashMap<ComponentId, SourceComponentLink<Event, Event>>();
		this.destinationLinks = new HashMap<ComponentId, DestinationComponentLink<Event, Event>>();
	}

	/**
	 * Constructor
	 */
	public RouterComponent(RouterSourceTable table, DirectReference selfReference) {
		this.streamStation = new RouterDataHandlerStation(this, selfReference, table);
	}

	/**
	 * @return the network table
	 */
	public RouterSourceTable getRouterSourceTable() {
		return this.streamStation.getRouterTable();
	}

	@Override
	public boolean acceptDestination(ComponentId componentId) {
		return !this.destinationLinks.containsKey(componentId);
	}

	@Override
	public ComponentLink connectDestination(DestinationComponentLink<Event, Event> handler) throws ComponentConnectedException {
		assert handler != null;

		final ComponentId componentId = handler.getDestination().getComponentId();
		if (this.acceptSource(componentId)) {
			this.destinationLinks.put(componentId, handler);
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
		if (this.destinationLinks.containsKey(componentId)) {
			this.destinationLinks.remove(componentId);
			this.filters.remove(componentId);
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	@Override
	public boolean acceptSource(ComponentId componentId) {
		return !this.sourceLinks.containsKey(componentId);
	}

	@Override
	public ComponentLink connectSource(SourceComponentLink<Event, Event> handler) throws ComponentConnectedException {
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
			this.filters.remove(componentId);
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	@Override
	public UpStreamDataHandler<Event> getUpStreamDataHandler() {
		return this.streamStation;
	}

	@Override
	public DownStreamDataHandler<Event> getDownStreamDataHandler() {
		return this.streamStation;
	}

	/**
	 * Provide the existing filters
	 * 
	 * @return a map of filters
	 */
	public Map<ComponentId, DirectReference> getFilters() {
		return this.filters;
	}

	/**
	 * Provide a component using it's identifier
	 * 
	 * @return an source component
	 * @throws ComponentNotConnectedException
	 */
	DataHandler<Event> getDataHander(ComponentId componentId) throws ComponentNotConnectedException {
		final DestinationComponentLink<Event, Event> destinationComponentLink = this.destinationLinks.get(componentId);

		if (!ComponentLinkFactory.isUndefined(destinationComponentLink)) {
			return destinationComponentLink.getDestination().getUpStreamDataHandler();
		}

		final SourceComponentLink<Event, Event> sourceComponentLink = this.sourceLinks.get(componentId);

		if (!ComponentLinkFactory.isUndefined(sourceComponentLink)) {
			return sourceComponentLink.getSource().getDownStreamDataHandler();
		}

		throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
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
	public void filter(ComponentId componentId, DirectReference filter) throws ComponentConnectedException {
		assert componentId != null;

		if (!this.sourceLinks.containsKey(componentId) && !this.destinationLinks.containsKey(componentId)) {
			throw new ComponentConnectedException(NOT_YET_CONNECTED.format());
		} else if (filter == null) {
			this.filters.remove(componentId);
		} else {
			this.filters.put(componentId, filter);
		}
	}

	@Override
	public void closeUpStream() throws DataHandlerCloseException {
		for (DestinationComponentLink<Event, Event> source : this.destinationLinks.values()) {
			source.getDestination().closeUpStream();
		}
	}

	@Override
	public void closeDownStream() throws DataHandlerCloseException {
		for (SourceComponentLink<Event, Event> source : this.sourceLinks.values()) {
			source.getSource().closeUpStream();
		}
	}
}
