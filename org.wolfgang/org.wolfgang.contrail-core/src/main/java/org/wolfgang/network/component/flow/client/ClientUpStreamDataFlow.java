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

package org.wolfgang.network.component.flow.client;

import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.flow.DataFlow;
import org.wolfgang.contrail.flow.exception.DataFlowCloseException;
import org.wolfgang.contrail.flow.exception.DataFlowException;
import org.wolfgang.network.component.ClientComponent;
import org.wolfgang.network.packet.Packet;

/**
 * <code>RouterUpStreamDataFlow</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ClientUpStreamDataFlow implements DataFlow<Packet> {

	private final ClientComponent component;

	/**
	 * @param component
	 */
	public ClientUpStreamDataFlow(ClientComponent component) {
		this.component = component;

	}

	@Override
	public void handleData(Packet data) throws DataFlowException {
		try {
			component.addDestinationId(data.getSourceId());
			component.getDestinationComponentLink().getDestinationComponent().getUpStreamDataFlow().handleData(data);
		} catch (ComponentNotConnectedException e) {
			throw new DataFlowException(e);
		}
	}

	@Override
	public void handleClose() throws DataFlowCloseException {
		try {
			this.component.getSourceComponentLink().getSourceComponent().getDownStreamDataFlow().handleClose();
		} catch (ComponentNotConnectedException e) {
			throw new DataFlowCloseException(e);
		}
	}
}