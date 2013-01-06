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

package org.wolfgang.contrail.codec.jaxb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.wolfgang.contrail.codec.payload.Bytes;
import org.wolfgang.contrail.component.pipeline.transducer.DataTransducer;
import org.wolfgang.contrail.component.pipeline.transducer.DataTransducerException;

/**
 * <code>Encoder</code> is capable to transform an object to a byte array using
 * JAXB
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class Encoder implements DataTransducer<Object, Bytes> {

	/**
	 * Types used for the JAXB encoding process
	 */
	private final Class<?>[] types;

	/**
	 * Constructor
	 * 
	 * @param types
	 *            Types used for the encoding
	 */
	public Encoder(Class<?>[] types) {
		super();
		this.types = types.clone();
	}

	@Override
	public List<Bytes> transform(Object source) throws DataTransducerException {
		try {
			// TODO - Cache Object
			final JAXBContext context = JAXBContext.newInstance(types);
			final Marshaller marshaller = context.createMarshaller();
			final ByteArrayOutputStream stream = new ByteArrayOutputStream();
			try {
				marshaller.marshal(source, stream);
			} finally {
				stream.close();
			}
			return Arrays.asList(new Bytes(stream.toByteArray()));
		} catch (IOException e) {
			throw new DataTransducerException(e);
		} catch (JAXBException e) {
			throw new DataTransducerException(e);
		}
	}

	@Override
	public List<Bytes> finish() throws DataTransducerException {
		return Arrays.asList();
	}
}