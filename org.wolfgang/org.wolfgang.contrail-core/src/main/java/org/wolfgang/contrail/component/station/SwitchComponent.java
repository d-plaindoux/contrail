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
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.bound.AbstractDestinationComponent;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.link.ComponentLink;
import org.wolfgang.contrail.link.DestinationComponentLink;

/**
 * <code>StationComponent</code> is capable to manager multiple source and
 * multiple destinations.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class SwitchComponent<U, D> extends AbstractDestinationComponent<U, D> implements StationDestinationComponent<U, D>, SourceComponent<U, D> {

	/**
	 * The set of connected filtering destination component (can be empty)
	 */
	private Map<ComponentId, DestinationComponentLink<U, D>> destinationLinks;

	/**
	 * 
	 */
	private DataUpStreamStation<U> dataUpStreamFlowStation;

	{
		this.destinationLinks = new HashMap<ComponentId, DestinationComponentLink<U, D>>();
	}

	protected void init(DataUpStreamStation<U> dataUpStreamFlowStation) {
		assert this.dataUpStreamFlowStation == null;

		this.dataUpStreamFlowStation = dataUpStreamFlowStation;
	}

	/**
	 * Constructor
	 */
	public SwitchComponent() {
		this.init(new DataUpStreamStation<U>(this));
	}

	/**
	 * Constructor
	 * 
	 * @param dataFlowStation
	 */
	protected SwitchComponent(DataUpStreamStation<U> dataUpStreamFlowStation) {
		super();
		this.init(dataUpStreamFlowStation);
	}

	@Override
	public UpStreamDataFlow<U> getUpStreamDataFlow() {
		return this.dataUpStreamFlowStation;
	}

	@Override
	public DownStreamDataFlow<D> getDownStreamDataFlow() {
		return this.getSourceComponentLink().getSourceComponent().getDownStreamDataFlow();
	}

	@Override
	public void closeUpStream() throws DataFlowCloseException {
		for (DestinationComponentLink<U, D> source : this.destinationLinks.values()) {
			source.getDestinationComponent().closeUpStream();
		}
	}

	@Override
	public boolean acceptDestination(ComponentId componentId) {
		return !this.destinationLinks.containsKey(componentId);
	}

	@Override
	public ComponentLink connectDestination(DestinationComponentLink<U, D> handler) throws ComponentConnectionRejectedException {
		final ComponentId componentId = handler.getDestinationComponent().getComponentId();
		if (this.acceptDestination(componentId)) {
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
		} else {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		}
	}

	/**
	 * @param data
	 * @return
	 * @throws DataFlowException
	 */
	@Override
	public boolean handleUpStreamData(U data) throws DataFlowException {
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
