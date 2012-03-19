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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.wolfgang.common.message.Message;
import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.common.utils.Coercion;
import org.wolfgang.contrail.component.ComponentConnectedException;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.MultipleDestinationComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.component.core.DirectDownStreamDataHandler;
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
public class DeMultiplexerComponent<U,D> extends AbstractComponent implements
		MultipleDestinationComponent<DataWithInformation<U>, D> {

	/**
	 * Static message definition for not yet connected component
	 */
	protected static final Message FILTERING_SOURCE_REQUIRED;

	static {
		final String category = "org.wolfgang.contrail.message";

		FILTERING_SOURCE_REQUIRED = MessagesProvider.get(category, "filtering.source.required");
	}

	/**
	 * The set of connected filtering source component (can be empty)
	 */
	private final Map<ComponentId, FilteringDestinationComponent<U,D>> sourceComponents;

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
	private SourceComponent<DataWithInformation<U>,D> upStreamSourceComponent;

	{
		this.sourceComponents = new HashMap<ComponentId, FilteringDestinationComponent<U,D>>();
	}

	/**
	 * Constructor
	 */
	public DeMultiplexerComponent() {
		super();
		this.upStreamDataHandler = new DeMultiplexerDataHandler<U>(this);
		this.downStreamDataHandler = new DirectDownStreamDataHandler<D>(this);
	}

	/**
	 * Provide an array of connected upstream source components
	 * 
	 * @return an array of upstream source component
	 */
	@SuppressWarnings("unchecked")
	FilteringDestinationComponent<U,D>[] getSourceComponents() {
		final Collection<FilteringDestinationComponent<U,D>> values = sourceComponents.values();
		return values.toArray(new FilteringDestinationComponent[values.size()]);
	}

	@Override
	public UpStreamDataHandler<DataWithInformation<U>> getUpStreamDataHandler() {
		return this.upStreamDataHandler;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void connect(DestinationComponent<DataWithInformation<U>,D> handler)
			throws ComponentConnectionRejectedException {
		if (this.sourceComponents.containsKey(handler.getComponentId())) {
			throw new ComponentConnectedException(ALREADY_CONNECTED.format());
		} else if (Coercion.canCoerce(handler, FilteringDestinationComponent.class)) {
			this.sourceComponents.put(handler.getComponentId(),
					Coercion.coerce(handler, FilteringDestinationComponent.class));
		} else {
			throw new ComponentConnectionRejectedException(FILTERING_SOURCE_REQUIRED.format(handler.getClass().getSimpleName()));
		}
	}

	@Override
	public void disconnect(DestinationComponent<DataWithInformation<U>,D> handler) throws ComponentNotConnectedException {
		if (this.sourceComponents.containsKey(handler.getComponentId())) {
			this.sourceComponents.remove(handler.getComponentId());
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	@Override
	public void closeUpStream() throws DataHandlerCloseException {
		for (FilteringDestinationComponent<U,D> source : this.sourceComponents.values()) {
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
	public void connect(SourceComponent<DataWithInformation<U>,D> handler) throws ComponentConnectedException {
		if (this.upStreamSourceComponent == null) {
			this.upStreamSourceComponent = handler;
		} else {
			throw new ComponentConnectedException(ALREADY_CONNECTED.format());
		}
	}

	@Override
	public void disconnect(SourceComponent<DataWithInformation<U>,D> handler) throws ComponentNotConnectedException {
		if (this.upStreamSourceComponent != null
				&& this.upStreamSourceComponent.getComponentId().equals(handler.getComponentId())) {
			this.upStreamSourceComponent = null;
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}
}
