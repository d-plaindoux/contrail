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

import static org.wolfgang.common.message.MessagesProvider.message;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.wolfgang.common.message.Message;
import org.wolfgang.contrail.component.bound.DataSenderFactory;
import org.wolfgang.contrail.ecosystem.key.RegisteredUnitEcosystemKey;
import org.wolfgang.contrail.ecosystem.key.UnitEcosystemKey;
import org.wolfgang.contrail.link.ComponentLinkManager;
import org.wolfgang.contrail.link.ComponentLinkManagerImpl;

/**
 * The <code>EcosystemImpl</code> proposes an implementation using standard
 * components and link mechanisms.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class EcosystemImpl implements Ecosystem {

	/**
	 * Initial component integration triggers
	 */
	private final Map<RegisteredUnitEcosystemKey, DataSenderFactory<?, ?>> hooks;

	/**
	 * The related link manager
	 */
	private final ComponentLinkManagerImpl linkManager;

	{
		this.hooks = new HashMap<RegisteredUnitEcosystemKey, DataSenderFactory<?, ?>>();
		this.linkManager = new ComponentLinkManagerImpl();
	}

	/**
	 * Return the value of the link manager
	 * 
	 * @return the link manager
	 */
	public ComponentLinkManagerImpl getLinkManager() {
		return linkManager;
	}

	/**
	 * Method used to add a new terminal component unit integrator. If the entry
	 * is already defined this method do not add the integrator.
	 * 
	 * @param upstream
	 *            The upstream type
	 * @param downstream
	 *            The downstream type
	 * @param factory
	 *            The factory
	 * @return true if the factory is correctly added; false otherwise
	 */
	public <U, D> boolean addFactory(RegisteredUnitEcosystemKey ecosystemKey, DataSenderFactory<U, D> factory) {
		if (this.hooks.containsKey(ecosystemKey)) {
			return false;
		} else {
			this.hooks.put(ecosystemKey, factory);
			return true;
		}
	}

	/**
	 * Method providing the terminal component factory using types.
	 * 
	 * @param upstream
	 *            The upstream type
	 * @param downstream
	 *            The downstream type
	 * @return the terminal component factory
	 * @throws CannotProvideTerminalComponentException
	 *             if the terminal component cannot be created
	 */
	@SuppressWarnings("unchecked")
	public <U, D> DataSenderFactory<U, D> getBinder(final UnitEcosystemKey filter) throws CannotProvideComponentException {
		for (Entry<RegisteredUnitEcosystemKey, DataSenderFactory<?, ?>> unit : hooks.entrySet()) {
			if (filter.filteredBy(unit.getKey())) {
				return (DataSenderFactory<U, D>) unit.getValue();
			}
		}

		final Message message = message("org/wolfgang/contrail/ecosystem", "dataflow.factory.not.found");
		throw new CannotProvideComponentException(message.format(filter));
	}
}
