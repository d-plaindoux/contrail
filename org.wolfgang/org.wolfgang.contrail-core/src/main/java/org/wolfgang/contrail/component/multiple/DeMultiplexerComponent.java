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

import java.util.HashMap;
import java.util.Map;

import org.wolfgang.common.message.Message;
import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.contrail.component.ComponentConnectedException;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.MultipleDestinationComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.component.core.DirectDownStreamDataHandler;
import org.wolfgang.contrail.data.DataInformationFilter;
import org.wolfgang.contrail.data.DataWithInformation;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;

/**
 * The <code>DeMultiplexerComponent</code> is capable to manage multiple
 * destination an a single source
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class DeMultiplexerComponent<U, D> extends AbstractComponent implements
		MultipleDestinationComponent<DataWithInformation<U>, D>, FilteredDestinationComponents<U> {

	/**
	 * Static message definition for not yet connected component
	 */
	protected static final Message FILTERING_SOURCE_REQUIRED;

	static {
		final String category = "org.wolfgang.contrail.message";

		FILTERING_SOURCE_REQUIRED = MessagesProvider.get(category, "filtering.source.required");
	}

	/**
	 * The set of connected filtering destination component (can be empty)
	 */
	private final Map<ComponentId, DataInformationFilter> destinationFilters;

	/**
	 * The set of connected filtering destination component (can be empty)
	 */
	private final Map<ComponentId, DestinationComponent<DataWithInformation<U>, D>> destinationComponents;

	/**
	 * The internal upstream data handler
	 */
	private final UpStreamDataHandler<DataWithInformation<U>> upStreamDataHandler;

	/**
	 * The internal upstream data handler
	 */
	private final DownStreamDataHandler<D> downStreamDataHandler;

	/**
	 * The connected upstream destination component (can be <code>null</code>)
	 */
	private SourceComponent<DataWithInformation<U>, D> upStreamSourceComponent;

	{
		this.destinationFilters = new HashMap<ComponentId, DataInformationFilter>();
		this.destinationComponents = new HashMap<ComponentId, DestinationComponent<DataWithInformation<U>, D>>();
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
	public Map<ComponentId, DataInformationFilter> getDestinationFilters() {
		return destinationFilters;
	}

	@Override
	public DestinationComponent<DataWithInformation<U>, D> getDestinationComponent(ComponentId componentId)
			throws ComponentNotConnectedException {
		final DestinationComponent<DataWithInformation<U>, D> destinationComponent = this.destinationComponents
				.get(componentId);

		if (destinationComponent == null) {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		} else {
			return destinationComponent;
		}
	}

	@Override
	public UpStreamDataHandler<DataWithInformation<U>> getUpStreamDataHandler() {
		return this.upStreamDataHandler;
	}

	@Override
	public void connect(DestinationComponent<DataWithInformation<U>, D> handler) throws ComponentConnectionRejectedException {
		assert handler != null;

		if (this.destinationComponents.containsKey(handler.getComponentId())) {
			throw new ComponentConnectedException(ALREADY_CONNECTED.format());
		} else {
			this.destinationComponents.put(handler.getComponentId(), handler);
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
	public void filterDestination(ComponentId componentId, DataInformationFilter filter) throws ComponentConnectedException {
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
	public void disconnect(DestinationComponent<DataWithInformation<U>, D> handler) throws ComponentNotConnectedException {
		if (this.destinationComponents.containsKey(handler.getComponentId())) {
			this.destinationComponents.remove(handler.getComponentId());
			this.destinationFilters.remove(handler.getComponentId());
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	@Override
	public void closeUpStream() throws DataHandlerCloseException {
		for (DestinationComponent<DataWithInformation<U>, D> source : this.destinationComponents.values()) {
			source.closeUpStream();
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
	public void connect(SourceComponent<DataWithInformation<U>, D> handler) throws ComponentConnectedException {
		if (this.upStreamSourceComponent == null) {
			this.upStreamSourceComponent = handler;
		} else {
			throw new ComponentConnectedException(ALREADY_CONNECTED.format());
		}
	}

	@Override
	public void disconnect(SourceComponent<DataWithInformation<U>, D> handler) throws ComponentNotConnectedException {
		if (this.upStreamSourceComponent != null
				&& this.upStreamSourceComponent.getComponentId().equals(handler.getComponentId())) {
			this.upStreamSourceComponent = null;
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}
}
