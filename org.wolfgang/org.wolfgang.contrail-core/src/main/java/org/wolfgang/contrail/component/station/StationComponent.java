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
public class StationComponent<U, D> extends AbstractComponent implements MultipleSourceComponent<U, D>, MultipleDestinationComponent<U, D> {

	/**
	 * The set of connected filtering destination component (can be empty)
	 */
	private final Map<ComponentId, SourceComponentLink<U, D>> sourceLinks;

	/**
	 * The set of connected filtering destination component (can be empty)
	 */
	private Map<ComponentId, DestinationComponentLink<U, D>> destinationLinks;

	/**
	 * 
	 */
	private final DataUpStreamStation<U> dataUpStreamFlowStation;
	private final DataDownStreamStation<D> dataDownStreamFlowStation;

	{
		this.sourceLinks = new HashMap<ComponentId, SourceComponentLink<U, D>>();
		this.destinationLinks = new HashMap<ComponentId, DestinationComponentLink<U, D>>();
	}

	/**
	 * Constructor
	 */
	public StationComponent() {
		this.dataUpStreamFlowStation = new DataUpStreamStation<U>(this);
		this.dataDownStreamFlowStation = new DataDownStreamStation<D>(this);
	}

	/**
	 * Constructor
	 * 
	 * @param dataFlowStation
	 */
	protected StationComponent(DataUpStreamStation<U> dataUpStreamFlowStation, DataDownStreamStation<D> dataDownStreamFlowStation) {
		super();
		this.dataUpStreamFlowStation = dataUpStreamFlowStation;
		this.dataDownStreamFlowStation = dataDownStreamFlowStation;
	}

	@Override
	public UpStreamDataFlow<U> getUpStreamDataFlow() {
		return this.dataUpStreamFlowStation;
	}

	@Override
	public DownStreamDataFlow<D> getDownStreamDataFlow() {
		return this.dataDownStreamFlowStation;
	}

	@Override
	public void closeUpStream() throws DataFlowCloseException {
		for (DestinationComponentLink<U, D> source : this.destinationLinks.values()) {
			source.getDestinationComponent().closeUpStream();
		}
	}

	@Override
	public void closeDownStream() throws DataFlowCloseException {
		for (SourceComponentLink<U, D> source : this.sourceLinks.values()) {
			source.getSourceComponent().closeUpStream();
		}
	}

	@Override
	public boolean acceptSource(ComponentId componentId) {
		return !this.sourceLinks.containsKey(componentId) && !this.destinationLinks.containsKey(componentId);
	}

	@Override
	public ComponentLink connectSource(SourceComponentLink<U, D> handler) throws ComponentConnectionRejectedException {
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
	}

	@Override
	public boolean acceptDestination(ComponentId componentId) {
		return this.acceptSource(componentId);
	}

	@Override
	public ComponentLink connectDestination(DestinationComponentLink<U, D> handler) throws ComponentConnectionRejectedException {
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
	 * @param data
	 * @return
	 * @throws DataFlowException
	 */
	boolean handleDownStreamData(D data) throws DataFlowException {
		boolean hasBeenSent = false;

		try {
			for (SourceComponentLink<U, D> sourceComponentLink : this.sourceLinks.values()) {
				try {
					sourceComponentLink.getSourceComponent().getDownStreamDataFlow().handleData(data);
					hasBeenSent = true;
				} catch (CannotAcceptDataException consume) {
					// Ignore
				}
			}
		} catch (StopAcceptData e) {
			// Ignore
		}

		return hasBeenSent;
	}

	/**
	 * @param data
	 * @return
	 * @throws DataFlowException
	 */
	boolean handleUpStreamData(U data) throws DataFlowException {
		boolean hasBeenSent = false;

		try {
			for (DestinationComponentLink<U, D> destinationComponentLink : this.destinationLinks.values()) {
				try {
					destinationComponentLink.getDestinationComponent().getUpStreamDataFlow().handleData(data);
					hasBeenSent = true;
				} catch (CannotAcceptDataException consume) {
					// Ignore
				}
			}
		} catch (StopAcceptData e) {
			// Ignore
		}

		return hasBeenSent;
	}
}
