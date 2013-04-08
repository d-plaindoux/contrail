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

package org.contrail.stream.component.multi.flow;

import org.contrail.stream.component.ComponentNotConnectedException;
import org.contrail.stream.component.multi.MultiDestinationComponent;
import org.contrail.stream.flow.DataFlow;
import org.contrail.stream.flow.exception.DataFlowCloseException;
import org.contrail.stream.flow.exception.DataFlowException;
import org.contrail.stream.link.DestinationComponentLink;

/**
 * <code>MultiUpStreamDataFlow</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class MultiUpStreamDataFlow<U> implements DataFlow<U> {

	private final MultiDestinationComponent<U, ?> component;

	public MultiUpStreamDataFlow(MultiDestinationComponent<U, ?> component) {
		super();
		this.component = component;
	}

	@Override
	public void handleData(U data) throws DataFlowException {
		for (DestinationComponentLink<U, ?> link : component.getDestinationComponentLinks()) {
			try {
				link.getDestinationComponent().getUpStreamDataFlow().handleData(data);
			} catch (DataFlowException e) {
				// TODO
			} catch (ComponentNotConnectedException e) {
				// TODO
			}
		}
	}

	@Override
	public void handleClose() throws DataFlowCloseException {
		for (DestinationComponentLink<U, ?> link : component.getDestinationComponentLinks()) {
			try {
				link.getDestinationComponent().getUpStreamDataFlow().handleClose();
			} catch (DataFlowCloseException e) {
				// TODO
			} catch (ComponentNotConnectedException e) {
				// TODO
			}
		}
	}

}
