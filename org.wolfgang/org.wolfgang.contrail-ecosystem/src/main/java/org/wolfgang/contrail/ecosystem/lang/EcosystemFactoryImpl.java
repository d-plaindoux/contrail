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

package org.wolfgang.contrail.ecosystem.lang;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wolfgang.common.lang.TypeUtils;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.PipelineComponent;
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
import org.wolfgang.contrail.connection.Client;
import org.wolfgang.contrail.connection.ClientFactory;
import org.wolfgang.contrail.connection.Server;
import org.wolfgang.contrail.connection.ServerFactory;
import org.wolfgang.contrail.ecosystem.EcosystemImpl;
import org.wolfgang.contrail.ecosystem.factory.EcosystemFactory;
import org.wolfgang.contrail.ecosystem.key.RegisteredUnitEcosystemKey;
import org.wolfgang.contrail.ecosystem.lang.code.ClosureValue;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValue;
import org.wolfgang.contrail.ecosystem.lang.code.ComponentValue;
import org.wolfgang.contrail.ecosystem.lang.code.ConstantValue;
import org.wolfgang.contrail.ecosystem.lang.code.FlowValue;
import org.wolfgang.contrail.ecosystem.lang.code.ImportEntry;
import org.wolfgang.contrail.ecosystem.lang.delta.InitialFactory;
import org.wolfgang.contrail.ecosystem.lang.delta.PipelineFactory;
import org.wolfgang.contrail.ecosystem.lang.delta.TerminalFactory;
import org.wolfgang.contrail.ecosystem.lang.model.Apply;
import org.wolfgang.contrail.ecosystem.lang.model.Atom;
import org.wolfgang.contrail.ecosystem.lang.model.Bind;
import org.wolfgang.contrail.ecosystem.lang.model.Definition;
import org.wolfgang.contrail.ecosystem.lang.model.EcosystemModel;
import org.wolfgang.contrail.ecosystem.lang.model.Expression;
import org.wolfgang.contrail.ecosystem.lang.model.ExpressionVisitor;
import org.wolfgang.contrail.ecosystem.lang.model.Function;
import org.wolfgang.contrail.ecosystem.lang.model.Import;
import org.wolfgang.contrail.ecosystem.lang.model.Reference;
import org.wolfgang.contrail.ecosystem.lang.model.Starter;
import org.wolfgang.contrail.link.ComponentLinkManager;
import org.wolfgang.contrail.link.ComponentLinkManagerImpl;

/**
 * <code>EcosystemFactory</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@SuppressWarnings("rawtypes")
public final class EcosystemFactoryImpl implements EcosystemFactory {

	private static class DataSenderFactoryImpl<U, D> implements DataSenderFactory<U, D> {
		private final EcosystemFactoryImpl factory;
		private final CodeValue flow;

		/**
		 * Constructor
		 * 
		 * @param items
		 */
		private DataSenderFactoryImpl(EcosystemFactoryImpl factory, CodeValue flow) {
			super();
			this.factory = factory;
			this.flow = flow;
		}

		@Override
		public DataSender<U> create(DataReceiver<D> receiver) throws CannotCreateDataSenderException {
			try {
				final InitialComponent<U, D> initialComponent = new InitialComponent<U, D>(receiver);
				factory.create(initialComponent, flow);
				return initialComponent.getDataSender();
			} catch (CannotCreateComponentException e) {
				throw new CannotCreateDataSenderException(e);
			} catch (ComponentConnectionRejectedException e) {
				throw new CannotCreateDataSenderException(e);
			}
		}
	}

	/**
	 * <code>Interpret</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	private static class Interpret implements ExpressionVisitor<CodeValue, Exception> {

		private final EcosystemFactoryImpl factory;
		private final Map<String, CodeValue> environment;

		/**
		 * Constructor
		 * 
		 * @param environement
		 */
		private Interpret(EcosystemFactoryImpl factory, Map<String, CodeValue> environement) {
			super();
			this.factory = factory;
			this.environment = environement;
		}

		public CodeValue visit(final List<Expression> expressions) throws Exception {
			final CodeValue[] values = new CodeValue[expressions.size()];
			final Interpret interpret = new Interpret(factory, environment);
			for (int i = 0; i < values.length; i++) {
				values[i] = expressions.get(i).visit(interpret);
			}
			if (values.length == 1) {
				return values[0];
			} else {
				return new FlowValue(values);
			}
		}

		@Override
		public CodeValue visit(final Reference expression) throws Exception {
			if (factory.importations.containsKey(expression.getValue())) {
				return new ComponentValue(environment, factory.importations.get(expression.getValue()));
			} else {
				return null; // TODO
			}
		}

		@Override
		public CodeValue visit(Atom expression) throws CannotCreateComponentException {
			return new ConstantValue(expression.getValue());
		}

		@Override
		public CodeValue visit(Apply expression) throws Exception {
			assert expression.getExpressions().size() == 2;

			final Expression argument0 = expression.getExpressions().get(0);
			final Expression argument1 = expression.getExpressions().get(1);

			if (argument0 instanceof Function) {
				final Function function = (Function) argument0;
				final CodeValue result = argument1.visit(this);

				final Map<String, CodeValue> newEnvironment = new HashMap<String, CodeValue>();
				newEnvironment.putAll(environment);
				newEnvironment.put(function.getParameter(), result);

				final Interpret interpret = new Interpret(factory, newEnvironment);
				return interpret.visit(function.getExpressions());
			} else {
				throw new Exception("Evalutation Error : TODO : Waiting for a function");
			}
		}

		@Override
		public CodeValue visit(Function expression) throws Exception {
			final Map<String, CodeValue> newEnvironment = new HashMap<String, CodeValue>();
			newEnvironment.putAll(environment);
			return new ClosureValue(expression, newEnvironment);
		}
	}

	/**
	 * <code>PipelineImportEntry</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	private static class PipelineImportEntry implements ImportEntry<PipelineComponent> {
		private final EcosystemFactory factory;
		private final Class<?> component;

		/**
		 * Constructor
		 * 
		 * @param component
		 */
		private PipelineImportEntry(EcosystemFactory factory, Class component) {
			super();
			this.factory = factory;
			this.component = component;
		}

		@Override
		public PipelineComponent create(String... parameters) throws CannotCreateComponentException {
			return PipelineFactory.create(factory, component, parameters);
		}
	}

	/**
	 * <code>TerminalImportEntry</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	private static class InitialImportEntry implements ImportEntry<InitialComponent> {
		private final EcosystemFactory factory;
		private final Class<?> component;

		/**
		 * Constructor
		 * 
		 * @param component
		 */
		private InitialImportEntry(EcosystemFactory factory, Class component) {
			super();
			this.factory = factory;
			this.component = component;
		}

		@Override
		public InitialComponent create(String... parameters) throws CannotCreateComponentException {
			return InitialFactory.create(factory, component, parameters);
		}
	}

	/**
	 * <code>TerminalImportEntry</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	private static class TerminaImportEntry implements ImportEntry<TerminalComponent> {
		private final EcosystemFactory factory;
		private final Class<?> component;

		/**
		 * Constructor
		 * 
		 * @param component
		 */
		private TerminaImportEntry(EcosystemFactory factory, Class component) {
			super();
			this.factory = factory;
			this.component = component;
		}

		@Override
		public TerminalComponent create(String... parameters) throws CannotCreateComponentException {
			return TerminalFactory.create(factory, component, parameters);
		}
	}

	/**
	 * Importations
	 */
	private final Map<String, ImportEntry<?>> importations;

	/**
	 * Importations
	 */
	private final Map<String, CodeValue> definitions;

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

	{
		this.serverFactory = new ServerFactory();
		this.clientFactory = new ClientFactory();
		this.componentLinkManager = new ComponentLinkManagerImpl();
		this.classLoader = EcosystemFactoryImpl.class.getClassLoader();

		this.importations = new HashMap<String, ImportEntry<?>>();
		this.definitions = new HashMap<String, CodeValue>();
	}

	/**
	 * Constructor
	 */
	private EcosystemFactoryImpl() {
		super();
		// TODO Auto-generated constructor stub
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
	private Component create(final Component source, final CodeValue value) throws CannotCreateComponentException, CannotCreateDataSenderException, ComponentConnectionRejectedException {
		Component current = source;

		if (current == null) {
			throw new CannotCreateDataSenderException();
		} else {
			return current;
		}
	}

	/**
	 * @param loader
	 * @param factory
	 */
	private void loadImportations(EcosystemModel ecosystemModel) {
		for (Import importation : ecosystemModel.getImportations()) {
			try {
				final Class<?> aClass = classLoader.loadClass(importation.getElement());
				if (aClass.isAnnotationPresent(ContrailClient.class)) {
					final ContrailClient annotation = aClass.getAnnotation(ContrailClient.class);
					if (Client.class.isAssignableFrom(aClass)) {
						this.getClientFactory().declareScheme(annotation.scheme(), aClass);
					} else {
						// TODO -- LOG ERROR
					}
				} else if (aClass.isAnnotationPresent(ContrailServer.class)) {
					final ContrailServer annotation = aClass.getAnnotation(ContrailServer.class);
					if (Server.class.isAssignableFrom(aClass)) {
						this.getServerFactory().declareScheme(annotation.scheme(), aClass);
					} else {
						// TODO -- LOG ERROR
					}
				} else if (aClass.isAnnotationPresent(ContrailPipeline.class)) {
					final ContrailPipeline annotation = aClass.getAnnotation(ContrailPipeline.class);
					if (PipelineFactory.class.isAssignableFrom(aClass)) {
						final String name;
						if (importation.getAlias() != null) {
							name = importation.getAlias();
						} else {
							name = annotation.name();
						}
						this.importations.put(name, new PipelineImportEntry(this, aClass));
					} else {
						// TODO -- LOG ERROR
					}
				} else if (aClass.isAnnotationPresent(ContrailTerminal.class)) {
					final ContrailTerminal annotation = aClass.getAnnotation(ContrailTerminal.class);
					if (TerminalFactory.class.isAssignableFrom(aClass)) {
						final String name;
						if (importation.getAlias() != null) {
							name = importation.getAlias();
						} else {
							name = annotation.name();
						}
						this.importations.put(name, new TerminaImportEntry(this, aClass));
					} else {
						// TODO -- LOG ERROR
					}
				} else if (aClass.isAnnotationPresent(ContrailInitial.class)) {
					final ContrailInitial annotation = aClass.getAnnotation(ContrailInitial.class);
					if (TerminalFactory.class.isAssignableFrom(aClass)) {
						final String name;
						if (importation.getAlias() != null) {
							name = importation.getAlias();
						} else {
							name = annotation.name();
						}
						this.importations.put(name, new InitialImportEntry(this, aClass));
					} else {
						// TODO -- LOG ERROR
					}
				}
			} catch (ClassNotFoundException ignore) {
				// Consume
			}
		}
	}

	/**
	 * Main method called whether an ecosystem must be created
	 * 
	 * @param ecosystemModel
	 * @throws Exception
	 * @throws ClassNotFoundException
	 */
	public static org.wolfgang.contrail.ecosystem.Ecosystem build(EcosystemModel ecosystemModel) throws Exception {

		final EcosystemFactoryImpl factory = new EcosystemFactoryImpl();

		// Check and load importations
		factory.loadImportations(ecosystemModel);

		final Interpret interpret = new Interpret(factory, new HashMap<String, CodeValue>());

		// Check on load definitions
		for (Definition definition : ecosystemModel.getDefinitions()) {
			factory.definitions.put(definition.getName(), interpret.visit(definition.getExpressions()));
		}

		final EcosystemImpl ecosystemImpl = new EcosystemImpl();

		// Create and Load the binders
		for (Bind bind : ecosystemModel.getBinders()) {
			final Class<?> typeIn = TypeUtils.getType(bind.getTypeIn());
			final Class<?> typeOut = TypeUtils.getType(bind.getTypeOut());
			final RegisteredUnitEcosystemKey key = new RegisteredUnitEcosystemKey(bind.getName(), typeIn, typeOut);
			final CodeValue flow = interpret.visit(bind.getExpressions());
			ecosystemImpl.addBinder(key, new DataSenderFactoryImpl(factory, flow));
		}

		// Create and Start the starters
		for (Starter starter : ecosystemModel.getStarters()) {
			final CodeValue flow = interpret.visit(starter.getExpressions());
			factory.create(null, flow);
		}

		return ecosystemImpl;
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