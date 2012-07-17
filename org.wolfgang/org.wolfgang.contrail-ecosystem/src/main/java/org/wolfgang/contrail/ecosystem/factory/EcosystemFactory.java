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

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wolfgang.common.lang.TypeUtils;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.PipelineComponent;
import org.wolfgang.contrail.component.RouterSourceComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.bound.CannotCreateDataSenderException;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.component.bound.DataSender;
import org.wolfgang.contrail.component.bound.DataSenderFactory;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.bound.TerminalFactory;
import org.wolfgang.contrail.component.multiple.RouterSourceFactory;
import org.wolfgang.contrail.component.pipeline.PipelineFactory;
import org.wolfgang.contrail.ecosystem.EcosystemImpl;
import org.wolfgang.contrail.ecosystem.key.RegisteredUnitEcosystemKey;
import org.wolfgang.contrail.ecosystem.model.Binder;
import org.wolfgang.contrail.ecosystem.model.Client;
import org.wolfgang.contrail.ecosystem.model.Ecosystem;
import org.wolfgang.contrail.ecosystem.model.EndPoint;
import org.wolfgang.contrail.ecosystem.model.Flow;
import org.wolfgang.contrail.ecosystem.model.Flow.Item;
import org.wolfgang.contrail.ecosystem.model.Pipeline;
import org.wolfgang.contrail.ecosystem.model.Router;
import org.wolfgang.contrail.ecosystem.model.Terminal;
import org.wolfgang.contrail.link.ComponentLinkManager;
import org.wolfgang.contrail.link.ComponentLinkManagerImpl;

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
	private final Map<String, Pipeline> pipelines;

	/**
	 * Declared pipelines
	 */
	private final Map<String, Router> routers;

	/**
	 * Declared terminal
	 */
	private final Map<String, Terminal> terminals;

	/**
	 * Declared terminal
	 */
	private final Map<String, Flow> flows;

	{
		this.componentLinkManager = new ComponentLinkManagerImpl();
		this.aliasedComponents = new HashMap<String, Component>();

		this.pipelines = new HashMap<String, Pipeline>();
		this.terminals = new HashMap<String, Terminal>();
		this.routers = new HashMap<String, Router>();
		this.flows = new HashMap<String, Flow>();

		this.classLoader = EcosystemFactory.class.getClassLoader();
	}

	/**
	 * Internal method dedicated to the component creation
	 * 
	 * @param name
	 * @return
	 * @throws CannotCreateComponentException
	 * @throws CannotCreateDataSenderException
	 * @throws ComponentConnectionRejectedException
	 */
	private Component create(final Component source, final Item[] items) throws CannotCreateComponentException, CannotCreateDataSenderException, ComponentConnectionRejectedException {

		Component current = source;

		for (Item item : items) {
			final Component component;
			final String name = item.getName();
			if (item.asAlias()) {
				if (aliasedComponents.containsKey(item.getAlias())) {
					component = aliasedComponents.get(item.getAlias());
				} else {
					if (pipelines.containsKey(name)) {
						component = create(pipelines.get(name));
					} else if (terminals.containsKey(name)) {
						component = create(terminals.get(name));
					} else if (routers.containsKey(name)) {
						component = create(routers.get(name));
					} else if (flows.containsKey(name)) {
						component = create(current, Flow.decompose(flows.get(name).getValue()));
					} else {
						return null;
					}
					if (component == null) {
						throw new CannotCreateDataSenderException();
					} else {
						aliasedComponents.put(item.getAlias(), component);
					}
				}
			} else {
				if (pipelines.containsKey(name)) {
					component = create(pipelines.get(name));
				} else if (terminals.containsKey(name)) {
					component = create(terminals.get(name));
				} else if (routers.containsKey(name)) {
					component = create(routers.get(name));
				} else if (flows.containsKey(name)) {
					component = create(current, Flow.decompose(flows.get(name).getValue()));
				} else {
					return null;
				}
				if (component == null) {
					throw new CannotCreateDataSenderException();
				}
			}

			componentLinkManager.connect((SourceComponent) current, (DestinationComponent) component);
			current = component;
		}

		return current;
	}

	/**
	 * @param pipeline
	 * @return
	 * @throws CannotCreateComponentException
	 */
	private PipelineComponent create(Pipeline pipeline) throws CannotCreateComponentException {
		final String factory = pipeline.getFactory();
		final List<String> parameters = pipeline.getParameters();
		return PipelineFactory.create(classLoader, factory, parameters.toArray(new String[parameters.size()]));
	}

	/**
	 * @param pipeline
	 * @return
	 * @throws CannotCreateComponentException
	 */
	private TerminalComponent create(Terminal terminal) throws CannotCreateComponentException {
		final String factory = terminal.getFactory();
		final List<String> parameters = terminal.getParameters();
		return TerminalFactory.create(classLoader, factory, parameters.toArray(new String[parameters.size()]));
	}

	/**
	 * @param pipeline
	 * @return
	 * @throws CannotCreateComponentException
	 */
	private RouterSourceComponent create(Router router) throws CannotCreateComponentException {
		final String factory = router.getFactory();
		final List<String> parameters = router.getParameters();
		final List<RouterSourceFactory.Client> clients = new ArrayList<RouterSourceFactory.Client>();

		for (Client client : router.getClients()) {
			try {
				final EndPoint endpoint = new EndPoint(client.getEndpoint());
				if (endpoint.getScheme().equals("tcp")) {
					clients.add(new RouterSourceFactory.Client() {
						@Override
						public void install(RouterSourceComponent<?, ?> component) {
							
						}
					});
				}
			} catch (URISyntaxException e) {
				// TODO -- Log it when a logger is provided
			}

		}

		return RouterSourceFactory.create(classLoader, factory, parameters.toArray(new String[parameters.size()]), clients.toArray(new RouterSourceFactory.Client[clients.size()]));
	}

	/**
	 * @param terminal
	 */
	private void register(Terminal terminal) {
		this.terminals.put(terminal.getName(), terminal);
	}

	/**
	 * @param pipeline
	 */
	private void register(Pipeline pipeline) {
		this.pipelines.put(pipeline.getName(), pipeline);
	}

	/**
	 * @param router
	 */
	private void register(Router router) {
		this.routers.put(router.getName(), router);
	}

	/**
	 * @param router
	 */
	private void register(Flow flow) {
		this.flows.put(flow.getName(), flow);
	}

	/**
	 * Main method called whether an ecosystem must be created
	 * 
	 * @param ecosystem
	 * @throws ClassNotFoundException
	 */
	public org.wolfgang.contrail.ecosystem.Ecosystem build(Ecosystem ecosystem) throws ClassNotFoundException {
		final EcosystemImpl ecosystemImpl = new EcosystemImpl();

		for (Terminal terminal : ecosystem.getTerminals()) {
			register(terminal);
		}

		for (Pipeline pipeline : ecosystem.getPipelines()) {
			register(pipeline);
		}

		for (Router router : ecosystem.getRouters()) {
			register(router);
		}

		for (Flow flow : ecosystem.getFlows()) {
			register(flow);
		}

		for (Binder binder : ecosystem.getBinders()) {
			final String name = binder.getName();
			final Class<?> typeIn = TypeUtils.getType(binder.getTypeIn());
			final Class<?> typeOut = TypeUtils.getType(binder.getTypeOut());

			final RegisteredUnitEcosystemKey key = new RegisteredUnitEcosystemKey(name, typeIn, typeOut);
			ecosystemImpl.addFactory(key, new DataSenderFactoryImpl(Flow.decompose(binder.getFlow())));
		}

		return ecosystemImpl;
	}
}
