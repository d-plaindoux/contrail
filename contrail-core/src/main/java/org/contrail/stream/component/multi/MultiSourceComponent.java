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
import org.contrail.stream.component.DestinationComponent;
import org.contrail.stream.component.core.SourceComponentWithSingleDestination;
import org.contrail.stream.component.multi.flow.MultiDownStreamDataFlow;
import org.contrail.stream.flow.DataFlow;
import org.contrail.stream.flow.exception.DataFlowCloseException;
import org.contrail.stream.link.DisposableLink;
import org.contrail.stream.link.SourceComponentLink;

/**
 * <code>MultiDestinationComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class MultiSourceComponent<U, D> extends SourceComponentWithSingleDestination<U, D> implements DestinationComponent<U, D> {

	private final Map<ComponentId, SourceComponentLink<U, D>> sources;
	private final DataFlow<D> downStreamDataFlow;

	{
		this.sources = new HashMap<ComponentId, SourceComponentLink<U, D>>();
		this.downStreamDataFlow = new MultiDownStreamDataFlow<D>(this);
	}

	@Override
	public DataFlow<D> getDownStreamDataFlow() {
		return this.downStreamDataFlow;
	}

	public Collection<SourceComponentLink<U, D>> getSourceComponentLinks() {
		return sources.values();
	}

	@Override
	public boolean acceptSource(ComponentId componentId) {
		return !this.sources.containsKey(componentId);
	}

	@Override
	public DisposableLink connectSource(SourceComponentLink<U, D> handler) throws ComponentConnectionRejectedException {
		final ComponentId componentId = handler.getSourceComponent().getComponentId();
		if (this.acceptSource(componentId)) {
			this.sources.put(componentId, handler);
			return new DisposableLink() {
				@Override
				public void dispose() throws ComponentDisconnectionRejectedException {
					if (sources.remove(componentId) == null) {
						throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
					}
				}
			};

		} else {
			throw new ComponentConnectedException(ALREADY_CONNECTED.format());
		}
	}

	@Override
	public void closeDownStream() throws DataFlowCloseException {
		for (SourceComponentLink<U, D> component : getSourceComponentLinks()) {
			try {
				component.getSourceComponent().closeDownStream();
			} catch (DataFlowCloseException ignore) {
				// Consume
			}
		}
	}
}
