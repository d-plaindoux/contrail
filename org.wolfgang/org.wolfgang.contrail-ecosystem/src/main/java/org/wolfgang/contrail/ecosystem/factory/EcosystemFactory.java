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

package org.wolfgang.contrail.ecosystem.factory;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wolfgang.common.lang.TypeUtils;
import org.wolfgang.common.utils.UUIDUtils;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.MultipleSourceComponent;
import org.wolfgang.contrail.component.PipelineComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.bound.CannotCreateDataSenderException;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataSender;
import org.wolfgang.contrail.component.bound.DataSenderFactory;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.bound.TerminalFactory;
import org.wolfgang.contrail.component.pipeline.PipelineFactory;
import org.wolfgang.contrail.component.pipeline.identity.IdentityComponent;
import org.wolfgang.contrail.component.router.RouterSourceComponent;
import org.wolfgang.contrail.component.router.RouterSourceFactory;
import org.wolfgang.contrail.component.router.RouterSourceTable;
import org.wolfgang.contrail.component.router.event.Event;
import org.wolfgang.contrail.connection.CannotCreateClientException;
import org.wolfgang.contrail.connection.Client;
import org.wolfgang.contrail.connection.ClientFactory;
import org.wolfgang.contrail.connection.ClientFactoryCreationException;
import org.wolfgang.contrail.ecosystem.EcosystemImpl;
import org.wolfgang.contrail.ecosystem.key.RegisteredUnitEcosystemKey;
import org.wolfgang.contrail.ecosystem.model.BinderModel;
import org.wolfgang.contrail.ecosystem.model.ClientModel;
import org.wolfgang.contrail.ecosystem.model.EcosystemModel;
import org.wolfgang.contrail.ecosystem.model.FlowModel;
import org.wolfgang.contrail.ecosystem.model.FlowModel.Item;
import org.wolfgang.contrail.ecosystem.model.PipelineModel;
import org.wolfgang.contrail.ecosystem.model.RouterModel;
import org.wolfgang.contrail.ecosystem.model.TerminalModel;
import org.wolfgang.contrail.link.ComponentLinkManager;
import org.wolfgang.contrail.link.ComponentLinkManagerImpl;
import org.wolfgang.contrail.reference.DirectReference;
import org.wolfgang.contrail.reference.ReferenceEntryAlreadyExistException;
import org.wolfgang.contrail.reference.ReferenceFactory;

/**
 * <code>EcosystemFactory</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@SuppressWarnings("rawtypes")
public final class EcosystemFactory {

	private class DataSenderFactoryImpl<U, D> implements DataSenderFactory<U, D> {
		private final Item[] items;

		/**
		 * Constructor
		 * 
		 * @param items
		 */
		private DataSenderFactoryImpl(Item[] items) {
			super();
			this.items = items;
		}

		@Override
		public DataSender<U> create(DataReceiver<D> receiver) throws CannotCreateDataSenderException {
			try {
				final InitialComponent<U, D> initialComponent = new InitialComponent<U, D>(receiver);
				EcosystemFactory.this.create(initialComponent, items);
				return initialComponent.getDataSender();
			} catch (CannotCreateComponentException e) {
				throw new CannotCreateDataSenderException(e);
			} catch (ComponentConnectionRejectedException e) {
				throw new CannotCreateDataSenderException(e);
			}
		}
	}

	/**
	 * 
	 */
	private final ComponentLinkManager componentLinkManager;

	/**
	 * The class loader to use when components must be created
	 */
	private final ClassLoader classLoader;

	/**
	 * Aliasing
	 */
	private final Map<String, Component> aliasedComponents;

	/**
	 * Declared pipelines
	 */
	private final Map<String, PipelineModel> pipelines;

	/**
	 * Declared pipelines
	 */
	private final Map<String, RouterModel> routers;

	/**
	 * Declared terminal
	 */
	private final Map<String, TerminalModel> terminals;

	/**
	 * Declared terminal
	 */
	private final Map<String, FlowModel> flows;

	/**
	 * The main component
	 */
	private Component mainComponent; // TODO

	{
		this.componentLinkManager = new ComponentLinkManagerImpl();
		this.aliasedComponents = new HashMap<String, Component>();

		this.pipelines = new HashMap<String, PipelineModel>();
		this.terminals = new HashMap<String, TerminalModel>();
		this.routers = new HashMap<String, RouterModel>();
		this.flows = new HashMap<String, FlowModel>();

		this.classLoader = EcosystemFactory.class.getClassLoader();
	}

	/**
	 * Method called when a link must be established between two components
	 * 
	 * @param source
	 *            The source (Can be <code>null</code>)
	 * @param destination
	 *            The destination (Never <code>null</code>)
	 * @return the destination
	 * @throws ComponentConnectionRejectedException
	 */
	@SuppressWarnings("unchecked")
	private Component link(final Component source, final Component destination) throws ComponentConnectionRejectedException {
		if (source != null) {
			try {
				componentLinkManager.connect((SourceComponent) source, (DestinationComponent) destination);
			} catch (ClassCastException e) {
				throw new ComponentConnectionRejectedException(e);
			}
		}

		return destination;
	}

	/**
	 * Method called when a component is aliased
	 * 
	 * @param name
	 *            The name (Can be <code>null</code>)
	 * @param component
	 *            The component (Never <code>null</code>)
	 * @return the component
	 */
	private Component register(String name, Component component) {
		if (name != null) {
			aliasedComponents.put(name, component);
		}
		return component;
	}

	/**
	 * Internal method dedicated to the component flow creation
	 * 
	 * @param source
	 *            Can be null
	 * @param items
	 *            The items to be used for the flow creation
	 * @return a component (Never <code>null</code>
	 * @throws CannotCreateComponentException
	 * @throws CannotCreateDataSenderException
	 * @throws ComponentConnectionRejectedException
	 */
	private Component create(final Component source, final Item item) throws CannotCreateComponentException, CannotCreateDataSenderException, ComponentConnectionRejectedException {
		final String name = item.getName();

		if (item.asAlias() && this.aliasedComponents.containsKey(item.getAlias())) {
			return link(source, this.aliasedComponents.get(item.getAlias()));
		} else {
			final String[] parameters = item.getParameters();

			if (pipelines.containsKey(name)) {
				return register(item.getAlias(), link(source, create(pipelines.get(name), parameters)));
			} else if (terminals.containsKey(name)) {
				return register(item.getAlias(), link(source, create(terminals.get(name), parameters)));
			} else if (routers.containsKey(name)) {
				return register(item.getAlias(), link(source, create(routers.get(name), parameters)));
			} else if (flows.containsKey(name)) {
				return register(item.getAlias(), create(source, FlowModel.decompose(flows.get(name).getValue())));
			} else {
				throw new CannotCreateDataSenderException();
			}
		}
	}

	/**
	 * Internal method dedicated to the component flow creation
	 * 
	 * @param source
	 *            Can be null
	 * @param items
	 *            The items to be used for the flow creation
	 * @return a component (Never <code>null</code>
	 * @throws CannotCreateComponentException
	 * @throws CannotCreateDataSenderException
	 * @throws ComponentConnectionRejectedException
	 */
	private Component create(final Component source, final Item[] items) throws CannotCreateComponentException, CannotCreateDataSenderException, ComponentConnectionRejectedException {
		Component current = source;

		for (Item item : items) {
			current = create(current, item);
		}

		if (current == null) {
			throw new CannotCreateDataSenderException();
		} else {
			return current;
		}
	}

	/**
	 * @param pipeline
	 * @return
	 * @throws CannotCreateComponentException
	 */
	private PipelineComponent create(PipelineModel pipeline, String[] additionalParameters) throws CannotCreateComponentException {
		final String factory = pipeline.getFactory();
		final List<String> parameters = new ArrayList<String>();
		parameters.addAll(pipeline.getParameters());
		for (String additionalParameter : additionalParameters) {
			parameters.add(additionalParameter);
		}
		return PipelineFactory.create(classLoader, factory, parameters.toArray(new String[parameters.size()]));
	}

	/**
	 * @param pipeline
	 * @return
	 * @throws CannotCreateComponentException
	 */
	private TerminalComponent create(TerminalModel terminal, String[] additionalParameters) throws CannotCreateComponentException {
		final String factory = terminal.getFactory();
		final List<String> parameters = new ArrayList<String>();
		parameters.addAll(terminal.getParameters());
		for (String additionalParameter : additionalParameters) {
			parameters.add(additionalParameter);
		}
		return TerminalFactory.create(classLoader, factory, parameters.toArray(new String[parameters.size()]));
	}

	/**
	 * @param pipeline
	 * @return
	 * @throws CannotCreateComponentException
	 */
	private MultipleSourceComponent create(RouterModel router, String[] additionalParameters) throws CannotCreateComponentException {
		final List<String> parameters = new ArrayList<String>();

		parameters.addAll(router.getParameters());
		for (String additionalParameter : additionalParameters) {
			parameters.add(additionalParameter);
		}

		final DirectReference reference;

		try {
			reference = ReferenceFactory.createServerReference(UUIDUtils.digestBased(router.getSelf()));
		} catch (NoSuchAlgorithmException e) {
			throw new CannotCreateComponentException(e);
		}

		final RouterSourceComponent routerComponent = RouterSourceFactory.create(reference);

		for (ClientModel clientModel : router.getClients()) {
			try {
				final URI uri = new URI(clientModel.getEndpoint());
				final Item[] flow = FlowModel.decompose(clientModel.getFlow());

				final DirectReference mainReference;
				final Client client;

				try {
					mainReference = ReferenceFactory.createServerReference(UUIDUtils.digestBased(router.getSelf()));
					client = ClientFactory.create(classLoader, uri.getScheme());
				} catch (NoSuchAlgorithmException e) {
					throw new CannotCreateComponentException(e);
				} catch (ClientFactoryCreationException e) {
					throw new CannotCreateComponentException(e);
				}

				final RouterSourceTable.Entry entry = new RouterSourceTable.Entry() {
					@SuppressWarnings("unchecked")
					@Override
					public SourceComponent<Event, Event> create() throws CannotCreateComponentException {

						System.err.println(routerComponent + " - Opening a client to " + this.getReferenceToUse() + " [endpoint=" + uri + "]");
						try {

							// Build the initial flow
							final PipelineComponent initialTransducer = new IdentityComponent();
							final Component terminalTransducer = EcosystemFactory.this.create(initialTransducer, flow);

							routerComponent.filterSource(terminalTransducer.getComponentId(), this.getReferenceToUse());

							final DataSenderFactory<byte[], byte[]> dataSenderFactory = new DataSenderFactory<byte[], byte[]>() {
								@Override
								public DataSender<byte[]> create(DataReceiver<byte[]> component) throws CannotCreateDataSenderException {
									// Initial component
									final InitialComponent<byte[], byte[]> initial = new InitialComponent<byte[], byte[]>(component);
									try {
										componentLinkManager.connect(initial, initialTransducer);
										return initial.getDataSender();
									} catch (ComponentConnectionRejectedException e) {
										throw new CannotCreateDataSenderException(e);
									}
								}
							};

							client.connect(uri, dataSenderFactory);

							return (SourceComponent<Event, Event>) terminalTransducer;
						} catch (ComponentConnectionRejectedException e) {
							throw new CannotCreateComponentException(e);
						} catch (CannotCreateDataSenderException e) {
							throw new CannotCreateComponentException(e);
						} catch (ClassCastException e) {
							throw new CannotCreateComponentException(e);
						} catch (CannotCreateClientException e) {
							throw new CannotCreateComponentException(e);
						}
					}

					@Override
					public DirectReference getReferenceToUse() {
						return mainReference;
					}
				};

				routerComponent.getRouterSourceTable().insert(entry, entry.getReferenceToUse());

			} catch (URISyntaxException e) {
				// TODO -- Log it when a logger is provided
			} catch (ReferenceEntryAlreadyExistException e) {
				// TODO -- Log it when a logger is provided
			}
		}

		return routerComponent;
	}

	/**
	 * @param terminal
	 */
	private void register(TerminalModel terminal) {
		this.terminals.put(terminal.getName(), terminal);
	}

	/**
	 * @param pipeline
	 */
	private void register(PipelineModel pipeline) {
		this.pipelines.put(pipeline.getName(), pipeline);
	}

	/**
	 * @param router
	 */
	private void register(RouterModel router) {
		this.routers.put(router.getName(), router);
	}

	/**
	 * @param router
	 */
	private void register(FlowModel flow) {
		this.flows.put(flow.getName(), flow);
	}

	/**
	 * Main method called whether an ecosystem must be created
	 * 
	 * @param ecosystem
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public org.wolfgang.contrail.ecosystem.Ecosystem build(EcosystemModel ecosystem) throws EcosystemCreationException {
		try {
			final EcosystemImpl ecosystemImpl = new EcosystemImpl();

			for (TerminalModel terminal : ecosystem.getTerminals()) {
				register(terminal);
			}

			for (PipelineModel pipeline : ecosystem.getPipelines()) {
				register(pipeline);
			}

			for (RouterModel router : ecosystem.getRouters()) {
				register(router);
			}

			for (FlowModel flow : ecosystem.getFlows()) {
				register(flow);
			}

			for (BinderModel binder : ecosystem.getBinders()) {
				final String name = binder.getName();
				final Class<?> typeIn = TypeUtils.getType(binder.getTypeIn());
				final Class<?> typeOut = TypeUtils.getType(binder.getTypeOut());

				final RegisteredUnitEcosystemKey key = new RegisteredUnitEcosystemKey(name, typeIn, typeOut);
				ecosystemImpl.addFactory(key, new DataSenderFactoryImpl(FlowModel.decompose(binder.getFlow())));
			}

			final Item[] decompose = FlowModel.decompose(ecosystem.getMain());
			if (decompose.length > 0) {
				this.mainComponent = create(null, decompose);
			}

			return ecosystemImpl;
		} catch (CannotCreateComponentException e) {
			throw new EcosystemCreationException(e);
		} catch (CannotCreateDataSenderException e) {
			throw new EcosystemCreationException(e);
		} catch (ComponentConnectionRejectedException e) {
			throw new EcosystemCreationException(e);
		} catch (ClassNotFoundException e) {
			throw new EcosystemCreationException(e);
		}

	}
}