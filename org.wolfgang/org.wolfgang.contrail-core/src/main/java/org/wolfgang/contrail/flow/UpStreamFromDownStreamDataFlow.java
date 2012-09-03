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

package org.wolfgang.contrail.flow;

/**
 * <code>DownStreamDataHandlerAdapter</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class UpStreamFromDownStreamDataFlow<D> implements UpStreamDataFlow<D> {
	private final DownStreamDataFlow<D> dataFlow;

	/**
	 * Constructor
	 * 
	 * @param dataFlow
	 */
	UpStreamFromDownStreamDataFlow(DownStreamDataFlow<D> dataFlow) {
		this.dataFlow = dataFlow;
	}

	@Override
	public void handleData(D data) throws DataFlowException {
		dataFlow.handleData(data);
	}

	@Override
	public void handleClose() throws DataFlowCloseException {
		dataFlow.handleClose();
	}

	@Override
	public void handleLost() throws DataFlowCloseException {
		dataFlow.handleLost();
	}
}
