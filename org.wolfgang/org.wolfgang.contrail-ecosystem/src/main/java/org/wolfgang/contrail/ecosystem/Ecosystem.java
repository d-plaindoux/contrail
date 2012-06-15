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

package org.wolfgang.contrail.ecosystem;

import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataSenderFactory;
import org.wolfgang.contrail.ecosystem.key.FilteredUnitEcosystemKey;

/**
 * An <code>Ecosystem</code> is able to provide components based on criterion.
 * This is the main component to be used for a network components construction.
 * Since the component is returned it must be connected.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface Ecosystem {

	/**
	 * Method called whether an initial component binder is required
	 * 
	 * @param key
	 *            The name of the required binder
	 * @return a data sender factory (Never <code>null</code>)
	 * @throws CannotProvideInitialComponentException
	 *             if no specific mechanisms are linked to the required stream
	 *             types
	 * @throws CannotProvideInitialComponentException
	 *             if the component integration fails
	 */
	<U, D> DataSenderFactory<U, D> getInitialBinder(final FilteredUnitEcosystemKey key)
			throws CannotProvideInitialComponentException;

	/**
	 * Method called whether a terminal component binder is required
	 * 
	 * @param key
	 *            The name of the required binder
	 * @return a data sender factory (Never <code>null</code>)
	 * @throws CannotProvideInitialComponentException
	 *             if no specific mechanisms are linked to the required stream
	 *             types
	 * @throws CannotProvideTerminalComponentException
	 *             if the component integration fails
	 */
	<U, D> DataSenderFactory<D, U> getTerminalBinder(final FilteredUnitEcosystemKey key)
			throws CannotProvideTerminalComponentException;
}
