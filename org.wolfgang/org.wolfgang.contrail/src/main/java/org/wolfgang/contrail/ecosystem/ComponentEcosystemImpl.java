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

import java.util.HashMap;
import java.util.Map;

import org.wolfgang.common.message.Message;
import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataSender;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.InitialDataReceiverFactory;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.bound.TerminalDataReceiverFactory;
import org.wolfgang.contrail.link.ComponentsLinkManager;
import org.wolfgang.contrail.link.ComponentsLinkManagerImpl;

/**
 * The <code>ComponentEcosystemImpl</code> proposes an implementation using
 * standard components and link mechanisms.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class ComponentEcosystemImpl implements ComponentEcosystem {

	/**
	 * The dedicated link manager
	 */
	private final ComponentsLinkManager linkManager;

	/**
	 * Initial component integration triggers
	 */
	private final Map<UnitIntegrationKey<?, ?>, DestinationComponentFactory<?, ?>> initialIntegrators;

	/**
	 * Terminal component integration triggers
	 */
	private final Map<UnitIntegrationKey<?, ?>, SourceComponentFactory<?, ?>> terminalIntegrators;

	{
		this.linkManager = new ComponentsLinkManagerImpl();
		this.initialIntegrators = new HashMap<UnitIntegrationKey<?, ?>, DestinationComponentFactory<?, ?>>();
		this.terminalIntegrators = new HashMap<UnitIntegrationKey<?, ?>, SourceComponentFactory<?, ?>>();
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
	public <U, D> boolean addDestinationFactory(Class<U> upstream, Class<D> downstream,
			DestinationComponentFactory<U, D> factory) {
		final UnitIntegrationKey<U, D> unitIntegratorKey = new UnitIntegrationKey<U, D>(upstream, downstream);
		if (this.terminalIntegrators.containsKey(unitIntegratorKey)) {
			return false;
		} else {
			this.initialIntegrators.put(new UnitIntegrationKey<U, D>(upstream, downstream), factory);
			return false;
		}
	}

	/**
	 * Method used to add a new terminal component unit integration trigger. If
	 * the entry is already defined this method do not add the integrator.
	 * 
	 * @param upstream
	 *            The upstream type
	 * @param downstream
	 *            The downstream type
	 * @param factory
	 *            The factory
	 * @return true if the factory is correctly added; false otherwise
	 */
	public <U, D> boolean addSourceFactory(Class<U> upstream, Class<D> downstream, SourceComponentFactory<U, D> factory) {
		final UnitIntegrationKey<U, D> unitIntegratorKey = new UnitIntegrationKey<U, D>(upstream, downstream);
		if (this.terminalIntegrators.containsKey(unitIntegratorKey)) {
			return false;
		} else {
			this.terminalIntegrators.put(unitIntegratorKey, factory);
			return true;
		}
	}

	/**
	 * Method providing the initial component factory using types.
	 * 
	 * @param upstream
	 *            The upstream type
	 * @param downstream
	 *            The downstream type
	 * @return the initial component factory
	 * @throws CannotProvideInitialComponentException
	 *             if the initial component cannot be created
	 */
	@SuppressWarnings("unchecked")
	private <U, D> DestinationComponentFactory<U, D> getInitialIntegrator(Class<U> upstream, Class<D> downstream)
			throws CannotProvideInitialComponentException {
		final UnitIntegrationKey<U, D> entry = new UnitIntegrationKey<U, D>(upstream, downstream);
		final DestinationComponentFactory<?, ?> integrator = this.initialIntegrators.get(entry);
		if (integrator != null) {
			return (DestinationComponentFactory<U, D>) integrator;
		} else {
			final Message message = MessagesProvider.get("org/wolfgang/contrail/message", "initial.integrator.refused");
			throw new CannotProvideInitialComponentException(message.format(upstream, downstream));
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
	private <U, D> SourceComponentFactory<U, D> getTerminalIntegrator(Class<U> upstream, Class<D> downstream)
			throws CannotProvideTerminalComponentException {
		final UnitIntegrationKey<U, D> entry = new UnitIntegrationKey<U, D>(upstream, downstream);
		final SourceComponentFactory<?, ?> integrator = this.terminalIntegrators.get(entry);
		if (integrator != null) {
			return (SourceComponentFactory<U, D>) integrator;
		} else {
			final Message message = MessagesProvider.get("org/wolfgang/contrail/message", "terminal.integrator.refused");
			throw new CannotProvideTerminalComponentException(message.format(upstream, downstream));
		}
	}

	@Override
	public <U, D> DataSender<U> createInitial(final DataReceiver<D> receiver, Class<U> upstream, Class<D> downstream)
			throws CannotProvideInitialComponentException, CannotIntegrateInitialComponentException {
		final DestinationComponentFactory<U, D> initialIntegrator = getInitialIntegrator(upstream, downstream);

		final InitialComponent<U, D> initialComponent = new InitialComponent<U, D>(new InitialDataReceiverFactory<U, D>() {
			@Override
			public DataReceiver<D> create(InitialComponent<U, D> component) {
				return receiver;
			}
		});

		final DestinationComponent<U, D> destinationComponent = initialIntegrator.create();

		try {
			linkManager.connect(initialComponent, destinationComponent);
		} catch (ComponentConnectionRejectedException e) {
			throw new CannotIntegrateInitialComponentException(e);
		}

		return initialComponent.getDataSender();
	}

	@Override
	public <U, D> DataSender<D> createTerminal(final DataReceiver<U> receiver, Class<U> upstream, Class<D> downstream)
			throws CannotProvideTerminalComponentException, CannotIntegrateTerminalComponentException {
		final SourceComponentFactory<U, D> terminalIntegrator = getTerminalIntegrator(upstream, downstream);

		final TerminalComponent<U, D> terminalComponent = new TerminalComponent<U, D>(new TerminalDataReceiverFactory<U, D>() {
			@Override
			public DataReceiver<U> create(TerminalComponent<U, D> component) {
				return receiver;
			}
		});

		final SourceComponent<U, D> sourceComponent = terminalIntegrator.create();

		try {
			linkManager.connect(sourceComponent, terminalComponent);
		} catch (ComponentConnectionRejectedException e) {
			throw new CannotIntegrateTerminalComponentException(e);
		}

		return terminalComponent.getDataSender();
	}

}
