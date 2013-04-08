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

package org.contrail.stream.component.bound;

import org.contrail.stream.component.ComponentDataFlowFactory;
import org.contrail.stream.component.ComponentNotConnectedException;
import org.contrail.stream.component.bound.flow.TerminalDataFlow;
import org.contrail.stream.component.core.DestinationComponentWithSingleSource;
import org.contrail.stream.flow.DataFlow;
import org.contrail.stream.flow.exception.CannotCreateDataFlowException;
import org.contrail.stream.flow.exception.DataFlowCloseException;
import org.contrail.stream.link.ComponentLinkFactory;

/**
 * The <code>TerminalComponent</code> is capable to receive incoming events.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TerminalComponent<U, D> extends DestinationComponentWithSingleSource<U, D> {

	/**
	 * The internal down stream data handler
	 */
	private final DataFlow<U> upstreamDataHandler;

	/**
	 * Constructor
	 * 
	 * @param receiver
	 *            The terminal data receiver
	 */
	public TerminalComponent(final DataFlow<U> receiver) {
		super();

		this.upstreamDataHandler = receiver;
	}

	/**
	 * Constructor
	 * 
	 * @param receiver
	 *            The terminal data receiver
	 * @throws CannotCreateDataFlowException
	 */
	public TerminalComponent(final ComponentDataFlowFactory<D, U> receiver) throws CannotCreateDataFlowException {
		super();

		this.upstreamDataHandler = receiver.create(TerminalDataFlow.<D> create(this));
	}

	@Override
	public DataFlow<U> getUpStreamDataFlow() {
		return this.upstreamDataHandler;
	}

	/**
	 * Provides the data channel used for down stream communication facility
	 * 
	 * @return a DataFlow (never <code>null</code>)
	 * @throws ComponentNotConnectedException
	 *             thrown if the handler is not yet available
	 */
	public DataFlow<D> getDataFlow() throws ComponentNotConnectedException {
		if (ComponentLinkFactory.isUndefined(this.getSourceComponentLink())) {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		} else {
			return this.getSourceComponentLink().getSourceComponent().getDownStreamDataFlow();
		}
	}

	@Override
	public void closeUpStream() throws DataFlowCloseException {
		this.upstreamDataHandler.handleClose();
	}
}
