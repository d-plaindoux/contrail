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

package org.wolfgang.contrail.component.bound.flow;

import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.flow.DataFlow;
import org.wolfgang.contrail.flow.DataFlowAdapter;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DataFlows;

/**
 * <code>InitialDataFlow</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class InitialDataFlow<U> extends DataFlowAdapter<U> {

	private final InitialComponent<U, ?> component;

	/**
	 * Method called when a upstream must be created for a given initial
	 * component
	 * 
	 * @param component
	 *            The component
	 * @return
	 */
	public static <U> DataFlow<U> create(InitialComponent<U, ?> component) {
		return DataFlows.<U> closable(new InitialDataFlow<U>(component));
	}

	/**
	 * Constructor
	 * 
	 * @param component
	 */
	private InitialDataFlow(InitialComponent<U, ?> component) {
		super();
		this.component = component;
	}

	@Override
	public void handleData(U data) throws DataFlowException {
		try {
			this.component.getUpStreamDataFlow().handleData(data);
		} catch (ComponentNotConnectedException e) {
			throw new DataFlowException(e);
		}

	}

	@Override
	public void handleClose() throws DataFlowCloseException {
		this.component.closeUpStream();
	}
}
