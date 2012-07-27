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

package org.wolfgang.contrail.component.pipeline;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.PipelineComponent;
import org.wolfgang.contrail.component.pipeline.transducer.TransducerFactory;

/**
 * <code>PipelineFactory</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class PipelineFactory {

	/**
	 * Constructor
	 */
	private PipelineFactory() {
		super();
	}

	@SuppressWarnings({ "rawtypes" })
	public static PipelineComponent create(ClassLoader loader, String factoryName, String[] parameters) throws CannotCreateComponentException {
		try {
			final Class<?> component = loader.loadClass(factoryName);
			if (TransducerFactory.class.isAssignableFrom(component)) {
				TransducerFactory factory = null;
				try {
					final Constructor<?> constructor = component.getConstructor(String[].class);
					factory = (TransducerFactory) constructor.newInstance(new Object[] { parameters });
				} catch (NoSuchMethodException e) {
					factory = (TransducerFactory) component.newInstance();
				}
				return factory.createComponent();
			} else {
				try {
					final Constructor<?> constructor = component.getConstructor(String[].class);
					return (PipelineComponent) constructor.newInstance(new Object[] { parameters });
				} catch (NoSuchMethodException e) {
					return (PipelineComponent) component.newInstance();
				}
			}
		} catch (InvocationTargetException e) {
			throw new CannotCreateComponentException(e.getCause());
		} catch (Exception e) {
			throw new CannotCreateComponentException(e);
		}
	}
}
