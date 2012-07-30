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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.bound.TerminalComponent;

/**
 * <code>TerminalFactory</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TerminalFactory {

	/**
	 * @param classLoader
	 * @param factoryName
	 * @param array
	 * @return
	 * @throws CannotCreateComponentException
	 */
	@SuppressWarnings("rawtypes")
	public static TerminalComponent create(EcosystemFactory ecosystemFactory, String factoryName, String[] parameters) throws CannotCreateComponentException {
		try {
			final Class<?> component = ecosystemFactory.getClassLoader().loadClass(factoryName);
			try {
				final Constructor<?> constructor = component.getConstructor(EcosystemFactory.class, String[].class);
				return (TerminalComponent) constructor.newInstance(new Object[] { ecosystemFactory, parameters });
			} catch (NoSuchMethodException e1) {
				try {
					final Constructor<?> constructor = component.getConstructor(String[].class);
					return (TerminalComponent) constructor.newInstance(new Object[] { parameters });
				} catch (NoSuchMethodException e2) {
					return (TerminalComponent) component.newInstance();
				}
			}
		} catch (InvocationTargetException e) {
			throw new CannotCreateComponentException(e.getCause());
		} catch (Exception e) {
			throw new CannotCreateComponentException(e);
		}

	}

}
