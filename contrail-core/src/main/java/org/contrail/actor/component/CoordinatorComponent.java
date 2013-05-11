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

package org.contrail.actor.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.contrail.actor.component.flow.ActorInteractionFilter;
import org.contrail.actor.component.flow.CoordinatorDownStreamDataFlow;
import org.contrail.actor.component.flow.CoordinatorUpStreamDataFlow;
import org.contrail.actor.core.ActorException;
import org.contrail.actor.core.Coordinator;
import org.contrail.actor.event.Request;
import org.contrail.actor.event.Response;
import org.contrail.stream.codec.object.Decoder;
import org.contrail.stream.codec.object.Encoder;
import org.contrail.stream.component.ComponentNotConnectedException;
import org.contrail.stream.component.core.DestinationComponentWithSingleSource;
import org.contrail.stream.data.JSonifier;
import org.contrail.stream.flow.DataFlow;
import org.contrail.stream.flow.DataFlowFactory;
import org.contrail.stream.flow.FilteredDataFlow;
import org.contrail.stream.flow.exception.DataFlowCloseException;
import org.contrail.stream.network.packet.Packet;

/**
 * <code>CoordinatorComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class CoordinatorComponent extends DestinationComponentWithSingleSource<Packet, Packet> {

	private final Map<String, Response> pendingResponses;
	private final String domainId;
	private final DataFlow<Packet> upStreamDataFlow;
	private final DataFlow<Packet> downStreamDataFlow;
	private final List<JSonifier> allJSonifiers;
	
	private Encoder encoder;
	private Decoder decoder;

	{
		this.pendingResponses = new HashMap<String, Response>();
		this.allJSonifiers = new ArrayList<JSonifier>();
	}

	public CoordinatorComponent(Coordinator coordinator, String domainName) {
		super();

		final FilteredDataFlow.Filter<Packet> filter = new FilteredDataFlow.Filter<Packet>() {
			@Override
			public boolean accept(Packet data) {
				return ActorInteractionFilter.isAnActorRequest(data.getData()) || ActorInteractionFilter.isAnActorResponse(data.getData());
			}
		};

		this.domainId = domainName;
		this.upStreamDataFlow = DataFlowFactory.filtered(filter, new CoordinatorUpStreamDataFlow(coordinator, this));
		this.downStreamDataFlow = new CoordinatorDownStreamDataFlow(this);
		
		this.addJSonifiers(Request.jSonifable(), ActorException.jSonifable());
	}

	public String getDomainId() {
		return domainId;
	}

	public synchronized void addJSonifiers(JSonifier...jSonifiers) {
		for(JSonifier jSonifier : jSonifiers) {
			if (!this.allJSonifiers.contains(jSonifier)) {
				this.allJSonifiers.add(jSonifier);
			}
		}
		
		this.encoder = new Encoder(allJSonifiers);
		this.decoder = new Decoder(allJSonifiers);
	}
	
	public synchronized Encoder getEncoder() {
		return encoder;
	}

	public synchronized Decoder getDecoder() {
		return decoder;
	}

	@Override
	public DataFlow<Packet> getUpStreamDataFlow() throws ComponentNotConnectedException {
		return this.upStreamDataFlow;
	}

	@Override
	public DataFlow<Packet> getDownStreamDataFlow() throws ComponentNotConnectedException {
		return this.downStreamDataFlow;
	}

	@Override
	public void closeUpStream() throws DataFlowCloseException {
		// TODO
	}

	public String createResponseId(Response response) {
		final String identifier = UUID.randomUUID().toString();
		synchronized (this.pendingResponses) {
			this.pendingResponses.put(identifier, response);
		}
		return identifier;
	}

	public Response retrieveResponseById(String identifier) {
		synchronized (this.pendingResponses) {
			return this.pendingResponses.remove(identifier);
		}
	}
}
