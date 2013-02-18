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

package org.wolfgang.actor.component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.wolfgang.actor.component.flow.ActorInteractionFilter;
import org.wolfgang.actor.component.flow.CoordinatorUpStreamDataFlow;
import org.wolfgang.actor.core.Coordinator;
import org.wolfgang.actor.event.Response;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.core.DestinationComponentWithSingleSource;
import org.wolfgang.contrail.flow.DataFlow;
import org.wolfgang.contrail.flow.DataFlowFactory;
import org.wolfgang.contrail.flow.FilteredDataFlow;
import org.wolfgang.contrail.flow.exception.DataFlowCloseException;
import org.wolfgang.network.packet.Packet;

/**
 * <code>CoordinatorComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class CoordinatorComponent extends DestinationComponentWithSingleSource<Packet, Packet> {

	private final Map<String, Response> pendingResponses;
	private final DataFlow<Packet> upStreamDataFlow;

	{
		this.pendingResponses = new HashMap<String, Response>();
	}

	public CoordinatorComponent(Coordinator coordinator) {
		super();

		final FilteredDataFlow.Filter<Packet> filter = new FilteredDataFlow.Filter<Packet>() {
			@Override
			public boolean accept(Packet data) {
				return ActorInteractionFilter.isAnActorRequest(data.getData()) || ActorInteractionFilter.isAnActorResponse(data.getData());
			}
		};

		this.upStreamDataFlow = DataFlowFactory.filtered(filter, new CoordinatorUpStreamDataFlow(coordinator, this));
	}

	@Override
	public DataFlow<Packet> getUpStreamDataFlow() throws ComponentNotConnectedException {
		return this.upStreamDataFlow;
	}

	@Override
	public void closeUpStream() throws DataFlowCloseException {
		// TODO
	}

	public String createResponseId(Response response) {
		final String identifier = UUID.randomUUID().toString();
		this.pendingResponses.put(identifier, response);
		return identifier;
	}

	public Response retrieveResponseById(String identifier) {
		// TODO - check null response
		return this.pendingResponses.remove(identifier);
	}
}
