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

package org.wolfgang.contrail.codec.serializer;

import java.util.List;

import junit.framework.TestCase;

import org.wolfgang.contrail.codec.payload.Bytes;
import org.wolfgang.contrail.codec.serializer.SerializationTransducerFactory;
import org.wolfgang.contrail.component.transducer.DataTransducer;
import org.wolfgang.contrail.component.transducer.DataTransducerException;

/**
 * <code>TestSerializer</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestSerializer extends TestCase {

	public void testNominal() throws DataTransducerException {
		final String source = "Hello, World!";

		final SerializationTransducerFactory serializationTransducerFactory = new SerializationTransducerFactory();

		final DataTransducer<Object, Bytes> encoder = serializationTransducerFactory.getEncoder();
		final List<Bytes> bytes = encoder.transform(source);
		assertEquals(1, bytes.size());

		final DataTransducer<Bytes, Object> decoder = serializationTransducerFactory.getDecoder();
		final List<Object> result = decoder.transform(bytes.get(0));
		assertEquals(1, result.size());

		assertEquals(source, result.get(0));
	}

	public void testFailure01() {
		final SerializationTransducerFactory serializationTransducerFactory = new SerializationTransducerFactory();

		final DataTransducer<Object, Bytes> encoder = serializationTransducerFactory.getEncoder();
		try {
			encoder.transform(this);
			fail();
		} catch (DataTransducerException e) {
			// OK
		}
	}

	public void testFailure02() {
		final byte[] bytes = { 0, 0, 0, 2, 'X', 'X', 'X', 'X' };

		final SerializationTransducerFactory serializationTransducerFactory = new SerializationTransducerFactory();

		final DataTransducer<Bytes, Object> decoder = serializationTransducerFactory.getDecoder();
		try {
			decoder.transform(new Bytes(bytes));
			fail();
		} catch (DataTransducerException e) {
			// OK
		}
	}
}
