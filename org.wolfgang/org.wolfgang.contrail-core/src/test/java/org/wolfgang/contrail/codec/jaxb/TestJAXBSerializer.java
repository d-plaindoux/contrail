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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.wolfgang.contrail.codec.payload.Bytes;
import org.wolfgang.contrail.component.pipeline.transducer.DataTransducer;
import org.wolfgang.contrail.component.pipeline.transducer.DataTransducerException;
import org.wolfgang.contrail.component.pipeline.transducer.factory.XmlJaxbTransducerFactory;

/**
 * <code>TestSerializer</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestJAXBSerializer {

	@Test
	public void GivenAJAXBTransducerdAnExpectedClassEncodingMustSucceed() throws DataTransducerException {
		final SimpleClass source = new SimpleClass();
		source.setValue("Hello, World!");

		final XmlJaxbTransducerFactory jaxbTransducerFactory = givenAJAXBTransducer();
		final DataTransducer<Object, Bytes> encoder = jaxbTransducerFactory.getEncoder();
		final List<Bytes> bytes = encoder.transform(source);
		assertEquals(1, bytes.size());

		final DataTransducer<Bytes, Object> decoder = jaxbTransducerFactory.getDecoder();
		final List<Object> result = decoder.transform(bytes.get(0));
		assertEquals(1, result.size());

		assertEquals(source, result.get(0));
	}

	@Test
	public void GivenAJAXBTransducerdAnExpectedClassDecodingMustSucceed() throws DataTransducerException {
		final SimpleClass source = new SimpleClass();
		source.setValue("Hello, World!");

		final XmlJaxbTransducerFactory jaxbTransducerFactory = givenAJAXBTransducer();
		final DataTransducer<Object, Bytes> encoder = jaxbTransducerFactory.getEncoder();
		final DataTransducer<Bytes, Object> decoder = jaxbTransducerFactory.getDecoder();

		final List<Bytes> bytes = encoder.transform(source);
		final List<Object> result = decoder.transform(bytes.get(0));
		assertEquals(1, result.size());
		assertEquals(source, result.get(0));
	}

	@Test
	public void GivenAJAXBTransducerdAnUnexpectedClassEncodingMustFail() {
		final XmlJaxbTransducerFactory jaxbTransducerFactory = givenAJAXBTransducer();
		final DataTransducer<Object, Bytes> encoder = jaxbTransducerFactory.getEncoder();
		try {
			encoder.transform(this);
			fail();
		} catch (DataTransducerException e) {
			// OK
		}
	}

	@Test
	public void GivenACoercionTransducerdAnExpectedSequenceDecodingMustFail() {
		final byte[] bytes = { 0, 0, 0, 2, 'X', 'X', 'X', 'X' };

		final XmlJaxbTransducerFactory jaxbTransducerFactory = givenAJAXBTransducer();
		final DataTransducer<Bytes, Object> decoder = jaxbTransducerFactory.getDecoder();
		try {
			decoder.transform(new Bytes(bytes));
			fail();
		} catch (DataTransducerException e) {
			// OK
		}
	}

	private XmlJaxbTransducerFactory givenAJAXBTransducer() {
		return new XmlJaxbTransducerFactory(SimpleClass.class);
	}
}
