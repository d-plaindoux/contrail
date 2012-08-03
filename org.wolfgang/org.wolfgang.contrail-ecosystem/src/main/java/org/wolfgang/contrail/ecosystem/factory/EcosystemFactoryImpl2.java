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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wolfgang.common.lang.TypeUtils;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.PipelineComponent;
import org.wolfgang.contrail.component.annotation.ContrailClient;
import org.wolfgang.contrail.component.annotation.ContrailInitial;
import org.wolfgang.contrail.component.annotation.ContrailPipeline;
import org.wolfgang.contrail.component.annotation.ContrailServer;
import org.wolfgang.contrail.component.annotation.ContrailTerminal;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.connection.Client;
import org.wolfgang.contrail.connection.ClientFactory;
import org.wolfgang.contrail.connection.Server;
import org.wolfgang.contrail.connection.ServerFactory;
import org.wolfgang.contrail.ecosystem.EcosystemImpl;
import org.wolfgang.contrail.ecosystem.key.RegisteredUnitEcosystemKey;
import org.wolfgang.contrail.ecosystem.model2.Apply;
import org.wolfgang.contrail.ecosystem.model2.Atom;
import org.wolfgang.contrail.ecosystem.model2.Bind;
import org.wolfgang.contrail.ecosystem.model2.Definition;
import org.wolfgang.contrail.ecosystem.model2.EcosystemModel;
import org.wolfgang.contrail.ecosystem.model2.Expression;
import org.wolfgang.contrail.ecosystem.model2.ExpressionVisitor;
import org.wolfgang.contrail.ecosystem.model2.Function;
import org.wolfgang.contrail.ecosystem.model2.Import;
import org.wolfgang.contrail.ecosystem.model2.Reference;
import org.wolfgang.contrail.link.ComponentLinkManager;
import org.wolfgang.contrail.link.ComponentLinkManagerImpl;

/**
 * <code>EcosystemFactory</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@SuppressWarnings("rawtypes")
public final class EcosystemFactoryImpl2 implements EcosystemFactory {

	/**
	 * <code>ImportEntry</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	private interface CodeValue {
		// Nothing
	}

	private static class FlowValue implements CodeValue {
		private final CodeValue[] values;

		/**
		 * Constructor
		 * 
		 * @param values
		 */
		FlowValue(CodeValue[] values) {
			super();
			this.values = values;
		}

		/**
		 * Return the value of values
		 * 
		 * @return the values
		 */
		CodeValue[] getValues() {
			return values;
		}
	}

	private class ComponentValue implements CodeValue {
		private final Map<String, CodeValue> environement;
		private final ImportEntry entry;

		/**
		 * Constructor
		 * 
		 * @param environement
		 * @param entry
		 */
		ComponentValue(Map<String, CodeValue> environement, ImportEntry entry) {
			super();
			this.environement = environement;
			this.entry = entry;
		}

		Component getComponent() throws CannotCreateComponentException {
			return entry.create(EcosystemFactoryImpl2.this, null /* TODO */); // Missing
		}
	}

	private static class ConstantValue implements CodeValue {
		private final String value;

		/**
		 * Constructor
		 * 
		 * @param value
		 */
		ConstantValue(String value) {
			super();
			this.value = value;
		}

		/**
		 * Return the value of value
		 * 
		 * @return the value
		 */
		String getValue() {
			return value;
		}
	}

	/**
	 * <code>ClosureValue</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	private static class ClosureValue implements CodeValue {
		private final Map<String, CodeValue> environement;
		private final Function function;

		/**
		 * Constructor
		 * 
		 * @param function
		 * @param environement
		 */
		private ClosureValue(Function function, Map<String, CodeValue> environement) {
			super();
			this.function = function;
			this.environement = environement;
		}
	}

	@SuppressWarnings("unchecked")
	private class Interpret implements ExpressionVisitor<CodeValue, Exception> {

		private final Map<String, CodeValue> environment;

		/**
		 * Constructor
		 * 
		 * @param environement
		 */
		private Interpret(Map<String, CodeValue> environement) {
			super();
			this.environment = environement;
		}

		public CodeValue visit(final List<Expression> expressions) throws Exception {
			final CodeValue[] values = new CodeValue[expressions.size()];
			final Interpret interpret = new Interpret(environment);
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
			if (importations.containsKey(expression.getValue())) {
				return new ComponentValue(environment, importations.get(expression.getValue()));
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

				final Interpret interpret = new Interpret(newEnvironment);
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
	 * <code>ImportEntry</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	private interface ImportEntry<T extends Component> {
		T create(EcosystemFactory ecosystemFactory, String... parameters) throws CannotCreateComponentException;
	}

	/**
	 * <code>PipelineImportEntry</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	@SuppressWarnings("unused")
	private static class PipelineImportEntry implements ImportEntry<PipelineComponent> {
		private final Class<?> component;

		/**
		 * Constructor
		 * 
		 * @param component
		 */
		private PipelineImportEntry(Class component) {
			super();
			this.component = component;
		}

		@Override
		public PipelineComponent create(EcosystemFactory ecosystemFactory, String[] parameters) throws CannotCreateComponentException {
			return PipelineFactory.create(ecosystemFactory, component, parameters);
		}
	}

	/**
	 * <code>TerminalImportEntry</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	@SuppressWarnings("unused")
	private static class InitialImportEntry implements ImportEntry<InitialComponent> {
		private final Class<?> component;

		/**
		 * Constructor
		 * 
		 * @param component
		 */
		private InitialImportEntry(Class component) {
			super();
			this.component = component;
		}

		@Override
		public InitialComponent create(EcosystemFactory ecosystemFactory, String[] parameters) throws CannotCreateComponentException {
			return InitialFactory.create(ecosystemFactory, component, parameters);
		}
	}

	/**
	 * <code>TerminalImportEntry</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	@SuppressWarnings("unused")
	private static class TerminaImportEntry implements ImportEntry<TerminalComponent> {
		private final Class<?> component;

		/**
		 * Constructor
		 * 
		 * @param component
		 */
		private TerminaImportEntry(Class component) {
			super();
			this.component = component;
		}

		@Override
		public TerminalComponent create(EcosystemFactory ecosystemFactory, String[] parameters) throws CannotCreateComponentException {
			return TerminalFactory.create(ecosystemFactory, component, parameters);
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
		this.definitions = new HashMap<String, EcosystemFactoryImpl2.CodeValue>();
	}

	/**
	 * Constructor
	 */
	private EcosystemFactoryImpl2() {
		super();
		// TODO Auto-generated constructor stub
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
						this.importations.put(name, new PipelineImportEntry(aClass));
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
						this.importations.put(name, new TerminaImportEntry(aClass));
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
						this.importations.put(name, new InitialImportEntry(aClass));
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
	 * @return
	 */
	private Interpret interpret() {
		return new Interpret(new HashMap<String, CodeValue>());
	}

	/**
	 * Main method called whether an ecosystem must be created
	 * 
	 * @param ecosystem
	 * @throws Exception
	 * @throws ClassNotFoundException
	 */
	public static org.wolfgang.contrail.ecosystem.Ecosystem build(EcosystemModel ecosystem) throws Exception {

		final EcosystemFactoryImpl2 ecosystemFactory = new EcosystemFactoryImpl2();

		// Check and load importations
		ecosystemFactory.loadImportations(ecosystem);

		final Interpret interpret = ecosystemFactory.interpret();

		// Check on load definitions
		for (Definition definition : ecosystem.getDefinitions()) {
			ecosystemFactory.definitions.put(definition.getName(), interpret.visit(definition.getExpressions()));
		}

		// Load the starter and the binders
		for (Bind bind : ecosystem.getBinders()) {
			final Class<?> typeIn = TypeUtils.getType(bind.getTypeIn());
			final Class<?> typeOut = TypeUtils.getType(bind.getTypeOut());
			final RegisteredUnitEcosystemKey key = new RegisteredUnitEcosystemKey(bind.getName(), typeIn, typeOut);
			final CodeValue flow = interpret.visit(bind.getExpressions());
		}

		final EcosystemImpl ecosystemImpl = new EcosystemImpl();

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