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
import org.wolfgang.contrail.component.MultipleSourceComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.component.core.DirectUpStreamDataHandler;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolfgang.contrail.link.ComponentLinkFactory;
import org.wolfgang.contrail.link.DestinationComponentLink;
import org.wolfgang.contrail.link.SourceComponentLink;

/**
 * The <code>MultiplexerComponent</code> is capable to manage multiple source an
 * a single destination
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class MultiplexerComponent<U, D> extends AbstractComponent implements MultipleSourceComponent<U, D>,
		FilteredSourceComponents<D> {

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
	private final Map<ComponentId, DataFilter<D>> sourceFilters;

	/**
	 * The set of connected filtering destination component (can be empty)
	 */
	private final Map<ComponentId, SourceComponentLink<U, D>> sourceComponents;

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
	private DestinationComponentLink<U, D> upStreamDestinationComponentLink;

	{
		this.sourceFilters = new HashMap<ComponentId, DataFilter<D>>();
		this.sourceComponents = new HashMap<ComponentId, SourceComponentLink<U, D>>();
		this.upStreamDestinationComponentLink = ComponentLinkFactory.undefDestinationComponentLink();
	}

	/**
	 * Constructor
	 */
	public MultiplexerComponent(MultiplexerDataHandlerFactory<D> multiplexerDataHandlerFactory) {
		super();
		this.upStreamDataHandler = new DirectUpStreamDataHandler<U>(this);
		this.downStreamDataHandler = multiplexerDataHandlerFactory.create(this);
	}

	@Override
	public Map<ComponentId, DataFilter<D>> getSourceFilters() {
		return sourceFilters;
	}

	@Override
	public SourceComponent<U, D> getSourceComponent(ComponentId componentId) throws ComponentNotConnectedException {
		final SourceComponentLink<U, D> sourceComponentLink = this.sourceComponents.get(componentId);

		if (sourceComponentLink.getSourceComponent() == null) {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		} else {
			return sourceComponentLink.getSourceComponent();
		}
	}

	@Override
	public UpStreamDataHandler<U> getUpStreamDataHandler() {
		return this.upStreamDataHandler;
	}

	@Override
	public boolean acceptSource(ComponentId componentId) {
		return !this.sourceComponents.containsKey(componentId);
	}

	@Override
	public void connectSource(SourceComponentLink<U, D> handler) throws ComponentConnectionRejectedException {
		assert handler != null;

		if (this.acceptSource(handler.getSourceComponent().getComponentId())) {
			this.sourceComponents.put(handler.getSourceComponent().getComponentId(), handler);
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
	public void filterSource(ComponentId componentId, DataFilter<D> filter) throws ComponentConnectedException {
		assert componentId != null;

		if (!this.sourceComponents.containsKey(componentId)) {
			throw new ComponentConnectedException(NOT_YET_CONNECTED.format());
		} else if (filter == null) {
			this.sourceFilters.remove(componentId);
		} else {
			this.sourceFilters.put(componentId, filter);
		}
	}

	@Override
	public void disconnectSource(ComponentId componentId) throws ComponentDisconnectionRejectedException {
		if (this.sourceComponents.containsKey(componentId)) {
			this.sourceFilters.remove(componentId);
			this.sourceComponents.remove(componentId);
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	@Override
	public void closeUpStream() throws DataHandlerCloseException {
		for (SourceComponentLink<U, D> source : this.sourceComponents.values()) {
			source.getSourceComponent().closeUpStream();
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
	public boolean acceptDestination(ComponentId componentId) {
		return this.upStreamDestinationComponentLink.getDestinationComponent() == null;
	}

	@Override
	public void connectDestination(DestinationComponentLink<U, D> handler) throws ComponentConnectedException {
		if (this.acceptDestination(handler.getDestinationComponent().getComponentId())) {
			this.upStreamDestinationComponentLink = handler;
		} else {
			throw new ComponentConnectedException(ALREADY_CONNECTED.format());
		}
	}

	@Override
	public void disconnectDestination(ComponentId componentId) throws ComponentNotConnectedException {
		final DestinationComponent<U, D> destinationComponent = upStreamDestinationComponentLink.getDestinationComponent();
		if (destinationComponent != null && destinationComponent.getComponentId().equals(componentId)) {
			this.upStreamDestinationComponentLink = ComponentLinkFactory.undefDestinationComponentLink();
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}
}
