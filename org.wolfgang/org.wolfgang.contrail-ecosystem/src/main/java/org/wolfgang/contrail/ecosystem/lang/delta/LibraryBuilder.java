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

package org.wolfgang.contrail.ecosystem.lang.delta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import org.wolfgang.common.message.Message;
import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.common.utils.ExceptionUtils;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.connection.ContextFactory;
import org.wolfgang.contrail.ecosystem.annotation.ContrailArgument;
import org.wolfgang.contrail.ecosystem.lang.EcosystemSymbolTable;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValue;
import org.wolfgang.contrail.ecosystem.lang.delta.converter.CoercionConverter;
import org.wolfgang.contrail.ecosystem.lang.delta.converter.ConversionException;
import org.wolfgang.contrail.ecosystem.lang.delta.converter.Converter;
import org.wolfgang.contrail.link.ComponentLinkManager;

/**
 * <code>LibraryBuilder</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class LibraryBuilder {

	/**
	 * 
	 * @param linkManager
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static <E> Converter<E> getConverter(ComponentLinkManager linkManager, Class<E> type) {
		final Class<LibraryBuilder> aClass = LibraryBuilder.class;
		final String name = aClass.getPackage().getName() + ".converter." + type.getSimpleName() + "Converter";
		try {
			final Class<?> converter = aClass.getClassLoader().loadClass(name);
			final Constructor<?> constructor = converter.getConstructor(ComponentLinkManager.class);
			return (Converter<E>) constructor.newInstance(linkManager);
		} catch (ClassNotFoundException e) {
			return new CoercionConverter<E>(type);
		} catch (InstantiationException e) {
			return new CoercionConverter<E>(type);
		} catch (IllegalAccessException e) {
			return new CoercionConverter<E>(type);
		} catch (SecurityException e) {
			return new CoercionConverter<E>(type);
		} catch (NoSuchMethodException e) {
			return new CoercionConverter<E>(type);
		} catch (IllegalArgumentException e) {
			return new CoercionConverter<E>(type);
		} catch (InvocationTargetException e) {
			return new CoercionConverter<E>(type);
		}
	}

	/**
	 * Method called when a given code value must be converted
	 * 
	 * @param linkManager
	 * @param type
	 * @param value
	 * @return
	 * @throws ConversionException
	 */
	public static <T> T convert(ComponentLinkManager linkManager, Class<T> type, CodeValue value) throws ConversionException {
		return getConverter(linkManager, type).performConversion(value);
	}

	/**
	 * Method providing the constructor defined
	 * 
	 * @param component
	 *            The class
	 * @return a constructor (Never <code>null</code>)
	 */
	public static Method[] getDeclaredMethods(String name, Class<?> component) {
		final TreeSet<Method> treeSet = new TreeSet<Method>(new Comparator<Method>() {
			@Override
			public int compare(Method arg0, Method arg1) {
				final int nameComparison = arg0.getName().compareTo(arg1.getName());
				if (nameComparison == 0) {
					int n = arg0.getParameterTypes().length - arg1.getParameterTypes().length;
					if (n < 0) {
						return 1;
					} else if (n > 0) {
						return -1;
					} else {
						return 0;
					}
				} else {
					return nameComparison;
				}
			}
		});

		final Collection<Method> annoted = treeSet;
		final Method[] methods = component.getDeclaredMethods();

		for (Method method : methods) {
			if ((name == null || method.getName().equals(name)) && Modifier.isPublic(method.getModifiers())) {
				annoted.add(method);
			}
		}

		return annoted.toArray(new Method[annoted.size()]);
	}

	/**
	 * 
	 * @param parameterTypes
	 * @return
	 */
	static ContrailArgument getDeclaredParameter(Annotation[] parameterTypes) {
		for (Annotation annotation : parameterTypes) {
			if (ContrailArgument.class.isAssignableFrom(annotation.annotationType())) {
				return (ContrailArgument) annotation;
			}
		}

		return null;
	}

	/**
	 * 
	 * @param linkManager
	 * @param constructor
	 * @param environment
	 * @return
	 * @throws CannotCreateComponentException
	 * @throws ConversionException
	 */
	static Object[] getParameters(ComponentLinkManager linkManager, Class<?>[] parameters, CodeValue[] values) throws ConversionException {
		assert parameters.length == values.length;

		final Object[] objects = new Object[parameters.length];

		for (int i = 0; i < parameters.length; i++) {
			objects[i] = convert(linkManager, parameters[i], values[i]);
		}

		return parameters;
	}

	public static String[] getParametersName(Method method) {
		final Annotation[][] parameterTypes = method.getParameterAnnotations();
		final String[] parameters = new String[parameterTypes.length];

		for (int i = 0; i < parameters.length; i++) {
			final ContrailArgument annotation = getDeclaredParameter(parameterTypes[i]);
			if (annotation == null) {
				parameters[i] = "$_" + i;
			} else {
				parameters[i] = annotation.value();
			}
		}

		return parameters;
	}

	/**
	 * 
	 * @param linkManager
	 * @param method
	 * @param environment
	 * @return
	 * @throws CannotCreateComponentException
	 * @throws ConversionException
	 */
	static Object[] getParameters(Method method, ComponentLinkManager linkManager, EcosystemSymbolTable symbolTable) throws CannotCreateComponentException, ConversionException {
		final Annotation[][] parameterTypes = method.getParameterAnnotations();
		final Object[] parameters = new Object[parameterTypes.length];

		for (int i = 0; i < parameters.length; i++) {
			final ContrailArgument annotation = getDeclaredParameter(parameterTypes[i]);
			if (annotation == null) {
				final CodeValue codeValue = symbolTable.getDefinition("$_" + i);
				parameters[i] = convert(linkManager, method.getParameterTypes()[i], codeValue);
			} else if (!symbolTable.hasDefinition(annotation.value())) {
				final Message message = MessagesProvider.message("org/wolfgang/contrail/ecosystem", "method.library.argument.undefined");
				throw new ConversionException(message.format(method.getDeclaringClass().getName(), method.getName(), annotation.value()));
			} else {
				final CodeValue codeValue = symbolTable.getDefinition(annotation.value());
				parameters[i] = convert(linkManager, method.getParameterTypes()[i], codeValue);
			}
		}

		return parameters;
	}

	/**
	 * @param classLoader
	 * @param factoryName
	 * @param array
	 * @return
	 * @throws CannotCreateComponentException
	 */
	@SuppressWarnings({ "unchecked" })
	public static <T> T create(Object component, Method method, ContextFactory ecosystemFactory, EcosystemSymbolTable symbolTable) throws CannotCreateComponentException {
		try {
			return (T) method.invoke(component, getParameters(method, ecosystemFactory.getLinkManager(), symbolTable));
		} catch (Exception e) {
			throw new CannotCreateComponentException(ExceptionUtils.getInitialCause(e));
		}
	}

	/**
	 * @param classLoader
	 * @param factoryName
	 * @param array
	 * @return
	 * @throws CannotCreateComponentException
	 */
	@SuppressWarnings({ "unchecked" })
	public static <T> T create(Object component, String name, ContextFactory ecosystemFactory, EcosystemSymbolTable symbolTable) throws CannotCreateComponentException {
		final Method[] methods = getDeclaredMethods(name, component.getClass());
		Throwable reason = null;
		
		for (Method method : methods) {
			try {
				return (T) create(component, method, ecosystemFactory, symbolTable);
			} catch (CannotCreateComponentException e) {
				reason = ExceptionUtils.getInitialCause(e);
			}
		}

		final Message message = MessagesProvider.message("org/wolfgang/contrail/ecosystem", "method.library.not.found");
		throw new CannotCreateComponentException(message.format(reason==null?name:reason.getMessage()), reason);
	}
}
