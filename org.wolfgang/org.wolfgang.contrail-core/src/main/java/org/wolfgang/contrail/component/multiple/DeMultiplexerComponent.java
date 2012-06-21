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

package org.wolfgang.contrail.component.multiple;

import static org.wolfgang.common.message.MessagesProvider.message;

import java.util.HashMap;
import java.util.Map;

import org.wolfgang.common.message.Message;
import org.wolfgang.contrail.component.ComponentConnectedException;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.MultipleDestinationComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.component.core.DirectDownStreamDataHandler;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolfgang.contrail.link.ComponentLink;
import org.wolfgang.contrail.link.ComponentLinkFactory;
import org.wolfgang.contrail.link.DestinationComponentLink;
import org.wolfgang.contrail.link.SourceComponentLink;

/**
 * The <code>DeMultiplexerComponent</code> is capable to manage multiple
 * destination an a single source
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class DeMultiplexerComponent<U, D> extends AbstractComponent implements MultipleDestinationComponent<U, D>,
		FilteredDestinationComponents<U> {

	/**
	 * Static message definition for not yet connected component
	 */
	protected static final Message FILTERING_SOURCE_REQUIRED;

	static {
		final String category = "org.wolfgang.contrail.message";
		FILTERING_SOURCE_REQUIRED = message(category, "filtering.source.required");
	}

	/**
	 * The set of connected filtering destination component (can be empty)
	 */
	private final Map<ComponentId, DataFilter<U>> destinationFilters;

	/**
	 * The set of connected filtering destination component (can be empty)
	 */
	private final Map<ComponentId, DestinationComponentLink<U, D>> destinationComponents;

	/**
	 * The internal upstream data handler
	 */
	private final UpStreamDataHandler<U> upStreamDataHandler;

	/**
	 * The internal upstream data handler
	 */
	private final DownStreamDataHandler<D> downStreamDataHandler;

	/**
	 * The connected upstream destination component (can be <code>null</code>)
	 */
	private SourceComponentLink<U, D> upStreamSourceComponentLink;

	{
		this.destinationFilters = new HashMap<ComponentId, DataFilter<U>>();
		this.destinationComponents = new HashMap<ComponentId, DestinationComponentLink<U, D>>();
		this.upStreamSourceComponentLink = ComponentLinkFactory.undefSourceComponentLink();
	}

	/**
	 * Constructor
	 */
	public DeMultiplexerComponent(DeMultiplexerDataHandlerFactory<U> upStreamDataHandler) {
		super();
		this.upStreamDataHandler = upStreamDataHandler.create(this);
		this.downStreamDataHandler = new DirectDownStreamDataHandler<D>(this);
	}

	@Override
	public Map<ComponentId, DataFilter<U>> getDestinationFilters() {
		return destinationFilters;
	}

	@Override
	public DestinationComponent<U, D> getDestinationComponent(ComponentId componentId) throws ComponentNotConnectedException {
		final DestinationComponentLink<U, D> destinationComponentLink = this.destinationComponents.get(componentId);

		if (destinationComponentLink.getDestinationComponent() == null) {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		} else {
			return destinationComponentLink.getDestinationComponent();
		}
	}

	@Override
	public UpStreamDataHandler<U> getUpStreamDataHandler() {
		return this.upStreamDataHandler;
	}

	@Override
	public boolean acceptDestination(ComponentId componentId) {
		return !this.destinationComponents.containsKey(componentId);
	}

	@Override
	public ComponentLink connectDestination(DestinationComponentLink<U, D> handler) throws ComponentConnectionRejectedException {
		assert handler != null;

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
	public void filterDestination(ComponentId componentId, DataFilter<U> filter) throws ComponentConnectedException {
		assert componentId != null;

		if (!this.destinationComponents.containsKey(componentId)) {
			throw new ComponentConnectedException(NOT_YET_CONNECTED.format());
		} else if (filter == null) {
			this.destinationFilters.remove(componentId);
		} else {
			this.destinationFilters.put(componentId, filter);
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
		for (DestinationComponentLink<U, D> source : this.destinationComponents.values()) {
			source.getDestinationComponent().closeUpStream();
		}
	}

	@Override
	public void closeDownStream() throws DataHandlerCloseException {
		this.getDownStreamDataHandler().handleClose();
	}

	@Override
	public DownStreamDataHandler<D> getDownStreamDataHandler() {
		return this.downStreamDataHandler;
	}

	@Override
	public boolean acceptSource(ComponentId componentId) {
		return this.upStreamSourceComponentLink.getSourceComponent() == null;
	}

	@Override
	public ComponentLink connectSource(SourceComponentLink<U, D> handler) throws ComponentConnectedException {
		final ComponentId componentId = handler.getSourceComponent().getComponentId();
		if (this.acceptSource(componentId)) {
			this.upStreamSourceComponentLink = handler;
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
		final SourceComponent<U, D> sourceComponent = this.upStreamSourceComponentLink.getSourceComponent();
		if (sourceComponent != null && sourceComponent.getComponentId().equals(componentId)) {
			this.upStreamSourceComponentLink = ComponentLinkFactory.undefSourceComponentLink();
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}
}
