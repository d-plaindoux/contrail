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

package org.wolfgang.contrail.codec.jaxb.json;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.wolfgang.contrail.codec.payload.Bytes;
import org.wolfgang.contrail.component.pipeline.transducer.DataTransducer;
import org.wolfgang.contrail.component.pipeline.transducer.DataTransducerException;

/**
 * <code>Decoder</code> is able to transform a byte stream to an object using
 * JAXB mechanisms
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class Decoder implements DataTransducer<Bytes, Object> {

	/**
	 * Types used for the JAXB decoding process
	 */
	private final Class<?>[] types;

	/**
	 * Constructor
	 * 
	 * @param types
	 *            Types used for the decoding
	 */
	public Decoder(Class<?>[] types) {
		super();
		this.types = types.clone();
	}

	@Override
	public List<Object> transform(Bytes source) throws DataTransducerException {
		final InputStream stream = new ByteArrayInputStream(source.getContent());
		try {
			// TODO - Cache Object
			final JAXBContext context = JAXBContext.newInstance(types);
			final Unmarshaller unmarshaller = context.createUnmarshaller();
			return Arrays.asList(unmarshaller.unmarshal(stream));
		} catch (JAXBException e) {
			throw new DataTransducerException(e);
		} finally {
			try {
				stream.close();
			} catch (IOException consume) {
				// Ignore
			}
		}
	}

	@Override
	public List<Object> finish() throws DataTransducerException {
		return Arrays.asList();
	}
}