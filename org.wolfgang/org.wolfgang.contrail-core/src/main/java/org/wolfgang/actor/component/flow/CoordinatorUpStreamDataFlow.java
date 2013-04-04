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

package org.wolfgang.actor.component.flow;

import static org.wolfgang.actor.common.Keywords.*;

import org.wolfgang.actor.component.CoordinatorComponent;
import org.wolfgang.actor.component.handler.RemoteActorHandler;
import org.wolfgang.actor.component.handler.RemoteResponseHandler;
import org.wolfgang.actor.core.ActorException;
import org.wolfgang.actor.core.Coordinator;
import org.wolfgang.actor.event.Request;
import org.wolfgang.actor.event.Response;
import org.wolfgang.common.utils.Coercion;
import org.wolfgang.contrail.data.ObjectRecord;
import org.wolfgang.contrail.flow.DataFlow;
import org.wolfgang.contrail.flow.DataFlowAdapter;
import org.wolfgang.contrail.flow.exception.DataFlowCloseException;
import org.wolfgang.contrail.flow.exception.DataFlowException;
import org.wolfgang.network.packet.Packet;

/**
 * <code>CoordinatorUpStreamDataFlow</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class CoordinatorUpStreamDataFlow extends DataFlowAdapter<Packet> implements DataFlow<Packet> {

	private static final String RESPONSE = "response";
	
	private final Coordinator coordinator;
	private final CoordinatorComponent component;

	public CoordinatorUpStreamDataFlow(Coordinator coordinator, CoordinatorComponent component) {
		super();
		this.coordinator = coordinator;
		this.component = component;

		this.coordinator.setRemoteActorHandler(new RemoteActorHandler(this.component));
	}

	@Override
	public void handleData(Packet data) throws DataFlowException {
		if (ActorInteractionFilter.isAnActorRequest(data.getData())) {
			final ObjectRecord record = Coercion.coerce(data.getData(), ObjectRecord.class);
			final RemoteResponseHandler response;
			if (record.get(RESPONSE) != null) {
				response = new RemoteResponseHandler(component, data.getSourceId(), record.get(RESPONSE, String.class));
			} else {
				response = null;
			}
			this.coordinator.send(record.get(IDENTIFIER, String.class), record.get(REQUEST, Request.class), response);
		} else if (ActorInteractionFilter.isAnActorResponse(data.getData())) {
			final ObjectRecord record = Coercion.coerce(data.getData(), ObjectRecord.class);
			final Response response = this.component.retrieveResponseById(record.get(IDENTIFIER, String.class));
			if (record.get(TYPE, Integer.class) == 0x01) {
				response.success(record.get(VALUE, Object.class));
			} else {
				response.failure(record.get(VALUE, ActorException.class));
			}
		}
	}

	@Override
	public void handleClose() throws DataFlowCloseException {
		// Nothing to do for the moment
	}

}
