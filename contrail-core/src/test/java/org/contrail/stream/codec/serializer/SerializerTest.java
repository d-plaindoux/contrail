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

package org.contrail.stream.codec.serializer;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Array;
import java.util.List;

import org.junit.Test;
import org.contrail.stream.codec.payload.Bytes;
import org.contrail.stream.component.pipeline.transducer.DataTransducer;
import org.contrail.stream.component.pipeline.transducer.DataTransducerException;
import org.contrail.stream.component.pipeline.transducer.factory.SerializationTransducerFactory;
import org.contrail.stream.data.ObjectRecord;

/**
 * <code>TestSerializer</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class SerializerTest {

	@Test
	public void shouldRetrieveAStringWithEncodeAndDecode() throws DataTransducerException {
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

	@Test
	public void shouldRetrieveAnIntegerWithEncodeAndDecode() throws DataTransducerException {
		final int source = 42;

		final SerializationTransducerFactory serializationTransducerFactory = new SerializationTransducerFactory();

		final DataTransducer<Object, Bytes> encoder = serializationTransducerFactory.getEncoder();
		final List<Bytes> bytes = encoder.transform(source);
		assertEquals(1, bytes.size());

		final DataTransducer<Bytes, Object> decoder = serializationTransducerFactory.getDecoder();
		final List<Object> result = decoder.transform(bytes.get(0));
		assertEquals(1, result.size());

		assertEquals(source, result.get(0));
	}

	@Test
	public void shouldRetrieveAFloatWithEncodeAndDecode() throws DataTransducerException {
		final float source = 42.3f;

		final SerializationTransducerFactory serializationTransducerFactory = new SerializationTransducerFactory();

		final DataTransducer<Object, Bytes> encoder = serializationTransducerFactory.getEncoder();
		final List<Bytes> bytes = encoder.transform(source);
		assertEquals(1, bytes.size());

		final DataTransducer<Bytes, Object> decoder = serializationTransducerFactory.getDecoder();
		final List<Object> result = decoder.transform(bytes.get(0));
		assertEquals(1, result.size());

		assertEquals(source, result.get(0));
	}

	@Test
	public void shouldRetrieveTrueWithEncodeAndDecode() throws DataTransducerException {
		final boolean source = true;

		final SerializationTransducerFactory serializationTransducerFactory = new SerializationTransducerFactory();

		final DataTransducer<Object, Bytes> encoder = serializationTransducerFactory.getEncoder();
		final List<Bytes> bytes = encoder.transform(source);
		assertEquals(1, bytes.size());

		final DataTransducer<Bytes, Object> decoder = serializationTransducerFactory.getDecoder();
		final List<Object> result = decoder.transform(bytes.get(0));
		assertEquals(1, result.size());

		assertEquals(source, result.get(0));
	}

	@Test
	public void shouldRetrieveFalseWithEncodeAndDecode() throws DataTransducerException {
		final boolean source = false;

		final SerializationTransducerFactory serializationTransducerFactory = new SerializationTransducerFactory();

		final DataTransducer<Object, Bytes> encoder = serializationTransducerFactory.getEncoder();
		final List<Bytes> bytes = encoder.transform(source);
		assertEquals(1, bytes.size());

		final DataTransducer<Bytes, Object> decoder = serializationTransducerFactory.getDecoder();
		final List<Object> result = decoder.transform(bytes.get(0));
		assertEquals(1, result.size());

		assertEquals(source, result.get(0));
	}

	@Test
	public void shouldRetrieveNullWithEncodeAndDecode() throws DataTransducerException {
		final Object source = null;

		final SerializationTransducerFactory serializationTransducerFactory = new SerializationTransducerFactory();

		final DataTransducer<Object, Bytes> encoder = serializationTransducerFactory.getEncoder();
		final List<Bytes> bytes = encoder.transform(source);
		assertEquals(1, bytes.size());

		final DataTransducer<Bytes, Object> decoder = serializationTransducerFactory.getDecoder();
		final List<Object> result = decoder.transform(bytes.get(0));
		assertEquals(1, result.size());

		assertEquals(source, result.get(0));
	}

	@Test
	public void shouldRetrieveAnArrayWithEncodeAndDecode() throws DataTransducerException {
		final Object[] source = { "Hello, World!", 42 };

		final SerializationTransducerFactory serializationTransducerFactory = new SerializationTransducerFactory();

		final DataTransducer<Object, Bytes> encoder = serializationTransducerFactory.getEncoder();
		final List<Bytes> bytes = encoder.transform(source);
		assertEquals(1, bytes.size());

		final DataTransducer<Bytes, Object> decoder = serializationTransducerFactory.getDecoder();
		final List<Object> result = decoder.transform(bytes.get(0));
		assertEquals(1, result.size());
		assertEquals(true, result.get(0).getClass().isArray());
		assertEquals(source.length, Array.getLength(result.get(0)));

		for (int i = 0; i < source.length; i++) {
			assertEquals(source[i], Array.get(result.get(0), i));
		}
	}

	@Test
	public void shouldRetrieveAnObjectRecordWithEncodeAndDecode() throws DataTransducerException {
		final ObjectRecord source = new ObjectRecord().set("name", "A").set("age", 42);

		final SerializationTransducerFactory serializationTransducerFactory = new SerializationTransducerFactory();

		final DataTransducer<Object, Bytes> encoder = serializationTransducerFactory.getEncoder();
		final List<Bytes> bytes = encoder.transform(source);
		assertEquals(1, bytes.size());

		final DataTransducer<Bytes, Object> decoder = serializationTransducerFactory.getDecoder();
		final List<Object> result = decoder.transform(bytes.get(0));
		assertEquals(1, result.size());

		assertEquals(source, result.get(0));
	}

	@Test(expected = DataTransducerException.class)
	public void souldHaveAndExceptinoWhenEncodingNotAllowedData() throws DataTransducerException {
		final SerializationTransducerFactory serializationTransducerFactory = new SerializationTransducerFactory();
		final DataTransducer<Object, Bytes> encoder = serializationTransducerFactory.getEncoder();
		encoder.transform(this);
	}

	@Test(expected = DataTransducerException.class)
	public void souldHaveAndExceptinoWhenDecodingNotAllowedData() throws DataTransducerException {
		final byte[] bytes = { 0, 0, 0, 2, 'X', 'X', 'X', 'X' };

		final SerializationTransducerFactory serializationTransducerFactory = new SerializationTransducerFactory();

		final DataTransducer<Bytes, Object> decoder = serializationTransducerFactory.getDecoder();
		decoder.transform(new Bytes(bytes));
	}
}
