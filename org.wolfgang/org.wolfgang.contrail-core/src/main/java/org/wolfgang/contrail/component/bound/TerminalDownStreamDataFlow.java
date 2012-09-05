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

package org.wolfgang.contrail.component.bound;

import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DataFlows;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.DownStreamDataFlowAdapter;

/**
 * <code>TerminalDownStreamDataFlow</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TerminalDownStreamDataFlow<D> extends DownStreamDataFlowAdapter<D> {

	private final TerminalComponent<?, D> component;

	/**
	 * Method called when a downstream must be created for a given initial
	 * component
	 * 
	 * @param component
	 *            The component
	 * @return
	 */
	public static <D> DownStreamDataFlow<D> create(TerminalComponent<?, D> component) {
		return DataFlows.<D> closable(new TerminalDownStreamDataFlow<D>(component));
	}

	/**
	 * Constructor
	 * 
	 * @param component
	 */
	private TerminalDownStreamDataFlow(TerminalComponent<?, D> component) {
		super();
		this.component = component;
	}

	@Override
	public void handleData(D data) throws DataFlowException {
		try {
			this.component.getDownStreamDataHandler().handleData(data);
		} catch (ComponentNotConnectedException e) {
			throw new DataFlowException(e);
		}

	}

	@Override
	public void handleClose() throws DataFlowCloseException {
		this.component.closeDownStream();
	}
}
