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
import java.util.Map;

import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.annotation.ContrailArgument;
import org.wolfgang.contrail.component.annotation.ContrailConstructor;
import org.wolfgang.contrail.connection.ContextFactory;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValue;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValueVisitor;
import org.wolfgang.contrail.ecosystem.lang.code.ConstantValue;

/**
 * <code>TerminalFactory</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class ContrailComponentFactory {

	/**
	 * Method providing the constructor defined
	 * 
	 * @param component
	 *            The class
	 * @return a constructor (Never <code>null</code>)
	 */
	private static Constructor<?> getDeclaredConstructor(Class<?> component) {
		final Constructor<?>[] constructors = component.getConstructors();
		for (Constructor<?> constructor : constructors) {
			if (constructor.isAnnotationPresent(ContrailConstructor.class)) {
				return constructor;
			}
		}

		throw new NoSuchMethodError("TODO: Constructor Definition not found");
	}

	private static ContrailArgument getDeclaredParameter(Annotation[] parameterTypes) {
		for (Annotation annotation : parameterTypes) {
			if (ContrailArgument.class.isAssignableFrom(annotation.annotationType())) {
				return (ContrailArgument) annotation;
			}
		}

		throw new NoSuchMethodError("TODO: Constructor Parameter Definition not found");
	}

	/**
	 * @param classLoader
	 * @param factoryName
	 * @param array
	 * @return
	 * @throws CannotCreateComponentException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T create(CodeValueVisitor converter, ContextFactory ecosystemFactory, Class<?> component, Map<String, CodeValue> environment) throws CannotCreateComponentException {
		try {
			final Constructor<?> constructor = getDeclaredConstructor(component);
			final Annotation[][] parameterTypes = constructor.getParameterAnnotations();
			final Object[] parameters = new Object[parameterTypes.length];

			environment.put("context", new ConstantValue(ecosystemFactory));

			for (int i = 0; i < parameters.length; i++) {
				final ContrailArgument annotation = getDeclaredParameter(parameterTypes[i]);
				if (environment.containsKey(annotation.value())) {
					final CodeValue codeValue = environment.get(annotation.value());
					parameters[i] = codeValue.visit(converter);
				} else {
					parameters[i] = null;
				}
			}

			return (T) constructor.newInstance(parameters);
		} catch (InvocationTargetException e) {
			throw new CannotCreateComponentException(e.getCause());
		} catch (Exception e) {
			throw new CannotCreateComponentException(e);
		}

	}
}
