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

package org.wolfgang.actor.component.handler;

import org.wolfgang.actor.component.CoordinatorComponent;
import org.wolfgang.actor.core.ActorException;
import org.wolfgang.actor.event.Response;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.data.ObjectRecord;
import org.wolfgang.contrail.flow.exception.DataFlowException;
import org.wolfgang.network.packet.Packet;

/**
 * <code>ResponseHandler</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class RemoteResponseHandler implements Response {

	final private CoordinatorComponent component;
	final private String location;
	final private String identifier;

	public RemoteResponseHandler(CoordinatorComponent component, String location, String identifier) {
		this.component = component;
		this.location = location;
		this.identifier = identifier;
	}

	@Override
	public void success(Object value) {
		this.sendPacket(0x01, value);
	}

	@Override
	public void failure(ActorException error) {
		this.sendPacket(0x02, error);
	}

	private void sendPacket(int type, Object object) {
		try {
			final ObjectRecord data = new ObjectRecord().set("identifier", identifier).set("type", type).set("value", object);
			final Packet packet = new Packet(location, data);

			component.getDownStreamDataFlow().handleData(packet);
		} catch (ComponentNotConnectedException e) {
			// TODO
		} catch (DataFlowException e) {
			// TODO
		}
	}
}
