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

import java.util.HashMap;
import java.util.Map;

import org.wolfgang.common.message.Message;
import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataSender;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.InitialDataReceiverFactory;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.bound.TerminalDataReceiverFactory;

/**
 * The <code>ComponentIntegratorImpl</code> proposes an implementation using
 * standard components and link mechanisms.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class ComponentIntegratorImpl implements ComponentIntegrator {

	/**
	 * Initial component integrators
	 */
	private final Map<UnitIntegratorKey<?, ?>, InitialComponentUnitIntegrator<?, ?>> initialIntegrators;

	/**
	 * Terminal component integrators
	 */
	private final Map<UnitIntegratorKey<?, ?>, TerminalComponentUnitIntegrator<?, ?>> terminalIntegrators;

	{
		this.initialIntegrators = new HashMap<UnitIntegratorKey<?, ?>, InitialComponentUnitIntegrator<?, ?>>();
		this.terminalIntegrators = new HashMap<UnitIntegratorKey<?, ?>, TerminalComponentUnitIntegrator<?, ?>>();
	}

	/**
	 * Method used to add a new terminal component unit integrator. If the entry
	 * is already defined this method do not add the integrator.
	 * 
	 * @param upstream
	 *            The upstream type
	 * @param downstream
	 *            The downstream type
	 * @param integrator
	 *            The integrator
	 * @return true if the integrator is correctly added; false otherwise
	 */
	public <U, D> boolean add(Class<U> upstream, Class<D> downstream, InitialComponentUnitIntegrator<U, D> integrator) {
		final UnitIntegratorKey<U, D> unitIntegratorKey = new UnitIntegratorKey<U, D>(upstream, downstream);
		if (this.terminalIntegrators.containsKey(unitIntegratorKey)) {
			return false;
		} else {
			this.initialIntegrators.put(new UnitIntegratorKey<U, D>(upstream, downstream), integrator);
			return false;
		}
	}

	/**
	 * Method used to add a new terminal component unit integrator. If the entry
	 * is already defined this method do not add the integrator.
	 * 
	 * @param upstream
	 *            The upstream type
	 * @param downstream
	 *            The downstream type
	 * @param integrator
	 *            The integrator
	 * @return true if the integrator is correctly added; false otherwise
	 */
	public <U, D> boolean add(Class<U> upstream, Class<D> downstream, TerminalComponentUnitIntegrator<U, D> integrator) {
		final UnitIntegratorKey<U, D> unitIntegratorKey = new UnitIntegratorKey<U, D>(upstream, downstream);
		if (this.terminalIntegrators.containsKey(unitIntegratorKey)) {
			return false;
		} else {
			this.terminalIntegrators.put(unitIntegratorKey, integrator);
			return true;
		}
	}

	/**
	 * Method providing the initial component integrator using types.
	 * 
	 * @param upstream
	 *            The upstream type
	 * @param downstream
	 *            The downstream type
	 * @return the initial component integrator
	 * @throws CannotProvideInitialComponentException
	 *             if the initial component cannot be created
	 */
	@SuppressWarnings("unchecked")
	private <U, D> InitialComponentUnitIntegrator<U, D> getInitialIntegrator(Class<U> upstream, Class<D> downstream)
			throws CannotProvideInitialComponentException {
		final UnitIntegratorKey<U, D> entry = new UnitIntegratorKey<U, D>(upstream, downstream);
		final InitialComponentUnitIntegrator<?, ?> integrator = this.initialIntegrators.get(entry);
		if (integrator != null) {
			return (InitialComponentUnitIntegrator<U, D>) integrator;
		} else {
			final Message message = MessagesProvider.get("org/wolfgang/contrail/message", "initial.integrator.refused");
			throw new CannotProvideInitialComponentException(message.format(upstream, downstream));
		}
	}

	/**
	 * Method providing the terminal component integrator using types.
	 * 
	 * @param upstream
	 *            The upstream type
	 * @param downstream
	 *            The downstream type
	 * @return the terminal component integrator
	 * @throws CannotProvideTerminalComponentException
	 *             if the terminal component cannot be created
	 */
	@SuppressWarnings("unchecked")
	private <U, D> TerminalComponentUnitIntegrator<U, D> getTerminalIntegrator(Class<U> upstream, Class<D> downstream)
			throws CannotProvideTerminalComponentException {
		final UnitIntegratorKey<U, D> entry = new UnitIntegratorKey<U, D>(upstream, downstream);
		final TerminalComponentUnitIntegrator<?, ?> integrator = this.terminalIntegrators.get(entry);
		if (integrator != null) {
			return (TerminalComponentUnitIntegrator<U, D>) integrator;
		} else {
			final Message message = MessagesProvider.get("org/wolfgang/contrail/message", "terminal.integrator.refused");
			throw new CannotProvideTerminalComponentException(message.format(upstream, downstream));
		}
	}

	@Override
	public <U, D> DataSender<U> createInitial(final DataReceiver<D> receiver, Class<U> upstream, Class<D> downstream)
			throws CannotProvideInitialComponentException {
		final InitialComponentUnitIntegrator<U, D> initialIntegrator = getInitialIntegrator(upstream, downstream);

		final InitialComponent<U, D> component = new InitialComponent<U, D>(new InitialDataReceiverFactory<U, D>() {
			@Override
			public DataReceiver<D> create(InitialComponent<U, D> component) {
				return receiver;
			}
		});

		initialIntegrator.performIntegration(component);

		return component.getDataSender();
	}

	@Override
	public <U, D> DataSender<D> createTerminal(final DataReceiver<U> receiver, Class<U> upstream, Class<D> downstream)
			throws CannotProvideTerminalComponentException {
		final TerminalComponentUnitIntegrator<U, D> terminalIntegrator = getTerminalIntegrator(upstream, downstream);

		final TerminalComponent<U, D> component = new TerminalComponent<U, D>(new TerminalDataReceiverFactory<U, D>() {
			@Override
			public DataReceiver<U> create(TerminalComponent<U, D> component) {
				return receiver;
			}
		});

		terminalIntegrator.performIntegration(component);

		return component.getDataSender();
	}

}
