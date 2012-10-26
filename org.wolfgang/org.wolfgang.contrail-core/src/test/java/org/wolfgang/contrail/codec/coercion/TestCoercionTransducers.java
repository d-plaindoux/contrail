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

package org.wolfgang.contrail.codec.coercion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.wolfgang.contrail.component.pipeline.transducer.DataTransducer;
import org.wolfgang.contrail.component.pipeline.transducer.DataTransducerException;
import org.wolfgang.contrail.component.pipeline.transducer.coercion.CoercionTransducerFactory;

/**
 * <code>TestSerializer</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestCoercionTransducers {

	@Test
	public void ShouldSucceedWhenEncodingAnExpectedData() throws DataTransducerException {

		final SimpleClass source = givenASimpleData();
		final CoercionTransducerFactory<SimpleClass> payLoadTransducerFactory = givenACoercionTransducer();
		final List<Object> objects = whenEncodingAData(source, payLoadTransducerFactory);
		thenEncodedDataMustBeASingleton(source, objects);
	}

	@Test
	public void ShouldSucceedWhenDecodingAnExpectedData() throws DataTransducerException {
		final SimpleClass source = givenASimpleData();
		final CoercionTransducerFactory<SimpleClass> payLoadTransducerFactory = givenACoercionTransducer();
		final List<SimpleClass> results = whenDecodingAData(source, payLoadTransducerFactory);
		thenDataMustBeTheSource(source, results);
	}

	@Test(expected = DataTransducerException.class)
	public void ShouldFailWhenDecodingAnUnexpectedData() throws DataTransducerException {
		final CoercionTransducerFactory<SimpleClass> payLoadTransducerFactory = givenACoercionTransducer();
		whenDecodingAnUnexpectedData(payLoadTransducerFactory);
		fail();
	}

	private CoercionTransducerFactory<SimpleClass> givenACoercionTransducer() {
		return new CoercionTransducerFactory<SimpleClass>(SimpleClass.class);
	}

	private SimpleClass givenASimpleData() {
		return new SimpleClass();
	}

	private List<Object> whenEncodingAData(final SimpleClass source, final CoercionTransducerFactory<SimpleClass> payLoadTransducerFactory) throws DataTransducerException {
		final DataTransducer<SimpleClass, Object> encoder = payLoadTransducerFactory.getEncoder();
		final List<Object> objects = encoder.transform(source);
		return objects;
	}

	private void thenEncodedDataMustBeASingleton(final SimpleClass source, final List<Object> objects) {
		assertEquals(1, objects.size());
		assertEquals(source, objects.get(0));
	}

	private void thenDataMustBeTheSource(final SimpleClass source, final List<SimpleClass> results) {
		assertEquals(1, results.size());
		assertEquals(source, results.get(0));
	}

	private void whenDecodingAnUnexpectedData(final CoercionTransducerFactory<SimpleClass> payLoadTransducerFactory) throws DataTransducerException {
		final DataTransducer<Object, SimpleClass> decoder = payLoadTransducerFactory.getDecoder();
		decoder.transform(new WrongSimpleClass());
	}
	
	private List<SimpleClass> whenDecodingAData(final SimpleClass source, final CoercionTransducerFactory<SimpleClass> payLoadTransducerFactory) throws DataTransducerException {
		final DataTransducer<SimpleClass, Object> encoder = payLoadTransducerFactory.getEncoder();
		final DataTransducer<Object, SimpleClass> decoder = payLoadTransducerFactory.getDecoder();
		final List<Object> objects = encoder.transform(source);
		final List<SimpleClass> results = decoder.transform(objects.get(0));
		return results;
	}
}
