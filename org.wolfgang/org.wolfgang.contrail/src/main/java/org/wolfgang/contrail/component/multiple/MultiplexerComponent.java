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
import org.wolfgang.contrail.component.MultipleSourceComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.component.core.DirectUpStreamDataHandler;
import org.wolfgang.contrail.data.DataInformationFilter;
import org.wolfgang.contrail.data.DataWithInformation;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;

/**
 * <code>DeMultiplexerComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class MultiplexerComponent<U, D> extends AbstractComponent implements MultipleSourceComponent<U, DataWithInformation<D>> {

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
	private final Map<ComponentId, DataInformationFilter> sourceFilters;

	/**
	 * The set of connected filtering destination component (can be empty)
	 */
	private final Map<ComponentId, SourceComponent<U, DataWithInformation<D>>> sourceComponents;

	/**
	 * The internal upstream data handler
	 */
	private final UpStreamDataHandler<U> upStreamDataHandler;

	/**
	 * The internal upstream data handler
	 */
	private final DownStreamDataHandler<DataWithInformation<D>> downStreamDataHandler;

	/**
	 * The connected upstream destination component (can be <code>null</code>)
	 */
	private DestinationComponent<U,DataWithInformation<D>> upStreamSourceComponent;

	{
		this.sourceFilters = new HashMap<ComponentId, DataInformationFilter>();
		this.sourceComponents = new HashMap<ComponentId, SourceComponent<U, DataWithInformation<D>>>();
	}

	/**
	 * Constructor
	 */
	public MultiplexerComponent() {
		super();
		this.upStreamDataHandler = new DirectUpStreamDataHandler<U>(this);
		this.downStreamDataHandler = new MultiplexerDataHandler<D>(this);
	}

	/**
	 * Provide the existing filters
	 * 
	 * @return a map of filters
	 */
	Map<ComponentId, DataInformationFilter> getSourceFilters() {
		return sourceFilters;
	}

	/**
	 * Provide a component using it's identifier
	 * 
	 * @return an destination component
	 * @throws ComponentNotConnectedException
	 */
	SourceComponent<U, DataWithInformation<D>> getSourceComponent(ComponentId componentId)
			throws ComponentNotConnectedException {
		final SourceComponent<U, DataWithInformation<D>> destinationComponent = this.sourceComponents.get(componentId);

		if (destinationComponent == null) {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		} else {
			return destinationComponent;
		}
	}

	@Override
	public UpStreamDataHandler<U> getUpStreamDataHandler() {
		return this.upStreamDataHandler;
	}

	@Override
	public void connect(SourceComponent<U, DataWithInformation<D>> handler) throws ComponentConnectionRejectedException {
		assert handler != null;

		if (this.sourceComponents.containsKey(handler.getComponentId())) {
			throw new ComponentConnectedException(ALREADY_CONNECTED.format());
		} else {
			this.sourceComponents.put(handler.getComponentId(), handler);
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
	 */
	public void filter(ComponentId componentId, DataInformationFilter filter) {
		assert componentId != null;

		if (filter == null) {
			this.sourceFilters.remove(componentId);
		} else {
			this.sourceFilters.put(componentId, filter);
		}
	}

	@Override
	public void disconnect(SourceComponent<U, DataWithInformation<D>> handler) throws ComponentNotConnectedException {
		if (this.sourceComponents.containsKey(handler.getComponentId())) {
			this.sourceComponents.remove(handler.getComponentId());
			this.sourceFilters.remove(handler.getComponentId());
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	@Override
	public void closeUpStream() throws DataHandlerCloseException {
		for (SourceComponent<U, DataWithInformation<D>> source : this.sourceComponents.values()) {
			source.closeUpStream();
		}
	}

	@Override
	public void closeDownStream() throws DataHandlerCloseException {
		this.getDownStreamDataHandler().handleClose();
	}

	@Override
	public DownStreamDataHandler<DataWithInformation<D>> getDownStreamDataHandler() {
		return this.downStreamDataHandler;
	}

	@Override
	public void connect(DestinationComponent<U, DataWithInformation<D>> handler) throws ComponentConnectedException {
		if (this.upStreamSourceComponent == null) {
			this.upStreamSourceComponent = handler;
		} else {
			throw new ComponentConnectedException(ALREADY_CONNECTED.format());
		}
	}

	@Override
	public void disconnect(DestinationComponent<U, DataWithInformation<D>> handler) throws ComponentNotConnectedException {
		if (this.upStreamSourceComponent != null
				&& this.upStreamSourceComponent.getComponentId().equals(handler.getComponentId())) {
			this.upStreamSourceComponent = null;
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}
}
