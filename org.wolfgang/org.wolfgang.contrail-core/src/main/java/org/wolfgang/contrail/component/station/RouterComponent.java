/* Copyright (C)2012 D. Plaindoux.
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
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.bound.AbstractSourceComponent;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.link.ComponentLink;
import org.wolfgang.contrail.link.SourceComponentLink;

/**
 * <code>StationComponent</code> is capable to manager multiple source and
 * multiple destinations.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class RouterComponent<U, D> extends AbstractSourceComponent<U, D> implements StationSourceComponent<U, D>, DestinationComponent<U, D> {

	/**
	 * The set of connected filtering destination component (can be empty)
	 */
	private final Map<ComponentId, SourceComponentLink<U, D>> sourceLinks;

	/**
	 * 
	 */
	private DataDownStreamStation<D> dataDownStreamFlowStation;

	{
		this.sourceLinks = new HashMap<ComponentId, SourceComponentLink<U, D>>();
	}

	protected void init(DataDownStreamStation<D> dataDownStreamFlowStation) {
		assert this.dataDownStreamFlowStation == null;

		this.dataDownStreamFlowStation = dataDownStreamFlowStation;
	}

	/**
	 * Constructor
	 */
	public RouterComponent() {
		this.init(new DataDownStreamStation<D>(this));
	}

	/**
	 * Constructor
	 * 
	 * @param dataFlowStation
	 */
	protected RouterComponent(DataDownStreamStation<D> dataDownStreamFlowStation) {
		super();
		this.init(dataDownStreamFlowStation);
	}

	@Override
	public UpStreamDataFlow<U> getUpStreamDataFlow() {
		return this.getDestinationComponentLink().getDestinationComponent().getUpStreamDataFlow();
	}

	@Override
	public DownStreamDataFlow<D> getDownStreamDataFlow() {
		return this.dataDownStreamFlowStation;
	}

	@Override
	public void closeDownStream() throws DataFlowCloseException {
		for (SourceComponentLink<U, D> source : this.sourceLinks.values()) {
			source.getSourceComponent().closeUpStream();
		}
	}

	@Override
	public boolean acceptSource(ComponentId componentId) {
		return !this.sourceLinks.containsKey(componentId);
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

	private void disconnectSource(ComponentId componentId) throws ComponentNotConnectedException {
		if (this.sourceLinks.containsKey(componentId)) {
			this.sourceLinks.remove(componentId);
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
	public boolean handleDownStreamData(D data) throws DataFlowException {
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
}
