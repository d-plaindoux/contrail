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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.wolfgang.common.message.Message;
import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.common.utils.Coercion;
import org.wolfgang.common.utils.ExceptionUtils;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentFactory;
import org.wolfgang.contrail.connection.Clients;
import org.wolfgang.contrail.connection.ContextFactory;
import org.wolfgang.contrail.connection.Servers;
import org.wolfgang.contrail.ecosystem.EcosystemImpl;
import org.wolfgang.contrail.ecosystem.key.EcosystemKeyFactory;
import org.wolfgang.contrail.ecosystem.lang.code.ClosureValue;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValue;
import org.wolfgang.contrail.ecosystem.lang.code.ConstantValue;
import org.wolfgang.contrail.ecosystem.lang.delta.Bootstrap;
import org.wolfgang.contrail.ecosystem.lang.delta.CoreFunctions;
import org.wolfgang.contrail.ecosystem.lang.delta.LibraryBuilder;
import org.wolfgang.contrail.ecosystem.lang.delta.converter.ConversionException;
import org.wolfgang.contrail.ecosystem.lang.model.Definition;
import org.wolfgang.contrail.ecosystem.lang.model.EcosystemModel;
import org.wolfgang.contrail.ecosystem.lang.model.Import;
import org.wolfgang.contrail.link.ComponentLinkManager;
import org.wolfgang.contrail.link.ComponentLinkManagerImpl;

/**
 * <code>EcosystemFactory</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class EcosystemFactoryImpl extends EcosystemImpl implements ContextFactory {

	/**
	 * Symbol table
	 */
	private final EcosystemSymbolTableImpl symbolTableImpl;

	/**
	 * Server factory
	 */
	private final Servers serverFactory;

	/**
	 * Client factory
	 */
	private final Clients clientFactory;

	/**
	 * The class loader to use when components must be created
	 */
	private final ClassLoader classLoader;

	/**
	 * The code interpreter
	 */
	private final EcosystemCodeValueGenerator interpreter;

	{
		this.symbolTableImpl = new EcosystemSymbolTableImpl();
		this.serverFactory = new Servers();
		this.clientFactory = new Clients();
		this.classLoader = EcosystemFactoryImpl.class.getClassLoader();
		this.interpreter = new EcosystemCodeValueGenerator(this, this.symbolTableImpl);
	}

	/**
	 * Constructor
	 */
	private EcosystemFactoryImpl() {
		super(new ComponentLinkManagerImpl());
	}

	/**
	 * Method called whether an ecosystem importation must be managed. This
	 * management is done using annotations.
	 * 
	 * @param loader
	 * @param factory
	 */
	@SuppressWarnings("rawtypes")
	private void loadImportation(Logger logger, Object object) {
		final Class<? extends Object> aClass = object.getClass();
		try {
			final Method[] initMethods = LibraryBuilder.getDeclaredMethods("init", aClass);
			for (Method init : initMethods) {
				LibraryBuilder.create(object, init, this, this.symbolTableImpl);
			}
			final Method[] declaredMethods = LibraryBuilder.getDeclaredMethods(null, aClass);
			for (Method method : declaredMethods) {
				this.symbolTableImpl.putImportation(method.getName(), new FunctionImportation(this, object, method.getName()));
			}
		} catch (CannotCreateComponentException e) {
			final Message message = MessagesProvider.message("org.wolfgang.contrail.ecosystem", "no.component");
			logger.log(Level.WARNING, message.format(), e);
		}
	}

	/**
	 * Method called whether an ecosystem importation must be managed. This
	 * management is done using annotations.
	 * 
	 * @param loader
	 * @param factory
	 */
	private void loadImportation(Logger logger, Import importation) {
		try {
			final Class<?> aClass = classLoader.loadClass(importation.getElement());
			final Constructor<?> constructor = aClass.getConstructor(ContextFactory.class);
			final Object object = constructor.newInstance(this);
			this.loadImportation(logger, object);
		} catch (ClassNotFoundException e) {
			final Message message = MessagesProvider.message("org.wolfgang.contrail.ecosystem", "undefined.type");
			logger.log(Level.WARNING, message.format(importation.getElement(), e.getClass().getSimpleName()));
		} catch (SecurityException e) {
			// TODO
			e.printStackTrace();
			final Message message = MessagesProvider.message("org.wolfgang.contrail.ecosystem", "undefined.type");
			logger.log(Level.WARNING, message.format(importation.getElement(), e.getClass().getSimpleName()));
		} catch (NoSuchMethodException e) {
			final Message message = MessagesProvider.message("org.wolfgang.contrail.ecosystem", "undefined.type");
			logger.log(Level.WARNING, message.format(importation.getElement(), e.getClass().getSimpleName()));
		} catch (IllegalArgumentException e) {
			final Message message = MessagesProvider.message("org.wolfgang.contrail.ecosystem", "undefined.type");
			logger.log(Level.WARNING, message.format(importation.getElement(), e.getClass().getSimpleName()));
		} catch (InstantiationException e) {
			final Message message = MessagesProvider.message("org.wolfgang.contrail.ecosystem", "undefined.type");
			logger.log(Level.WARNING, message.format(importation.getElement(), e.getClass().getSimpleName()));
		} catch (IllegalAccessException e) {
			final Message message = MessagesProvider.message("org.wolfgang.contrail.ecosystem", "undefined.type");
			logger.log(Level.WARNING, message.format(importation.getElement(), e.getClass().getSimpleName()));
		} catch (InvocationTargetException e) {
			final Message message = MessagesProvider.message("org.wolfgang.contrail.ecosystem", "undefined.type");
			logger.log(Level.WARNING, message.format(importation.getElement(), e.getCause().getClass().getSimpleName()));
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
		loadImportation(logger, new Bootstrap(this, this.symbolTableImpl));

		// TODO -- Remove this pervasive definition ASAP
		loadImportation(logger, new CoreFunctions(this));

		for (Import importation : ecosystemModel.getImportations()) {
			loadImportation(logger, importation);
		}
	}

	/**
	 * Main method called whether an ecosystem must be created
	 * 
	 * @param ecosystemModel
	 * @throws Exception
	 * @throws ClassNotFoundException
	 */
	public void buildEcosystem(Logger logger, EcosystemModel ecosystemModel) throws Exception {

		// Check and load importations
		this.loadImportations(logger, ecosystemModel);

		// Check on load definitions
		for (Definition definition : ecosystemModel.getDefinitions()) {
			final CodeValue generated = this.interpreter.visit(definition.getExpressions());

			try {
				final Component component = LibraryBuilder.convert(getLinkManager(), Component.class, generated);
				EcosystemFactoryImpl.this.addActiveComponent(component);
			} catch (ConversionException consume) {
				// Ignore
			}

			final String name = definition.getName();

			if (name != null) {
				this.symbolTableImpl.putDefinition(name, generated);
				final ComponentFactory factory = new ComponentFactory() {
					@Override
					public ComponentLinkManager getLinkManager() {
						return EcosystemFactoryImpl.this.getLinkManager();
					}

					@Override
					public Component create(Object... arguments) throws CannotCreateComponentException {

						CodeValue interpreted = generated;

						for (Object argument : arguments) {
							if (Coercion.canCoerce(interpreted, ClosureValue.class)) {
								final ClosureValue closure = Coercion.coerce(interpreted, ClosureValue.class);
								try {
									interpreted = closure.apply(new ConstantValue(argument));
								} catch (EcosystemCodeValueGeneratorException e) {
									throw new CannotCreateComponentException(e);
								}
							} else {
								final Message message = MessagesProvider.message("org/wolfgang/contrail/ecosystem", "function.required");
								throw new CannotCreateComponentException(message.format());
							}
						}

						try {
							final Component component = LibraryBuilder.convert(getLinkManager(), Component.class, interpreted);
							EcosystemFactoryImpl.this.addActiveComponent(component);
							return component;
						} catch (ConversionException e) {
							throw new CannotCreateComponentException(ExceptionUtils.getInitialCause(e));
						}
					}
				};

				this.addBinder(EcosystemKeyFactory.key(definition.getName()), factory);
			}

		}
	}

	// EcosystemFactory public definitions

	@Override
	public ClassLoader getClassLoader() {
		return this.classLoader;
	}

	@Override
	public Servers getServerFactory() {
		return serverFactory;
	}

	@Override
	public Clients getClientFactory() {
		return clientFactory;
	}

	public EcosystemCodeValueGenerator getInterpreter() {
		return interpreter;
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
		final EcosystemFactoryImpl ecosystemFactoryImpl = new EcosystemFactoryImpl();
		ecosystemFactoryImpl.buildEcosystem(logger, ecosystemModel);
		return ecosystemFactoryImpl;
	}
}