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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.wolfgang.common.message.Message;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataSender;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.InitialDataReceiverFactory;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.bound.TerminalDataReceiverFactory;
import org.wolfgang.contrail.ecosystem.key.FilteredUnitEcosystemKey;
import org.wolfgang.contrail.ecosystem.key.RegisteredUnitEcosystemKey;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.link.ComponentsLink;
import org.wolfgang.contrail.link.ComponentsLinkManager;
import org.wolfgang.contrail.link.ComponentsLinkManagerImpl;

/**
 * The <code>EcosystemImpl</code> proposes an implementation using standard
 * components and link mechanisms.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class EcosystemImpl implements Ecosystem {

	/**
	 * The dedicated link manager
	 */
	private final ComponentsLinkManager linkManager;

	/**
	 * Initial component integration triggers
	 */
	private final Map<RegisteredUnitEcosystemKey, DestinationComponentFactory<?, ?>> initialHooks;

	/**
	 * Terminal component integration triggers
	 */
	private final Map<RegisteredUnitEcosystemKey, SourceComponentFactory<?, ?>> terminalHooks;

	{
		this.linkManager = new ComponentsLinkManagerImpl();
		this.initialHooks = new HashMap<RegisteredUnitEcosystemKey, DestinationComponentFactory<?, ?>>();
		this.terminalHooks = new HashMap<RegisteredUnitEcosystemKey, SourceComponentFactory<?, ?>>();
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
	public <U, D> boolean addDestinationFactory(RegisteredUnitEcosystemKey ecosystemKey,
			DestinationComponentFactory<U, D> factory) {
		if (this.initialHooks.containsKey(ecosystemKey)) {
			return false;
		} else {
			this.initialHooks.put(ecosystemKey, factory);
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
	public <U, D> boolean addSourceFactory(RegisteredUnitEcosystemKey ecosystemKey, SourceComponentFactory<U, D> factory) {
		if (this.terminalHooks.containsKey(ecosystemKey)) {
			return false;
		} else {
			this.terminalHooks.put(ecosystemKey, factory);
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
	private <U, D> DestinationComponentFactory<U, D> getInitialIntegrator(FilteredUnitEcosystemKey filter)
			throws CannotProvideInitialComponentException {

		for (Entry<RegisteredUnitEcosystemKey, DestinationComponentFactory<?, ?>> unit : initialHooks.entrySet()) {
			if (filter.filteredBy(unit.getKey())) {
				return (DestinationComponentFactory<U, D>) unit.getValue();
			}
		}

		final Message message = message("org/wolfgang/contrail/message", "initial.factory.refused");
		throw new CannotProvideInitialComponentException(message.format(filter));
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
	private <U, D> SourceComponentFactory<U, D> getTerminalIntegrator(FilteredUnitEcosystemKey filter)
			throws CannotProvideTerminalComponentException {

		for (Entry<RegisteredUnitEcosystemKey, SourceComponentFactory<?, ?>> unit : terminalHooks.entrySet()) {
			if (filter.filteredBy(unit.getKey())) {
				return (SourceComponentFactory<U, D>) unit.getValue();
			}
		}

		final Message message = message("org/wolfgang/contrail/message", "terminal.factory.refused");
		throw new CannotProvideTerminalComponentException(message.format(filter));
	}

	@Override
	public <U, D> DataSender<U> bindToInitial(FilteredUnitEcosystemKey filter, final DataReceiver<D> receiver)
			throws CannotProvideInitialComponentException, CannotBindToInitialComponentException {
		try {
			final DestinationComponentFactory<U, D> initialIntegrator = getInitialIntegrator(filter);

			final InitialComponent<U, D> initialComponent = new InitialComponent<U, D>(new InitialDataReceiverFactory<U, D>() {
				@Override
				public DataReceiver<D> create(InitialComponent<U, D> component) {
					return receiver;
				}
			});

			final DestinationComponent<U, D> destinationComponent = initialIntegrator.create();

			final ComponentsLink<U, D> link = linkManager.connect(initialComponent, destinationComponent);

			return new DataSender<U>() {

				@Override
				public void close() throws IOException {
					try {
						link.dispose();
					} catch (ComponentDisconnectionRejectedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					initialComponent.getDataSender().close();
				}

				@Override
				public void sendData(U data) throws DataHandlerException {
					initialComponent.getDataSender().sendData(data);
				}
			};
		} catch (ComponentConnectionRejectedException e) {
			throw new CannotBindToInitialComponentException(e);
		}

	}

	@Override
	public <U, D> DataSender<D> bindToTerminal(FilteredUnitEcosystemKey filter, final DataReceiver<U> receiver)
			throws CannotProvideTerminalComponentException, CannotBindToTerminalComponentException {
		try {
			final SourceComponentFactory<U, D> terminalIntegrator = this.getTerminalIntegrator(filter);

			final TerminalComponent<U, D> terminalComponent = new TerminalComponent<U, D>(
					new TerminalDataReceiverFactory<U, D>() {
						@Override
						public DataReceiver<U> create(TerminalComponent<U, D> component) {
							return receiver;
						}
					});

			final SourceComponent<U, D> sourceComponent = terminalIntegrator.create();

			final ComponentsLink<U, D> link = linkManager.connect(sourceComponent, terminalComponent);

			return new DataSender<D>() {
				@Override
				public void close() throws IOException {
					try {
						link.dispose();
					} catch (ComponentDisconnectionRejectedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					terminalComponent.getDataSender().close();
				}

				@Override
				public void sendData(D data) throws DataHandlerException {
					terminalComponent.getDataSender().sendData(data);
				}
			};
		} catch (ComponentConnectionRejectedException e) {
			throw new CannotBindToTerminalComponentException(e);
		}

	}
}
