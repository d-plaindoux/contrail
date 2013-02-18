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
import org.wolfgang.actor.event.Request;
import org.wolfgang.actor.event.Response;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.flow.exception.DataFlowException;
import org.wolfgang.network.packet.Packet;

/**
 * <code>RemoteActorHandler</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class RemoteActorHandler {

	private final CoordinatorComponent component;

	private RemoteActorHandler(CoordinatorComponent component) {
		super();
		this.component = component;
	}

	public void handle(String location, Request request, Response response) {
		final String responseId;

		if (response != null) {
			responseId = component.createResponseId(response);
		} else {
			responseId = null;
		}

		final Packet packet = new Packet(location, request.toActor(location, responseId));

		try {
			this.component.getDownStreamDataFlow().handleData(packet);
		} catch (ComponentNotConnectedException e) {
			// TODO
		} catch (DataFlowException e) {
			// TODO
		}
	}

}
