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

package org.contrail.actor.component.handler;

import static org.contrail.actor.common.Keywords.*;

import org.contrail.actor.component.CoordinatorComponent;
import org.contrail.actor.core.ActorException;
import org.contrail.actor.event.Response;
import org.contrail.stream.component.ComponentNotConnectedException;
import org.contrail.stream.data.ObjectRecord;
import org.contrail.stream.flow.exception.DataFlowException;
import org.contrail.stream.network.packet.Packet;

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
			final ObjectRecord data = new ObjectRecord().set(IDENTIFIER, identifier).set(TYPE, type).set(VALUE, object);
			final Packet packet = new Packet(location, data);

			component.getDownStreamDataFlow().handleData(packet);
		} catch (ComponentNotConnectedException e) {
			// TODO
		} catch (DataFlowException e) {
			// TODO
		}
	}
}
