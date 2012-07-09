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

import org.wolfgang.contrail.codec.CodecFactory;
import org.wolfgang.contrail.component.PipelineComponent;

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
	public static PipelineComponent create(ClassLoader loader, String name, String[] parameters) throws PipelineComponentCreationException {
		try {
			final Class<?> component = loader.loadClass(name);
			if (CodecFactory.class.isAssignableFrom(component)) {
				CodecFactory factory = null;
				try {
					final Constructor<?> constructor = component.getConstructor(String[].class);
					factory = (CodecFactory) constructor.newInstance(new Object[] { parameters });
				} catch (NoSuchMethodException e) {
					factory = (CodecFactory) component.newInstance();
				}
				return factory.getComponent();
			} else {
				try {
					final Constructor<?> constructor = component.getConstructor(String[].class);
					return (PipelineComponent) constructor.newInstance(new Object[] { parameters });
				} catch (NoSuchMethodException e) {
					return (PipelineComponent) component.newInstance();
				}
			}
		} catch (Exception e) {
			throw new PipelineComponentCreationException(e);
		}
	}
}