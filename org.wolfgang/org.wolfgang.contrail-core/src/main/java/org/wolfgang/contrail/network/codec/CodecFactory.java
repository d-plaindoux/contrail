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

package org.wolfgang.contrail.network.codec;

import java.lang.reflect.InvocationTargetException;

import org.wolfgang.contrail.component.transducer.DataTransducer;

/**
 * <code>CodecFactory</code> is the main interface used for encoder and decoder
 * creation.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface CodecFactory<U, D> {

	public static class Loader {
		public static CodecFactory<?, ?> load(ClassLoader loader, String name, String[] parameters) throws CodecFactoryCreationException {
			try {
				final Class<?> codec = loader.loadClass(name);
				try {
					return (CodecFactory<?, ?>) codec.getConstructor(String[].class).newInstance(parameters);
				} catch (NoSuchMethodException e) {
					return (CodecFactory<?, ?>) codec.newInstance();
				}
			} catch (Exception e) {
				throw new CodecFactoryCreationException(e);
			}
		}
	}

	/**
	 * Method providing the decoder
	 * 
	 * @return a decoding data transducer
	 */
	DataTransducer<U, D> getDecoder();

	/**
	 * Method providing the encoder
	 * 
	 * @return an encoding data transducer
	 */
	DataTransducer<D, U> getEncoder();

}
