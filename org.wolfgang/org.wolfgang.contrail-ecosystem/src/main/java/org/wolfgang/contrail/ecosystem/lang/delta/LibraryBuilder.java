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
import java.util.Map;
import java.util.TreeSet;

import org.wolfgang.common.message.Message;
import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.common.utils.Coercion;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.connection.ContextFactory;
import org.wolfgang.contrail.ecosystem.annotation.ContrailArgument;
import org.wolfgang.contrail.ecosystem.annotation.ContrailMethod;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValue;
import org.wolfgang.contrail.ecosystem.lang.delta.converter.CoercionConverter;
import org.wolfgang.contrail.ecosystem.lang.delta.converter.ConversionException;
import org.wolfgang.contrail.ecosystem.lang.delta.converter.Converter;
import org.wolfgang.contrail.link.ComponentLinkManager;

/**
 * <code>TerminalFactory</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class LibraryBuilder {

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
	 * Method called when a given code value msut be converted
	 * 
	 * @param linkManager
	 * @param type
	 * @param value
	 * @return
	 * @throws ConversionException
	 */
	public static <T> T create(ComponentLinkManager linkManager, Class<T> type, CodeValue value) throws ConversionException {
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
		final Method[] methods = component.getMethods();

		for (Method method : methods) {
			if ((name == null || method.getName().equals(name)) && method.isAnnotationPresent(ContrailMethod.class)) {
				if (Modifier.isStatic(method.getModifiers())) {
					annoted.add(method);
				}
			}
		}

		return annoted.toArray(new Method[annoted.size()]);
	}

	static ContrailArgument getDeclaredParameter(Annotation[] parameterTypes) {
		for (Annotation annotation : parameterTypes) {
			if (ContrailArgument.class.isAssignableFrom(annotation.annotationType())) {
				return (ContrailArgument) annotation;
			}
		}

		return null;
	}

	static Object[] getParameters(ComponentLinkManager linkManager, Method constructor, Map<String, CodeValue> environment) throws CannotCreateComponentException, ConversionException {
		final Annotation[][] parameterTypes = constructor.getParameterAnnotations();
		final Object[] parameters = new Object[parameterTypes.length];

		for (int i = 0; i < parameters.length; i++) {
			final ContrailArgument annotation = getDeclaredParameter(parameterTypes[i]);
			if (annotation == null) {
				final Message message = MessagesProvider.message("org/wolfgang/contrail/ecosystem", "method.library.argument.error");
				throw new ConversionException(message.format(constructor.getDeclaringClass().getName(), constructor.getName(), i));
			} else if (!environment.containsKey(annotation.value())) {
				final Message message = MessagesProvider.message("org/wolfgang/contrail/ecosystem", "method.library.argument.undefined");
				throw new ConversionException(message.format(constructor.getDeclaringClass().getName(), constructor.getName(), annotation.value()));
			} else {
				final CodeValue codeValue = environment.get(annotation.value());
				parameters[i] = create(linkManager, constructor.getParameterTypes()[i], codeValue);
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
	public static <T> T create(String name, ContextFactory ecosystemFactory, Class<?> component, Map<String, CodeValue> environment) throws CannotCreateComponentException {
		try {
			final Method[] methods = getDeclaredMethods(name, component);

			for (Method method : methods) {
				try {
					final Object create = method.invoke(null, getParameters(ecosystemFactory.getLinkManager(), method, environment));
					final Object result;

					if (Coercion.canCoerce(create, NativeFunction.class)) {
						result = Coercion.coerce(create, NativeFunction.class).create(ecosystemFactory);
					} else {
						result = create;
					}

					return (T) result;
				} catch (ClassCastException e) {
					throw new CannotCreateComponentException(e);
				} catch (ConversionException ignore) {
					// Consume
				}
			}

			final Message message = MessagesProvider.message("org/wolfgang/contrail/ecosystem", "method.library.not.found");
			throw new CannotCreateComponentException(message.format(name));
		} catch (CannotCreateComponentException e) {
			throw e;
		} catch (InvocationTargetException e) {
			throw new CannotCreateComponentException(e.getCause());
		} catch (Exception e) {
			throw new CannotCreateComponentException(e);
		}
	}
}
