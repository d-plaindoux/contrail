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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.wolfgang.common.message.Message;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.ComponentFactory;
import org.wolfgang.contrail.ecosystem.key.RegisteredUnitEcosystemKey;
import org.wolfgang.contrail.ecosystem.key.UnitEcosystemKey;
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.link.ComponentLink;
import org.wolfgang.contrail.link.ComponentManager;

/**
 * The <code>EcosystemImpl</code> proposes an implementation using standard
 * components and link mechanisms.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class EcosystemImpl implements Ecosystem {

	/**
	 * List of active components
	 */
	private final List<Component> activeComponents;

	/**
	 * Initial component integration triggers
	 */
	private final Map<RegisteredUnitEcosystemKey, ComponentFactory> hooks;

	/**
	 * The related link manager
	 */
	private final ComponentManager linkManager;

	{
		this.hooks = new HashMap<RegisteredUnitEcosystemKey, ComponentFactory>();
		this.activeComponents = new ArrayList<Component>();
	}

	/**
	 * Constructor
	 */
	public EcosystemImpl() {
		this(new ComponentManager());
	}

	/**
	 * Constructor
	 * 
	 * @param manager
	 * @param activeComponents
	 */
	public EcosystemImpl(ComponentManager manager) {
		super();
		this.linkManager = manager;
	}

	/**
	 * Register an active component
	 * 
	 * @param component
	 */
	protected void addActiveComponent(Component component) {
		this.activeComponents.add(component);
	}

	/**
	 * Return the value of the link manager
	 * 
	 * @return the link manager
	 */
	public ComponentManager getLinkManager() {
		return linkManager;
	}

	/**
	 * Method used to add a new terminal component unit integration. If the
	 * entry is already defined this method do not add the integration.
	 * 
	 * @param upstream
	 *            The upstream type
	 * @param downstream
	 *            The downstream type
	 * @param factory
	 *            The factory
	 * @return true if the factory is correctly added; false otherwise
	 */
	public <U, D> boolean addBinder(RegisteredUnitEcosystemKey ecosystemKey, ComponentFactory factory) {
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
	public ComponentFactory getFactory(final UnitEcosystemKey filter) throws CannotProvideComponentException {
		for (Entry<RegisteredUnitEcosystemKey, ComponentFactory> unit : hooks.entrySet()) {
			if (filter.filteredBy(unit.getKey())) {
				return unit.getValue();
			}
		}

		final Message message = message("org/wolfgang/contrail/ecosystem", "dataflow.factory.not.found");
		throw new CannotProvideComponentException(message.format(filter));
	}

	@Override
	public void close() throws IOException {
		for (ComponentLink link : this.linkManager.getExistingLinks()) {
			try {
				link.dispose();
			} catch (ComponentDisconnectionRejectedException consume) {
				// Ignore
			}
		}

		for (Component component : activeComponents) {
			try {
				component.closeDownStream();
			} catch (DataFlowCloseException consume) {
				// Ignore
			}
			try {
				component.closeUpStream();
			} catch (DataFlowCloseException consume) {
				// Ignore
			}
		}
	}
}
