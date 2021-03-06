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
import org.contrail.stream.component.bound.flow.InitialDataFlow;
import org.contrail.stream.component.core.SourceComponentWithSingleDestination;
import org.contrail.stream.flow.DataFlow;
import org.contrail.stream.flow.exception.CannotCreateDataFlowException;
import org.contrail.stream.flow.exception.DataFlowCloseException;
import org.contrail.stream.link.ComponentLinkFactory;

/**
 * The <code>InitialComponent</code> is capable to send only event in the
 * framework.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class InitialComponent<U, D> extends SourceComponentWithSingleDestination<U, D> {

	/**
	 * The internal down stream data handler
	 */
	private final DataFlow<D> downStreamDataHandler;

	/**
	 * Constructor
	 * 
	 * @param receiver
	 *            The initial data receiver
	 */
	public InitialComponent(final DataFlow<D> receiver) {
		super();
		this.downStreamDataHandler = receiver;
	}

	/**
	 * Constructor
	 * 
	 * @param receiver
	 *            The initial data receiver
	 */
	public InitialComponent(final ComponentDataFlowFactory<U, D> receiver) throws CannotCreateDataFlowException {
		super();
		this.downStreamDataHandler = receiver.create(InitialDataFlow.<U> create(this));
	}

	/**
	 * Provides the data channel used for up stream communication facility
	 * 
	 * @return an UpStreamDataHandler (never <code>null</code>)
	 * @throws ComponentNotConnectedException
	 *             thrown if the handler is not yet available
	 */
	public DataFlow<U> getUpStreamDataFlow() throws ComponentNotConnectedException {
		if (ComponentLinkFactory.isUndefined(this.getDestinationComponentLink())) {
			throw new ComponentNotConnectedException(NOT_YET_CONNECTED.format());
		} else {
			return this.getDestinationComponentLink().getDestinationComponent().getUpStreamDataFlow();
		}
	}

	@Override
	public DataFlow<D> getDownStreamDataFlow() {
		return this.downStreamDataHandler;
	}

	@Override
	public void closeDownStream() throws DataFlowCloseException {
		this.downStreamDataHandler.handleClose();
	}
}
