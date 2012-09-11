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

package org.wolfgang.contrail.component.reverse;

import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.bound.AbstractSourceComponent;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DataFlows;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.link.ComponentLinkManager;

/**
 * <code>ReversedPipelIneComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ReversedDestinationComponent<U, D> extends AbstractSourceComponent<U, D> {

	private final InitialComponent<D, U> initialComponent;

	/**
	 * Constructor
	 * 
	 * @param component
	 * @throws ComponentConnectionRejectedException
	 */
	public ReversedDestinationComponent(ComponentLinkManager manager, DestinationComponent<D, U> component) throws ComponentConnectionRejectedException {
		super();

		this.initialComponent = new InitialComponent<D, U>(DataFlows.closable(new DownStreamDataFlow<U>() {
			@Override
			public void handleData(U data) throws DataFlowException {
				getDestinationComponentLink().getDestinationComponent().getUpStreamDataFlow().handleData(data);
			}

			@Override
			public void handleClose() throws DataFlowCloseException {
				// TODO
			}
		}));

		manager.connect(initialComponent, component);
	}

	@Override
	public DownStreamDataFlow<D> getDownStreamDataFlow() {
		try {
			return DataFlows.reverse(this.initialComponent.getUpStreamDataFlow());
		} catch (ComponentNotConnectedException e) {
			throw new IllegalAccessError();
		}
	}

	@Override
	public void closeDownStream() throws DataFlowCloseException {
		// TODO
	}
}
