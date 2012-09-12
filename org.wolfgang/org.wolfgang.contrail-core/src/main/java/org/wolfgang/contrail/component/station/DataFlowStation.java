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

package org.wolfgang.contrail.component.station;

import org.wolfgang.common.message.Message;
import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlow;

/**
 * A <code>AbstractDataHandlerStation</code> is able to manage information using
 * filters owned buy each filtered upstream destination component linked to the
 * multiplexer component. The data is a network event in this case and the
 * management is done using a dedicated network router.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class DataFlowStation<U> implements DownStreamDataFlow<U>, UpStreamDataFlow<U> {

	/**
	 * Private message
	 */
	private static final Message NOT_FOUND;

	static {
		NOT_FOUND = MessagesProvider.message("org/wolfgang/contrail/message", "data.not.sent");
	}

	/**
	 * The component in charge of managing this multiplexer
	 */
	private final StationComponent<U> component;

	/**
	 * Constructor
	 * 
	 * @param component
	 * @param selfReference
	 * @param routerTable
	 */
	public DataFlowStation(StationComponent<U> component) {
		super();
		this.component = component;
	}

	@Override
	public void handleData(U data) throws DataFlowException {
		if (!this.component.handleData(data)) {
			throw new DataFlowException(NOT_FOUND.format());
		}
	}

	@Override
	public void handleClose() throws DataFlowCloseException {
		component.closeDownStream();
		component.closeUpStream();
	}
}
