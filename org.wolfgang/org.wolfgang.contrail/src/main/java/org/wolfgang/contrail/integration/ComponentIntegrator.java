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

package org.wolfgang.contrail.integration;

import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataSender;

/**
 * A <code>ComponentIntegrator</code> is able to provide components based on
 * criterion. This is the main component to be used for a network components
 * construction. Since the component is returned it must be connected.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface ComponentIntegrator {

	/**
	 * Method called whether a source component is required
	 * 
	 * @param factory
	 *            The factory used to create final glue
	 * @param upstream
	 *            The upstream data type
	 * @param downstream
	 *            The downstream data type
	 * @return a source component (Never <code>null</code>)
	 * @throws CannotProvideInitialComponentException
	 *             if no specific mechanisms are linked to the required stream
	 *             types
	 * @throws CannotIntegrateInitialComponentException
	 *             if the component integration fails
	 */
	<U, D> DataSender<U> createInitial(DataReceiver<D> receiver, Class<U> upstream, Class<D> downstream)
			throws CannotProvideInitialComponentException, CannotIntegrateInitialComponentException;

	/**
	 * Method called whether a destination component is required
	 * 
	 * @param factory
	 *            The factory used to create final glue
	 * @param upstream
	 *            The upstream data type
	 * @param downstream
	 *            The downstream data type
	 * @return a destination component (Never <code>null</code>)
	 * @throws CannotProvideTerminalComponentException
	 *             if no specific mechanisms are linked to the required stream
	 *             types
	 * @throws CannotIntegrateTerminalComponentException
	 *             if the component integration fails
	 */
	<U, D> DataSender<D> createTerminal(DataReceiver<U> receiver, Class<U> upstream, Class<D> downstream)
			throws CannotProvideTerminalComponentException, CannotIntegrateTerminalComponentException;

}
