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

import java.util.Map;

import org.wolfgang.contrail.component.ComponentConnectedException;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.MultipleDestinationComponent;
import org.wolfgang.contrail.component.MultipleSourceComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.component.multiple.factories.DeMultiplexerDataHandlerFactory;
import org.wolfgang.contrail.component.multiple.factories.MultiplexerDataHandlerFactory;
import org.wolfgang.contrail.data.DataInformationFilter;
import org.wolfgang.contrail.data.DataWithInformation;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;

/**
 * <code>RouterComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class RouterComponent<U, D> extends AbstractComponent implements
		MultipleDestinationComponent<DataWithInformation<U>, DataWithInformation<D>>, FilteredDestinationComponentSet<U>,
		MultipleSourceComponent<DataWithInformation<U>, DataWithInformation<D>>, FilteredSourceComponentSet<D> {

	/**
	 * The de-multiplexer component
	 */
	private final DeMultiplexerComponent<U, DataWithInformation<D>> deMultiplexerComponent;

	/**
	 * The multiplexer component
	 */
	private final MultiplexerComponent<DataWithInformation<U>, D> multiplexerComponent;

	/**
	 * Constructor
	 */
	public RouterComponent(DeMultiplexerDataHandlerFactory<U> deMultiplexerFactory,	MultiplexerDataHandlerFactory<D> multiplexerFactory) {
		this.deMultiplexerComponent = new DeMultiplexerComponent<U, DataWithInformation<D>>(deMultiplexerFactory);
		this.multiplexerComponent = new MultiplexerComponent<DataWithInformation<U>, D>(multiplexerFactory);
	}

	/**
	 * Constructor
	 * 
	 * @param deMultiplexerComponent
	 * @param multiplexerComponent
	 */
	public RouterComponent(DeMultiplexerComponent<U, DataWithInformation<D>> deMultiplexerComponent,
			MultiplexerComponent<DataWithInformation<U>, D> multiplexerComponent) {
		super();
		this.deMultiplexerComponent = deMultiplexerComponent;
		this.multiplexerComponent = multiplexerComponent;
	}

	@Override
	public void connect(DestinationComponent<DataWithInformation<U>, DataWithInformation<D>> handler)
			throws ComponentConnectionRejectedException {
		deMultiplexerComponent.connect(handler);
	}

	@Override
	public void disconnect(DestinationComponent<DataWithInformation<U>, DataWithInformation<D>> handler)
			throws ComponentNotConnectedException {
		deMultiplexerComponent.disconnect(handler);
	}

	@Override
	public void closeUpStream() throws DataHandlerCloseException {
		deMultiplexerComponent.closeUpStream();
	}

	@Override
	public void closeDownStream() throws DataHandlerCloseException {
		deMultiplexerComponent.closeDownStream();
	}

	@Override
	public void connect(SourceComponent<DataWithInformation<U>, DataWithInformation<D>> handler)
			throws ComponentConnectedException {
		deMultiplexerComponent.connect(handler);
	}

	@Override
	public void disconnect(SourceComponent<DataWithInformation<U>, DataWithInformation<D>> handler)
			throws ComponentNotConnectedException {
		deMultiplexerComponent.disconnect(handler);
	}

	@Override
	public Map<ComponentId, DataInformationFilter> getDestinationFilters() {
		return deMultiplexerComponent.getDestinationFilters();
	}

	@Override
	public DestinationComponent<DataWithInformation<U>, DataWithInformation<D>> getDestinationComponent(ComponentId componentId)
			throws ComponentNotConnectedException {
		return deMultiplexerComponent.getDestinationComponent(componentId);
	}

	@Override
	public UpStreamDataHandler<DataWithInformation<U>> getUpStreamDataHandler() {
		return deMultiplexerComponent.getUpStreamDataHandler();
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
		deMultiplexerComponent.filterDestination(componentId, filter);
	}

	@Override
	public DownStreamDataHandler<DataWithInformation<D>> getDownStreamDataHandler() {
		return deMultiplexerComponent.getDownStreamDataHandler();
	}

	@Override
	public Map<ComponentId, DataInformationFilter> getSourceFilters() {
		return multiplexerComponent.getSourceFilters();
	}

	@Override
	public SourceComponent<DataWithInformation<U>, DataWithInformation<D>> getSourceComponent(ComponentId componentId)
			throws ComponentNotConnectedException {
		return multiplexerComponent.getSourceComponent(componentId);
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
	public void filterSource(ComponentId componentId, DataInformationFilter filter) throws ComponentConnectedException {
		multiplexerComponent.filterSource(componentId, filter);
	}
}
