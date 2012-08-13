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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wolfgang.common.lang.TypeUtils;
import org.wolfgang.common.message.Message;
import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.common.utils.UUIDUtils;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.MultipleSourceComponent;
import org.wolfgang.contrail.component.PipelineComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.annotation.ContrailClient;
import org.wolfgang.contrail.component.annotation.ContrailInitial;
import org.wolfgang.contrail.component.annotation.ContrailPipeline;
import org.wolfgang.contrail.component.annotation.ContrailServer;
import org.wolfgang.contrail.component.annotation.ContrailTerminal;
import org.wolfgang.contrail.component.bound.CannotCreateDataSenderException;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataSender;
import org.wolfgang.contrail.component.bound.DataSenderFactory;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.pipeline.identity.IdentityComponent;
import org.wolfgang.contrail.component.router.RouterSourceComponent;
import org.wolfgang.contrail.component.router.RouterSourceTable;
import org.wolfgang.contrail.connection.CannotCreateClientException;
import org.wolfgang.contrail.connection.CannotCreateServerException;
import org.wolfgang.contrail.connection.Client;
import org.wolfgang.contrail.connection.ClientFactory;
import org.wolfgang.contrail.connection.ClientFactoryCreationException;
import org.wolfgang.contrail.connection.ContextFactory;
import org.wolfgang.contrail.connection.Server;
import org.wolfgang.contrail.connection.ServerFactory;
import org.wolfgang.contrail.connection.ServerFactoryCreationException;
import org.wolfgang.contrail.connection.Worker;
import org.wolfgang.contrail.ecosystem.EcosystemImpl;
import org.wolfgang.contrail.ecosystem.key.RegisteredUnitEcosystemKey;
import org.wolfgang.contrail.ecosystem.model.BinderModel;
import org.wolfgang.contrail.ecosystem.model.ClientModel;
import org.wolfgang.contrail.ecosystem.model.EcosystemModel;
import org.wolfgang.contrail.ecosystem.model.FlowModel;
import org.wolfgang.contrail.ecosystem.model.FlowModel.Item;
import org.wolfgang.contrail.ecosystem.model.PipelineModel;
import org.wolfgang.contrail.ecosystem.model.RouterModel;
import org.wolfgang.contrail.ecosystem.model.ServerModel;
import org.wolfgang.contrail.ecosystem.model.TerminalModel;
import org.wolfgang.contrail.event.Event;
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
public final class EcosystemFactoryImpl implements ContextFactory {

	private static class DataSenderFactoryImpl<U, D> implements DataSenderFactory<U, D> {
		private final EcosystemFactoryImpl ecosystemFactory;
		private final Item[] items;

		/**
		 * Constructor
		 * 
		 * @param items
		 */
		private DataSenderFactoryImpl(EcosystemFactoryImpl ecosystemFactorie, Item[] items) {
			super();
			this.ecosystemFactory = ecosystemFactorie;
			this.items = items;
		}

		@Override
		public DataSender<U> create(DataReceiver<D> receiver) throws CannotCreateDataSenderException {
			try {
				final InitialComponent<U, D> initialComponent = new InitialComponent<U, D>(receiver);
				ecosystemFactory.create(initialComponent, items);
				return initialComponent.getDataSender();
			} catch (CannotCreateComponentException e) {
				throw new CannotCreateDataSenderException(e);
			} catch (ComponentConnectionRejectedException e) {
				throw new CannotCreateDataSenderException(e);
			}
		}
	}

	/**
	 * Server factory
	 */
	private final ServerFactory serverFactory;

	/**
	 * Client factory
	 */
	private final ClientFactory clientFactory;

	/**
	 * The embedded component link manager
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
		this.serverFactory = new ServerFactory();
		this.clientFactory = new ClientFactory();

		this.componentLinkManager = new ComponentLinkManagerImpl();
		this.aliasedComponents = new HashMap<String, Component>();

		this.pipelines = new HashMap<String, PipelineModel>();
		this.terminals = new HashMap<String, TerminalModel>();
		this.routers = new HashMap<String, RouterModel>();
		this.flows = new HashMap<String, FlowModel>();

		this.classLoader = EcosystemFactoryImpl.class.getClassLoader();
	}

	/**
	 * Constructor
	 */
	private EcosystemFactoryImpl() {
		super();
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
				final Message message = MessagesProvider.message("org.wolfgang.contrail.ecosystem", "value.unknown");
				throw new CannotCreateDataSenderException(message.format(name));
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
		return PipelineFactory.create(this, factory, parameters.toArray(new String[parameters.size()]));
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
		return TerminalFactory.create(this, factory, parameters.toArray(new String[parameters.size()]));
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
					client = this.clientFactory.get(uri.getScheme());
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
							final Component terminalTransducer = EcosystemFactoryImpl.this.create(initialTransducer, flow);

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

	private Worker create(ServerModel serverModel) throws CannotCreateComponentException {
		final Server server;

		try {
			final URI uri = new URI(serverModel.getEndpoint());
			server = this.serverFactory.get(uri.getScheme());

			// Build the initial flow
			final Item[] flow = FlowModel.decompose(serverModel.getFlow());

			final PipelineComponent initialTransducer = new IdentityComponent();
			final Component terminalTransducer = create(initialTransducer, flow);

			final DataSenderFactory<byte[], byte[]> dataSenderFactory = new DataSenderFactory<byte[], byte[]>() {
				@SuppressWarnings("unchecked")
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

			return server.bind(uri, dataSenderFactory);
		} catch (ServerFactoryCreationException e) {
			throw new CannotCreateComponentException(e);
		} catch (URISyntaxException e) {
			throw new CannotCreateComponentException(e);
		} catch (CannotCreateServerException e) {
			throw new CannotCreateComponentException(e);
		} catch (CannotCreateDataSenderException e) {
			throw new CannotCreateComponentException(e);
		} catch (ComponentConnectionRejectedException e) {
			throw new CannotCreateComponentException(e);
		}
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
	 * @param loader
	 * @param factory
	 */
	private void revolve(EcosystemModel ecosystemModel) {
		if (ecosystemModel.getDeclaration() != null) {
			for (Item item : FlowModel.decompose(ecosystemModel.getDeclaration())) {
				try {
					final Class<?> aClass = classLoader.loadClass(item.getName());
					if (aClass.isAnnotationPresent(ContrailClient.class)) {
						final ContrailClient annotation = aClass.getAnnotation(ContrailClient.class);
						this.getClientFactory().declareScheme(annotation.scheme(), aClass);
					} else if (aClass.isAnnotationPresent(ContrailServer.class)) {
						final ContrailServer annotation = aClass.getAnnotation(ContrailServer.class);
						this.getServerFactory().declareScheme(annotation.scheme(), aClass);
					} else if (aClass.isAnnotationPresent(ContrailPipeline.class)) {
						final ContrailPipeline annotation = aClass.getAnnotation(ContrailPipeline.class);
						final String name;
						if (item.asAlias()) {
							name = item.getAlias();
						} else {
							name = annotation.name();
						}
						final PipelineModel model = new PipelineModel();
						model.setName(name);
						model.setFactory(item.getName());
						for (String parameter : item.getParameters()) {
							model.add(parameter);
						}
						ecosystemModel.add(model);
					} else if (aClass.isAnnotationPresent(ContrailTerminal.class)) {
						final ContrailTerminal annotation = aClass.getAnnotation(ContrailTerminal.class);
						final String name;
						if (item.asAlias()) {
							name = item.getAlias();
						} else {
							name = annotation.name();
						}
						final TerminalModel model = new TerminalModel();
						model.setName(name);
						model.setFactory(item.getName());
						for (String parameter : item.getParameters()) {
							model.add(parameter);
						}
						ecosystemModel.add(model);
					} else if (aClass.isAnnotationPresent(ContrailInitial.class)) {
						// TODO
					}
				} catch (ClassNotFoundException ignore) {
					// Consume
				}
			}

			ecosystemModel.setDeclaration(null); // Work done once only
		}
	}

	/**
	 * Main method called whether an ecosystem must be created
	 * 
	 * @param ecosystem
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public static org.wolfgang.contrail.ecosystem.Ecosystem build(EcosystemModel ecosystem) throws EcosystemCreationException {

		final EcosystemFactoryImpl ecosystemFactory = new EcosystemFactoryImpl();

		ecosystemFactory.revolve(ecosystem);

		try {
			final EcosystemImpl ecosystemImpl = new EcosystemImpl() {
				public void close() throws IOException {
					ecosystemFactory.clientFactory.close();
					ecosystemFactory.serverFactory.close();
				}
			};

			for (TerminalModel terminal : ecosystem.getTerminals()) {
				ecosystemFactory.register(terminal);
			}

			for (PipelineModel pipeline : ecosystem.getPipelines()) {
				ecosystemFactory.register(pipeline);
			}

			for (RouterModel router : ecosystem.getRouters()) {
				ecosystemFactory.register(router);
			}

			for (FlowModel flow : ecosystem.getFlows()) {
				ecosystemFactory.register(flow);
			}

			for (ServerModel server : ecosystem.getServers()) {
				ecosystemFactory.create(server); // Catch the worker
			}

			for (BinderModel binder : ecosystem.getBinders()) {
				final String name = binder.getName();
				final Class<?> typeIn = TypeUtils.getType(binder.getTypeIn());
				final Class<?> typeOut = TypeUtils.getType(binder.getTypeOut());

				final RegisteredUnitEcosystemKey key = new RegisteredUnitEcosystemKey(name, typeIn, typeOut);
				ecosystemImpl.addBinder(key, new DataSenderFactoryImpl(ecosystemFactory, FlowModel.decompose(binder.getFlow())));
			}

			final Item[] decompose = FlowModel.decompose(ecosystem.getMain());
			if (decompose.length > 0) {
				ecosystemFactory.mainComponent = ecosystemFactory.create(null, decompose);
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

	// EcosystemFactory public definitions

	@Override
	public ClassLoader getClassLoader() {
		return this.classLoader;
	}

	@Override
	public ServerFactory getServerFactory() {
		return serverFactory;
	}

	@Override
	public ClientFactory getClientFactory() {
		return clientFactory;
	}
}