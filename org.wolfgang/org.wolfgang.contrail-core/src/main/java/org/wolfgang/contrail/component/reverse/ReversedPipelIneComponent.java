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
import org.wolfgang.contrail.component.PipelineComponent;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.pipeline.AbstractPipelineComponent;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DataFlows;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.link.ComponentLinkManager;

/**
 * <code>ReversedPipelIneComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ReversedPipelIneComponent<U1, D1, U2, D2> extends AbstractPipelineComponent<U1, D1, U2, D2> {

	private final InitialComponent<D2, U2> initialComponent;
	private final TerminalComponent<D1, U1> terminalComponent;

	/**
	 * Constructor
	 * 
	 * @param component
	 * @throws ComponentConnectionRejectedException
	 */
	public ReversedPipelIneComponent(ComponentLinkManager manager, PipelineComponent<D2, U2, D1, U1> component) throws ComponentConnectionRejectedException {
		super();

		this.initialComponent = new InitialComponent<D2, U2>(DataFlows.closable(new DownStreamDataFlow<U2>() {
			@Override
			public void handleData(U2 data) throws DataFlowException {
				getDestinationComponentLink().getDestinationComponent().getUpStreamDataFlow().handleData(data);
			}

			@Override
			public void handleClose() throws DataFlowCloseException {
				// TODO
			}
		}));

		this.terminalComponent = new TerminalComponent<D1, U1>(DataFlows.closable(new UpStreamDataFlow<D1>() {

			@Override
			public void handleData(D1 data) throws DataFlowException {
				getSourceComponentLink().getSourceComponent().getDownStreamDataFlow().handleData(data);
			}

			@Override
			public void handleClose() throws DataFlowCloseException {
				// TODO
			}
		}));

		manager.connect(initialComponent, component);
		manager.connect(component, terminalComponent);
	}

	@Override
	public UpStreamDataFlow<U1> getUpStreamDataFlow() {
		try {
			return DataFlows.reverse(this.terminalComponent.getDownStreamDataHandler());
		} catch (ComponentNotConnectedException e) {
			throw new IllegalAccessError();
		}
	}

	@Override
	public DownStreamDataFlow<D2> getDownStreamDataFlow() {
		try {
			return DataFlows.reverse(this.initialComponent.getUpStreamDataFlow());
		} catch (ComponentNotConnectedException e) {
			throw new IllegalAccessError();
		}
	}

	@Override
	public void closeUpStream() throws DataFlowCloseException {
		// TODO Auto-generated method stub
		super.closeUpStream();
	}

	@Override
	public void closeDownStream() throws DataFlowCloseException {
		// TODO Auto-generated method stub
		super.closeDownStream();
	}
}
