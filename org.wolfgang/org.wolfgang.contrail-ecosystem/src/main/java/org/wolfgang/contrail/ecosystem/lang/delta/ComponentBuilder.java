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
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;

import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.annotation.ContrailArgument;
import org.wolfgang.contrail.component.annotation.ContrailConstructor;
import org.wolfgang.contrail.connection.ContextFactory;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValue;
import org.wolfgang.contrail.ecosystem.lang.code.ConstantValue;
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
public class ComponentBuilder {

	@SuppressWarnings("unchecked")
	static <E> Converter<E> getConverter(ComponentLinkManager linkManager, Class<E> type) {
		final Class<ComponentBuilder> aClass = ComponentBuilder.class;
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
	 * Method providing the constructor defined
	 * 
	 * @param component
	 *            The class
	 * @return a constructor (Never <code>null</code>)
	 */
	@SuppressWarnings("rawtypes")
	static Constructor[] getDeclaredConstructor(Class<?> component) {
		final TreeSet<Constructor> treeSet = new TreeSet<Constructor>(new Comparator<Constructor>() {
			@Override
			public int compare(Constructor arg0, Constructor arg1) {
				int n = arg0.getParameterTypes().length - arg1.getParameterTypes().length;
				if (n < 0) {
					return 1;
				} else if (n > 0) {
					return -1;
				} else {
					return 0;
				}
			}
		});

		final Collection<Constructor> annoted = treeSet;
		final Constructor<?>[] constructors = component.getConstructors();

		for (Constructor<?> constructor : constructors) {
			if (constructor.isAnnotationPresent(ContrailConstructor.class)) {
				annoted.add(constructor);
			}
		}

		return annoted.toArray(new Constructor[annoted.size()]);
	}

	static ContrailArgument getDeclaredParameter(Annotation[] parameterTypes) {
		for (Annotation annotation : parameterTypes) {
			if (ContrailArgument.class.isAssignableFrom(annotation.annotationType())) {
				return (ContrailArgument) annotation;
			}
		}

		return null;
	}

	static Object[] getParameters(ComponentLinkManager linkManager, Constructor<?> constructor, Map<String, CodeValue> environment) throws Exception {
		final Annotation[][] parameterTypes = constructor.getParameterAnnotations();
		final Object[] parameters = new Object[parameterTypes.length];

		for (int i = 0; i < parameters.length; i++) {
			final ContrailArgument annotation = getDeclaredParameter(parameterTypes[i]);
			if (annotation != null && environment.containsKey(annotation.value())) {
				final CodeValue codeValue = environment.get(annotation.value());
				parameters[i] = create(linkManager, constructor.getParameterTypes()[i], codeValue);
			} else {
				return null;
			}
		}

		return parameters;
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
	 * @param classLoader
	 * @param factoryName
	 * @param array
	 * @return
	 * @throws CannotCreateComponentException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T create(ComponentLinkManager linkManager, ContextFactory ecosystemFactory, Class<?> component, Map<String, CodeValue> environment) throws CannotCreateComponentException {
		try {
			final Constructor[] constructors = getDeclaredConstructor(component);

			environment.put("context", new ConstantValue(ecosystemFactory));

			Exception raised = new CannotCreateComponentException("TODO: Constructor Definition not found");

			for (Constructor<?> constructor : constructors) {
				Object[] parameters;

				try {
					parameters = getParameters(linkManager, constructor, environment);
				} catch (Exception exception) {
					parameters = null;
					raised = exception;
				}

				if (parameters != null) {
					return (T) constructor.newInstance(parameters);
				}
			}

			throw raised;

		} catch (CannotCreateComponentException e) {
			throw e;
		} catch (InvocationTargetException e) {
			throw new CannotCreateComponentException(e.getCause());
		} catch (Exception e) {
			throw new CannotCreateComponentException(e);
		}
	}
}
