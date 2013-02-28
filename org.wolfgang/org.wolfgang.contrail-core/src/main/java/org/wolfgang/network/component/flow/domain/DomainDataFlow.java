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

package org.wolfgang.network.component.flow.domain;

import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.flow.DataFlow;
import org.wolfgang.contrail.flow.exception.DataFlowException;
import org.wolfgang.network.component.DomainComponent;
import org.wolfgang.network.packet.Packet;

/**
 * <code>RouterDataFlow</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
abstract class DomainDataFlow implements DataFlow<Packet> {

	protected final DomainComponent component;

	public DomainDataFlow(DomainComponent component) {
		this.component = component;
	}

	public void handleData(Packet data) throws DataFlowException {
		try {
			if (data.getSourceId() == null) {
				data.setSourceId(this.component.getIdentifier());
			}
			
			if (this.component.getIdentifier().equals(data.getDestinationId())) {
				this.component.getDestinationComponentLink().getDestinationComponent().getUpStreamDataFlow().handleData(data);
			} else {
				this.component.getSourceComponentLink().getSourceComponent().getDownStreamDataFlow().handleData(data);
			}
		} catch (ComponentNotConnectedException e) {
			throw new DataFlowException(e);
		}
	}
}
