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

package org.wolfgang.contrail.component.pipeline.transducer;

import java.lang.reflect.Constructor;


/**
 * <code>CodecFactory</code> is the main interface used for encoder and decoder
 * creation.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface TransducerFactory<U, D> {

	/**
	 * <code>Loader</code> is dedicated to CoDec creation. This creation is done
	 * using parameters if defined otherwise default constructor is invoked.
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public static class Loader {
		public static TransducerFactory<?, ?> load(ClassLoader loader, String name, String[] parameters) throws TransducerFactoryCreationException {
			try {
				final Class<?> codec = loader.loadClass(name);
				try {
					final Constructor<?> constructor = codec.getConstructor(String[].class);
					return (TransducerFactory<?, ?>) constructor.newInstance(new Object[] { parameters });
				} catch (NoSuchMethodException e) {
					return (TransducerFactory<?, ?>) codec.newInstance();
				}
			} catch (Exception e) {
				throw new TransducerFactoryCreationException(e);
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

	/**
	 * Method called whether a transducer component must be created
	 * 
	 * @return a transducer component
	 */
	TransducerComponent<U, U, D, D> createComponent();
}
