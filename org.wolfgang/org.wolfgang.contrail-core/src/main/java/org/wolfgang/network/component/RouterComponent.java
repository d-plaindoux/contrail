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

package org.wolfgang.network.component;

import java.util.ArrayList;
import java.util.List;

import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.Components;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.multi.MultiSourceComponent;
import org.wolfgang.contrail.flow.DataFlow;
import org.wolfgang.contrail.flow.exception.DataFlowCloseException;
import org.wolfgang.contrail.network.route.RouteTable;
import org.wolfgang.network.component.flow.router.RouterDownStreamComponentDataFlow;
import org.wolfgang.network.packet.Packet;

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
	public void closeDownStream() throws DataFlowCloseException {
		// TODO
	}

	public ClientComponent addActiveRoute(SourceComponent<Packet, Packet> sourceComponent, String endPoint) throws ComponentConnectionRejectedException {
		final ClientComponent clientComponent = new ClientComponent(endPoint);
		Components.compose(sourceComponent, clientComponent, this);
		this.activeRoutes.add(clientComponent);
		return clientComponent;
	}

	public boolean hasActiveRoute(String destinationId, String endPoint) {

		for (ClientComponent route : activeRoutes) {
			if (route.acceptDestinationId(destinationId)) {
				return true;
			} else if (endPoint != null && endPoint.equals(route.getEndPoint())) {
				return true;
			}
		}

		return false;
	}

	public ClientComponent getActiveRoute(String destinationId, String endPoint) {

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
