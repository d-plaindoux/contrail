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

package org.wolfgang.contrail.component.factory;

import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.flow.CannotCreateDataFlowException;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.DownStreamDataFlowFactory;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlowFactory;

/**
 * <code>Components</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class BoundComponents {

	/**
	 * Constructor
	 */
	private BoundComponents() {
		super();
	}

	public static <U, D> InitialComponent<U, D> initial(DownStreamDataFlow<D> flow) {
		return new InitialComponent<U, D>(flow);
	}

	public static <U, D> InitialComponent<U, D> initial(DownStreamDataFlowFactory<U, D> factory) throws CannotCreateDataFlowException {
		return new InitialComponent<U, D>(factory);
	}

	public static <U, D> TerminalComponent<U, D> terminal(UpStreamDataFlow<U> flow) {
		return new TerminalComponent<U, D>(flow);
	}

	public static <U, D> TerminalComponent<U, D> terminal(UpStreamDataFlowFactory<U, D> factory) throws CannotCreateDataFlowException {
		return new TerminalComponent<U, D>(factory);
	}
}
