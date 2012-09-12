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

package org.wolfgang.contrail.component.station;

import java.util.HashMap;
import java.util.Map;

import org.wolfgang.common.message.Message;
import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.common.utils.Coercion;
import org.wolfgang.contrail.component.ComponentConnectedException;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.MultipleDestinationComponent;
import org.wolfgang.contrail.component.MultipleSourceComponent;
import org.wolfgang.contrail.component.core.AbstractComponent;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.link.ComponentLink;
import org.wolfgang.contrail.link.DestinationComponentLink;
import org.wolfgang.contrail.link.SourceComponentLink;

/**
 * <code>StationComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class StationComponent<U> extends AbstractComponent implements MultipleSourceComponent<U, U>, MultipleDestinationComponent<U, U> {

	/**
	 * The set of connected filtering destination component (can be empty)
	 */
	private final Map<ComponentId, SourceComponentLink<U, U>> sourceLinks;

	/**
	 * The set of connected filtering destination component (can be empty)
	 */
	private Map<ComponentId, DestinationComponentLink<U, U>> destinationLinks;

	/**
	 * 
	 */
	private final DataFlowStation<U> dataFlowStation;

	{
		this.sourceLinks = new HashMap<ComponentId, SourceComponentLink<U, U>>();
		this.destinationLinks = new HashMap<ComponentId, DestinationComponentLink<U, U>>();
	}

	/**
	 * Constructor
	 */
	public StationComponent() {
		this.dataFlowStation = new DataFlowStation<U>(this);
	}

	/**
	 * Constructor
	 * 
	 * @param dataFlowStation
	 */
	protected StationComponent(DataFlowStation<U> dataFlowStation) {
		super();
		this.dataFlowStation = dataFlowStation;
	}

	@Override
	public UpStreamDataFlow<U> getUpStreamDataFlow() {
		return this.dataFlowStation;
	}

	@Override
	public DownStreamDataFlow<U> getDownStreamDataFlow() {
		return this.dataFlowStation;
	}

	@Override
	public void closeUpStream() throws DataFlowCloseException {
		for (DestinationComponentLink<U, U> source : this.destinationLinks.values()) {
			source.getDestinationComponent().closeUpStream();
		}
	}

	@Override
	public void closeDownStream() throws DataFlowCloseException {
		for (SourceComponentLink<U, U> source : this.sourceLinks.values()) {
			source.getSourceComponent().closeUpStream();
		}
	}

	@Override
	public boolean acceptSource(ComponentId componentId) {
		return !this.sourceLinks.containsKey(componentId) && !this.destinationLinks.containsKey(componentId);
	}

	@Override
	public ComponentLink connectSource(SourceComponentLink<U, U> handler) throws ComponentConnectionRejectedException {
		if (Coercion.canCoerce(handler.getSourceComponent(), IDataStreamHandler.class)) {
			final ComponentId componentId = handler.getSourceComponent().getComponentId();
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
		} else {
			final Message message = MessagesProvider.message("org/wolfgang/contrail/message", "source.not.a.filter");
			throw new ComponentConnectedException(message.format());
		}
	}

	@Override
	public boolean acceptDestination(ComponentId componentId) {
		return this.acceptSource(componentId);
	}

	@Override
	public ComponentLink connectDestination(DestinationComponentLink<U, U> handler) throws ComponentConnectionRejectedException {
		if (Coercion.canCoerce(handler.getDestinationComponent(), IDataStreamHandler.class)) {
			final ComponentId componentId = handler.getDestinationComponent().getComponentId();
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
		} else {
			final Message message = MessagesProvider.message("org/wolfgang/contrail/message", "destination.not.a.filter");
			throw new ComponentConnectedException(message.format());
		}
	}

	private void disconnectSource(ComponentId componentId) throws ComponentNotConnectedException {
		if (this.sourceLinks.containsKey(componentId)) {
			this.sourceLinks.remove(componentId);
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	private void disconnectDestination(ComponentId componentId) throws ComponentNotConnectedException {
		if (this.destinationLinks.containsKey(componentId)) {
			this.destinationLinks.remove(componentId);
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	/**
	 * Provide a component using it's identifier
	 * 
	 * @return an source component
	 * @throws ComponentNotConnectedException
	 * @throws DataFlowException
	 */
	@SuppressWarnings("unchecked")
	boolean handleData(U data) throws DataFlowException {
		boolean hasBeenSent = false;

		for (DestinationComponentLink<U, U> destinationComponentLink : this.destinationLinks.values()) {
			final IDataStreamHandler<U> handler = Coercion.coerce(destinationComponentLink.getDestinationComponent(), IDataStreamHandler.class);
			if (handler.canAccept(data)) {
				handler.accept(data);
				destinationComponentLink.getDestinationComponent().getUpStreamDataFlow().handleData(data);
				hasBeenSent = true;
			}
		}

		for (SourceComponentLink<U, U> sourceComponentLink : this.sourceLinks.values()) {
			final IDataStreamHandler<U> handler = Coercion.coerce(sourceComponentLink.getSourceComponent(), IDataStreamHandler.class);
			if (handler.canAccept(data)) {
				handler.accept(data);
				sourceComponentLink.getSourceComponent().getDownStreamDataFlow().handleData(data);
				hasBeenSent = true;
			}
		}

		return hasBeenSent;
	}
}
