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

package org.contrail.stream.network.component;

import java.util.ArrayList;
import java.util.List;

import org.contrail.stream.component.ComponentConnectionRejectedException;
import org.contrail.stream.component.ComponentDisconnectionRejectedException;
import org.contrail.stream.component.Components;
import org.contrail.stream.component.SourceComponent;
import org.contrail.stream.component.multi.MultiSourceComponent;
import org.contrail.stream.flow.DataFlow;
import org.contrail.stream.flow.exception.DataFlowCloseException;
import org.contrail.stream.network.component.flow.router.RouterDownStreamComponentDataFlow;
import org.contrail.stream.network.packet.Packet;
import org.contrail.stream.network.route.RouteTable;

/**
 * <code>RouterComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class RouterComponent extends MultiSourceComponent<Packet, Packet> {

	private final RouteTable table;
	private final DataFlow<Packet> downStreamDataFlow;
	private final List<ClientComponent> activeRoutes;

	public RouterComponent(RouteTable table) {
		super();

		this.table = table;
		this.downStreamDataFlow = new RouterDownStreamComponentDataFlow(this);
		this.activeRoutes = new ArrayList<ClientComponent>();
	}

	public RouteTable getRouteTable() {
		return table;
	}

	@Override
	public DataFlow<Packet> getDownStreamDataFlow() {
		return this.downStreamDataFlow;
	}

	@Override
	public void closeUpStream() throws DataFlowCloseException {
		// Do nothing
	}

	public ClientComponent addActiveRoute(SourceComponent<Packet, Packet> sourceComponent, final String endPoint) throws ComponentConnectionRejectedException {
		final ClientComponent clientComponent = new ClientComponent(endPoint) {
			@Override
			public void closeUpStream() throws DataFlowCloseException {
				removeActiveRoute(this);
				try {
					super.closeUpStream();
				} finally {
					try {
						this.getDestinationComponentLink().dispose();
					} catch (ComponentDisconnectionRejectedException e) {
						throw new DataFlowCloseException(e);
					}
				}
			}
		};
		
		Components.compose(sourceComponent, clientComponent, this);		
		this.activeRoutes.add(clientComponent);
		
		return clientComponent;
	}

	private void removeActiveRoute(ClientComponent clientComponent) {
		synchronized (this.activeRoutes) {
			this.activeRoutes.remove(clientComponent);
		}
	}

	public boolean hasActiveRoute(String destinationId, String endPoint) {
		synchronized (this.activeRoutes) {
			for (ClientComponent route : activeRoutes) {
				if (route.acceptDestinationId(destinationId)) {
					return true;
				} else if (endPoint != null && endPoint.equals(route.getEndPoint())) {
					return true;
				}
			}
			return false;
		}

	}

	public synchronized ClientComponent getActiveRoute(String destinationId, String endPoint) {
		synchronized (this.activeRoutes) {
			for (ClientComponent route : activeRoutes) {
				if (route.acceptDestinationId(destinationId)) {
					return route;
				} else if (endPoint != null && endPoint.equals(route.getEndPoint())) {
					return route;
				}
			}
			return null;
		}
	}
}
