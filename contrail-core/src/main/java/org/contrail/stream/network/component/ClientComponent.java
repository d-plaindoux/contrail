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

package org.contrail.stream.network.component;

import java.util.ArrayList;
import java.util.List;

import org.contrail.stream.component.ComponentNotConnectedException;
import org.contrail.stream.component.pipeline.AbstractPipelineComponent;
import org.contrail.stream.flow.DataFlow;
import org.contrail.stream.network.component.flow.client.ClientDownStreamDataFlow;
import org.contrail.stream.network.component.flow.client.ClientUpStreamDataFlow;
import org.contrail.stream.network.packet.Packet;

/**
 * <code>ClientComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ClientComponent extends AbstractPipelineComponent<Packet, Packet, Packet, Packet> {

	private final String endPoint;
	private final DataFlow<Packet> upStreamDataFlow;
	private final DataFlow<Packet> downStreamDataFlow;
	private final List<String> destinations;
	private final ArrayList<String> sources;

	public ClientComponent(String endPoint) {
		this.endPoint = endPoint;

		this.upStreamDataFlow = new ClientUpStreamDataFlow(this);
		this.downStreamDataFlow = new ClientDownStreamDataFlow(this);

		this.destinations = new ArrayList<String>();
		this.sources = new ArrayList<String>();
	}

	public String getEndPoint() {
		return endPoint;
	}

	public synchronized void addSourceId(String sourceId) {
		if (!this.acceptSourceId(sourceId)) {
			this.sources.add(sourceId);
		}
	}

	public synchronized boolean acceptSourceId(String identifier) {
		return this.sources.contains(identifier);
	}

	public synchronized void addDestinationId(String identifier) {
		if (!this.acceptDestinationId(identifier)) {
			this.destinations.add(identifier);
		}
	}

	public boolean acceptDestinationId(String identifier) {
		return this.destinations.contains(identifier);
	}

	@Override
	public DataFlow<Packet> getUpStreamDataFlow() throws ComponentNotConnectedException {
		return this.upStreamDataFlow;
	}

	@Override
	public DataFlow<Packet> getDownStreamDataFlow() throws ComponentNotConnectedException {
		return this.downStreamDataFlow;
	}
}
