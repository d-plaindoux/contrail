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

package org.wolfgang.contrail.network.component.flow;

import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.flow.DataFlow;
import org.wolfgang.contrail.flow.exception.DataFlowException;
import org.wolfgang.contrail.network.component.RouteComponent;
import org.wolfgang.contrail.network.packet.Packet;
import org.wolfgang.contrail.network.route.RouteNotFoundException;

/**
 * <code>RouterDataFlow</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
abstract class RouterDataFlow implements DataFlow<Packet> {

	protected final RouteComponent router;

	public RouterDataFlow(RouteComponent router) {
		this.router = router;
	}

	public void handleData(Packet data) throws DataFlowException {
		try {
			if (this.router.getIdentifier().equals(data.getDestinationId())) {
				this.router.getDestinationComponentLink().getDestinationComponent().getUpStreamDataFlow().handleData(data);
			} else {
				final Packet newData = data.sendTo(this.router.getRouteTable().getRoute(data.getDestinationId()));
				this.router.getSourceComponentLink().getSourceComponent().getDownStreamDataFlow().handleData(newData);
			}
		} catch (ComponentNotConnectedException e) {
			throw new DataFlowException(e);
		} catch (RouteNotFoundException e) {
			throw new DataFlowException(e);
		}
	}
}
