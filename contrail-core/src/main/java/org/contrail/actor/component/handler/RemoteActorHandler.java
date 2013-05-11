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

import org.contrail.actor.component.CoordinatorComponent;
import org.contrail.actor.core.ActorException;
import org.contrail.actor.event.Request;
import org.contrail.actor.event.Response;
import org.contrail.stream.component.ComponentNotConnectedException;
import org.contrail.stream.data.JSonifier;
import org.contrail.stream.flow.exception.DataFlowException;
import org.contrail.stream.network.packet.Packet;

/**
 * <code>RemoteActorHandler</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class RemoteActorHandler {

	private final CoordinatorComponent component;

	public RemoteActorHandler(CoordinatorComponent component) {
		super();
		this.component = component;
	}

	public String getDomainId() {
		return this.component.getDomainId();
	}
	
	public void addJSonifiers(JSonifier... jSonifiers) {
		this.component.addJSonifiers(jSonifiers);
	}
	
	public void handle(String location, String actorId, Request request, Response response) {
		final String responseId;

		if (response != null) {
			responseId = component.createResponseId(response);
		} else {
			responseId = null;
		}

		final Packet packet = new Packet(location, request.toActor(actorId, responseId));

		try {
			component.getDownStreamDataFlow().handleData(packet);
		} catch (ComponentNotConnectedException e) {
			if (responseId != null) {
				component.retrieveResponseById(responseId).failure(new ActorException(e.getMessage(),e));
			}
		} catch (DataFlowException e) {
			if (responseId != null) {
				component.retrieveResponseById(responseId).failure(new ActorException(e.getMessage(),e));
			}
		}
	}
}
