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

package org.wolfgang.contrail.component.multi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.wolfgang.contrail.component.ComponentConnectedException;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.core.SourceComponentWithSingleDestination;
import org.wolfgang.contrail.component.multi.flow.MultiDownStreamDataFlow;
import org.wolfgang.contrail.flow.DataFlow;
import org.wolfgang.contrail.flow.exception.DataFlowCloseException;
import org.wolfgang.contrail.link.DisposableLink;
import org.wolfgang.contrail.link.SourceComponentLink;

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

	@Override
	public DataFlow<U> getUpStreamDataFlow() {
		try {
			return super.getUpStreamDataFlow();
		} catch (ComponentNotConnectedException e) {
			// TODO -- How this exception can be handled correctly
			return null;
		}
	}

	public Collection<SourceComponentLink<U, D>> getSourceComponentLinks() {
		return sources.values();
	}

	@Override
	public boolean acceptSource(ComponentId componentId) {
		return this.sources.containsKey(componentId);
	}

	@Override
	public DisposableLink connectSource(SourceComponentLink<U, D> handler) throws ComponentConnectionRejectedException {
		final ComponentId componentId = handler.getSourceComponent().getComponentId();
		if (this.acceptDestination(componentId)) {
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
			component.getSourceComponent().closeDownStream();
		}
	}
}
