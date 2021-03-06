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

package org.contrail.actor.component.flow;

import org.contrail.actor.component.CoordinatorComponent;
import org.contrail.stream.component.ComponentNotConnectedException;
import org.contrail.stream.component.pipeline.transducer.DataTransducerException;
import org.contrail.stream.flow.DataFlow;
import org.contrail.stream.flow.DataFlowAdapter;
import org.contrail.stream.flow.exception.DataFlowCloseException;
import org.contrail.stream.flow.exception.DataFlowException;
import org.contrail.stream.network.packet.Packet;

/**
 * <code>CoordinatorUpStreamDataFlow</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class CoordinatorDownStreamDataFlow extends DataFlowAdapter<Packet> implements DataFlow<Packet> {

	private final CoordinatorComponent component;

	public CoordinatorDownStreamDataFlow(CoordinatorComponent component) {
		super();
		this.component = component;
	}

	@Override
	public void handleData(Packet packet) throws DataFlowException {
		
		try {
			packet.setData(this.component.getEncoder().encode(packet.getData()));
			this.component.getSourceComponentLink().getSourceComponent().getDownStreamDataFlow().handleData(packet);
		} catch (DataTransducerException e) {
			throw new DataFlowException(e);
		} catch (ComponentNotConnectedException e) {
			throw new DataFlowException(e);
		}
	}

	@Override
	public void handleClose() throws DataFlowCloseException {
		// Nothing to do for the moment
	}

}
