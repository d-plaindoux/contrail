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

package org.contrail.stream.network.component.flow.router;

import org.contrail.stream.component.ComponentConnectionRejectedException;
import org.contrail.stream.component.ComponentNotConnectedException;
import org.contrail.stream.component.SourceComponent;
import org.contrail.stream.flow.DataFlow;
import org.contrail.stream.flow.exception.DataFlowCloseException;
import org.contrail.stream.flow.exception.DataFlowException;
import org.contrail.stream.network.builder.ClientBuilder;
import org.contrail.stream.network.component.RouterComponent;
import org.contrail.stream.network.packet.Packet;
import org.contrail.stream.network.route.RouteNotFoundException;

/**
 * <code>RouterDownStreamComponentDataFlow</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class RouterDownStreamComponentDataFlow implements DataFlow<Packet> {

	private final RouterComponent component;

	public RouterDownStreamComponentDataFlow(RouterComponent component) {
		super();
		this.component = component;
	}

	@Override
	public void handleData(Packet data) throws DataFlowException {
		try {
			final String destinationId = data.getDestinationId();

			ClientBuilder builder = null;
			String endPoint = null;

			if (this.component.getRouteTable().hasEntry(destinationId)) {
				try {
					builder = this.component.getRouteTable().getEntry(destinationId);
					endPoint = builder.getEndPoint();
				} catch (RouteNotFoundException e) {
					// Impossible
				}
			}

			SourceComponent<Packet, Packet> activeRoute = this.component.getActiveRoute(destinationId, endPoint);

			if (activeRoute == null) {
				if (builder != null) {
					activeRoute = this.component.addActiveRoute(builder.activate(), endPoint);
				}
			}

			activeRoute.getDownStreamDataFlow().handleData(data);
		} catch (ComponentConnectionRejectedException e) {
			throw new DataFlowException(e);
		} catch (ComponentNotConnectedException e) {
			throw new DataFlowException(e);
		}

	}

	@Override
	public void handleClose() throws DataFlowCloseException {

	}

}
