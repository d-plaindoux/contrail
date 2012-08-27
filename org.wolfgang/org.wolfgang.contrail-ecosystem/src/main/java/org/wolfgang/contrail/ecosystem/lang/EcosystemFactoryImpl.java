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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.wolfgang.common.lang.TypeUtils;
import org.wolfgang.common.message.Message;
import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.PipelineComponent;
import org.wolfgang.contrail.component.annotation.ContrailClient;
import org.wolfgang.contrail.component.annotation.ContrailInitial;
import org.wolfgang.contrail.component.annotation.ContrailPipeline;
import org.wolfgang.contrail.component.annotation.ContrailServer;
import org.wolfgang.contrail.component.annotation.ContrailTerminal;
import org.wolfgang.contrail.component.annotation.ContrailTransducer;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.pipeline.transducer.TransducerFactory;
import org.wolfgang.contrail.connection.Client;
import org.wolfgang.contrail.connection.ClientFactory;
import org.wolfgang.contrail.connection.ContextFactory;
import org.wolfgang.contrail.connection.Server;
import org.wolfgang.contrail.connection.ServerFactory;
import org.wolfgang.contrail.ecosystem.EcosystemImpl;
import org.wolfgang.contrail.ecosystem.key.EcosystemKeyFactory;
import org.wolfgang.contrail.ecosystem.key.RegisteredUnitEcosystemKey;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValue;
import org.wolfgang.contrail.ecosystem.lang.code.ConstantValue;
import org.wolfgang.contrail.ecosystem.lang.delta.ParameterCodeConverter;
import org.wolfgang.contrail.ecosystem.lang.model.Bind;
import org.wolfgang.contrail.ecosystem.lang.model.Definition;
import org.wolfgang.contrail.ecosystem.lang.model.EcosystemModel;
import org.wolfgang.contrail.ecosystem.lang.model.Import;
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
public final class EcosystemFactoryImpl implements EcosystemSymbolTable, ContextFactory {

	/**
	 * Importations
	 */
	private final Map<String, EcosystemImportation<?>> importations;

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
	private final ComponentLinkManager linkManager;

	/**
	 * The class loader to use when components must be created
	 */
	private final ClassLoader classLoader;

	/**
	 * The code interpreter
	 */
	private final EcosystemInterpreter interpreter;

	{
		this.serverFactory = new ServerFactory();
		this.clientFactory = new ClientFactory();
		this.linkManager = new ComponentLinkManagerImpl();
		this.classLoader = EcosystemFactoryImpl.class.getClassLoader();

		this.importations = new HashMap<String, EcosystemImportation<?>>();
		this.definitions = new HashMap<String, CodeValue>();
		this.interpreter = new EcosystemInterpreter(this, definitions);
	}

	/**
	 * Constructor
	 */
	private EcosystemFactoryImpl() {
		super();
	}

	/**
	 * Internal method dedicated to the component flow creation
	 * 
	 * @param source
	 *            Can be null
	 * @param items
	 *            The items to be used for the flow creation
	 * @return a component (Never <code>null</code>)
	 * @throws CannotCreateComponentException
	 * @throws EcosystemBuilderException
	 */
	Component create(final Component source, final CodeValue value) throws CannotCreateComponentException, EcosystemBuilderException {
		final EcosystemComponentBuilder builder = new EcosystemComponentBuilder(this.interpreter, linkManager, source);
		final Component current = value.visit(builder);
		if (current == null) {
			throw new CannotCreateComponentException(MessagesProvider.message("org/wolfgang/contrail/ecosystem", "no.component").format());
		} else {
			return current;
		}
	}

	/**
	 * Method called whether an ecosystem importation set must be managed. This
	 * management is done using annotations.
	 * 
	 * @param loader
	 * @param factory
	 */
	private void loadImportations(Logger logger, EcosystemModel ecosystemModel) {
		for (Import importation : ecosystemModel.getImportations()) {
			try {
				final Class<?> aClass = classLoader.loadClass(importation.getElement());
				final Message message = MessagesProvider.message("org.wolfgang.contrail.ecosystem", "incompatible.type");
				final ParameterCodeConverter codeConverter = new ParameterCodeConverter(new EcosystemComponentBuilder(this.interpreter, linkManager, null));

				if (aClass.isAnnotationPresent(ContrailClient.class)) {
					final ContrailClient annotation = aClass.getAnnotation(ContrailClient.class);
					if (Client.class.isAssignableFrom(aClass)) {
						this.getClientFactory().declareScheme(annotation.scheme(), aClass);
					} else {
						logger.log(Level.WARNING, message.format(Client.class, importation.getElement()));
					}
				} else if (aClass.isAnnotationPresent(ContrailServer.class)) {
					final ContrailServer annotation = aClass.getAnnotation(ContrailServer.class);
					if (Server.class.isAssignableFrom(aClass)) {
						this.getServerFactory().declareScheme(annotation.scheme(), aClass);
					} else {
						logger.log(Level.WARNING, message.format(Server.class, importation.getElement()));
					}
				} else if (aClass.isAnnotationPresent(ContrailPipeline.class)) {
					final ContrailPipeline annotation = aClass.getAnnotation(ContrailPipeline.class);
					if (PipelineComponent.class.isAssignableFrom(aClass)) {
						final String name;
						if (importation.getAlias() != null) {
							name = importation.getAlias();
						} else {
							name = annotation.name();
						}
						this.importations.put(name, new PipelineImportEntry(codeConverter, this, aClass));
					} else {
						logger.log(Level.WARNING, message.format(PipelineComponent.class, importation.getElement()));
					}
				} else if (aClass.isAnnotationPresent(ContrailTransducer.class)) {
					final ContrailTransducer annotation = aClass.getAnnotation(ContrailTransducer.class);
					if (TransducerFactory.class.isAssignableFrom(aClass)) {
						final String name;
						if (importation.getAlias() != null) {
							name = importation.getAlias();
						} else {
							name = annotation.name();
						}
						this.importations.put(name, new TransducerImportEntry(codeConverter, this, aClass));
					} else {
						logger.log(Level.WARNING, message.format(PipelineComponent.class, importation.getElement()));
					}
				} else if (aClass.isAnnotationPresent(ContrailTerminal.class)) {
					final ContrailTerminal annotation = aClass.getAnnotation(ContrailTerminal.class);
					if (TerminalComponent.class.isAssignableFrom(aClass)) {
						final String name;
						if (importation.getAlias() != null) {
							name = importation.getAlias();
						} else {
							name = annotation.name();
						}
						this.importations.put(name, new TerminaImportEntry(codeConverter, this, aClass));
					} else {
						logger.log(Level.WARNING, message.format(TerminalComponent.class, importation.getElement()));
					}
				} else if (aClass.isAnnotationPresent(ContrailInitial.class)) {
					final ContrailInitial annotation = aClass.getAnnotation(ContrailInitial.class);
					if (InitialComponent.class.isAssignableFrom(aClass)) {
						final String name;
						if (importation.getAlias() != null) {
							name = importation.getAlias();
						} else {
							name = annotation.name();
						}
						this.importations.put(name, new InitialImportEntry(codeConverter, this, aClass));
					} else {
						logger.log(Level.WARNING, message.format(InitialComponent.class, importation.getElement()));
					}
				} else if (importation.getAlias() != null) {
					this.definitions.put(importation.getAlias(), new ConstantValue(importation.getElement()));
				}
			} catch (ClassNotFoundException ignore) {
				final Message message = MessagesProvider.message("org.wolfgang.contrail.ecosystem", "undefined.type");
				logger.log(Level.WARNING, message.format(importation.getElement(), ignore.getClass().getSimpleName()));
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
	@SuppressWarnings("unchecked")
	private org.wolfgang.contrail.ecosystem.Ecosystem buildEcosystem(Logger logger, EcosystemModel ecosystemModel) throws Exception {

		// Check and load importations
		this.loadImportations(logger, ecosystemModel);

		// Check on load definitions
		for (Definition definition : ecosystemModel.getDefinitions()) {
			this.definitions.put(definition.getName(), this.interpreter.visit(definition.getExpressions()));
		}

		// Create the ecosystem implementation
		final EcosystemImpl ecosystemImpl = new EcosystemImpl();

		// Create and Load the binders
		for (Bind bind : ecosystemModel.getBinders()) {
			final Class<?> typeIn = TypeUtils.getType(bind.getTypeIn());
			final Class<?> typeOut = TypeUtils.getType(bind.getTypeOut());
			final RegisteredUnitEcosystemKey key = EcosystemKeyFactory.key(bind.getName(), typeIn, typeOut);
			final CodeValue flow = this.interpreter.visit(bind.getExpressions());
			ecosystemImpl.addBinder(key, new BinderDataSenderFactoryImpl(this, flow));
		}

		// Create and Start the starters
		for (Starter starter : ecosystemModel.getStarters()) {
			final CodeValue flow = this.interpreter.visit(starter.getExpressions());
			this.create(null, flow);
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

	@Override
	public boolean hasImportation(String name) {
		return this.importations.containsKey(name);
	}

	@Override
	public EcosystemImportation<?> getImportation(String name) {
		return this.importations.get(name);
	}

	// Static method

	/**
	 * Main method called whether an ecosystem must be created
	 * 
	 * @param ecosystemModel
	 * @throws Exception
	 * @throws ClassNotFoundException
	 */
	public static org.wolfgang.contrail.ecosystem.Ecosystem build(Logger logger, EcosystemModel ecosystemModel) throws Exception {
		return new EcosystemFactoryImpl().buildEcosystem(logger, ecosystemModel);
	}
}