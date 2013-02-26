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

package org.wolfgang.network.component;

import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.pipeline.AbstractPipelineComponent;
import org.wolfgang.contrail.flow.DataFlow;
import org.wolfgang.network.component.flow.TargetSelectorDownStreamDataFlow;
import org.wolfgang.network.component.flow.TargetSelectorUpStreamDataFlow;
import org.wolfgang.network.packet.Packet;

/**
 * <code>RouteComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TargetSelectorComponent extends AbstractPipelineComponent<Packet, Packet, Packet, Packet> {

	private final String identifier;
	private final DataFlow<Packet> upStreamDataFlow;
	private final DataFlow<Packet> downStreamDataFlow;

	public TargetSelectorComponent(String identifier) {
		this.identifier = identifier;

		this.upStreamDataFlow = new TargetSelectorUpStreamDataFlow(this);
		this.downStreamDataFlow = new TargetSelectorDownStreamDataFlow(this);
	}

	public String getIdentifier() {
		return identifier;
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
