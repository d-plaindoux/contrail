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

package org.contrail.stream.component.multi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.contrail.stream.component.ComponentConnectedException;
import org.contrail.stream.component.ComponentConnectionRejectedException;
import org.contrail.stream.component.ComponentDisconnectionRejectedException;
import org.contrail.stream.component.ComponentId;
import org.contrail.stream.component.ComponentNotConnectedException;
import org.contrail.stream.component.SourceComponent;
import org.contrail.stream.component.core.DestinationComponentWithSingleSource;
import org.contrail.stream.component.multi.flow.MultiUpStreamDataFlow;
import org.contrail.stream.flow.DataFlow;
import org.contrail.stream.flow.exception.DataFlowCloseException;
import org.contrail.stream.link.DestinationComponentLink;
import org.contrail.stream.link.DisposableLink;

/**
 * <code>MultiDestinationComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class MultiDestinationComponent<U, D> extends DestinationComponentWithSingleSource<U, D> implements SourceComponent<U, D> {

	private final Map<ComponentId, DestinationComponentLink<U, D>> destinations;
	private final DataFlow<U> upStreamDataFlow;

	{
		this.destinations = new HashMap<ComponentId, DestinationComponentLink<U, D>>();
		this.upStreamDataFlow = new MultiUpStreamDataFlow<U>(this);
	}

	@Override
	public DataFlow<U> getUpStreamDataFlow() {
		return this.upStreamDataFlow;
	}

	public Collection<DestinationComponentLink<U, D>> getDestinationComponentLinks() {
		return destinations.values();
	}

	@Override
	public boolean acceptDestination(ComponentId componentId) {
		return !this.destinations.containsKey(componentId);
	}

	@Override
	public DisposableLink connectDestination(DestinationComponentLink<U, D> handler) throws ComponentConnectionRejectedException {
		final ComponentId componentId = handler.getDestinationComponent().getComponentId();
		if (this.acceptDestination(componentId)) {
			this.destinations.put(componentId, handler);
			return new DisposableLink() {
				@Override
				public void dispose() throws ComponentDisconnectionRejectedException {
					if (destinations.remove(componentId) == null) {
						throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
					}
				}
			};

		} else {
			throw new ComponentConnectedException(ALREADY_CONNECTED.format());
		}
	}

	@Override
	public void closeUpStream() throws DataFlowCloseException {
		for (DestinationComponentLink<U, D> link : getDestinationComponentLinks()) {
			link.getDestinationComponent().closeUpStream();
		}
	}
}
