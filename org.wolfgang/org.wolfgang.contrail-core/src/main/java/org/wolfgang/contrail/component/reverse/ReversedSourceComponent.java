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
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.bound.AbstractDestinationComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DataFlows;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.link.ComponentLinkManager;

/**
 * <code>ReversedPipelIneComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ReversedSourceComponent<U, D> extends AbstractDestinationComponent<U, D> {

	private final TerminalComponent<D, U> terminalComponent;

	/**
	 * Constructor
	 * 
	 * @param component
	 * @throws ComponentConnectionRejectedException
	 */
	public ReversedSourceComponent(ComponentLinkManager manager, SourceComponent<D, U> component) throws ComponentConnectionRejectedException {
		super();

		this.terminalComponent = new TerminalComponent<D, U>(DataFlows.closable(new UpStreamDataFlow<D>() {

			@Override
			public void handleData(D data) throws DataFlowException {
				getSourceComponentLink().getSourceComponent().getDownStreamDataFlow().handleData(data);
			}

			@Override
			public void handleClose() throws DataFlowCloseException {
				// TODO
			}
		}));

		manager.connect(component, terminalComponent);
	}

	@Override
	public UpStreamDataFlow<U> getUpStreamDataFlow() {
		try {
			return DataFlows.reverse(this.terminalComponent.getDownStreamDataHandler());
		} catch (ComponentNotConnectedException e) {
			throw new IllegalAccessError();
		}
	}

	@Override
	public void closeUpStream() throws DataFlowCloseException {
		// TODO
	}
}
