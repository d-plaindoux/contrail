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

import java.util.List;

import junit.framework.TestCase;

import org.wolfgang.contrail.component.pipeline.transducer.DataTransducer;
import org.wolfgang.contrail.component.pipeline.transducer.DataTransducerException;
import org.wolfgang.contrail.component.pipeline.transducer.coercion.CoercionTransducerFactory;

/**
 * <code>TestSerializer</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestCoercionTransducers extends TestCase {

	public static class SimpleClass {
	}

	public static class WrongSimpleClass {
	}

	public void testNominal01() throws DataTransducerException {
		final SimpleClass source = new SimpleClass();

		final CoercionTransducerFactory<SimpleClass> payLoadTransducerFactory = new CoercionTransducerFactory<SimpleClass>(SimpleClass.class);
		final DataTransducer<SimpleClass, Object> encoder = payLoadTransducerFactory.getEncoder();
		final List<Object> objects = encoder.transform(source);
		assertEquals(1, objects.size());
		assertEquals(source, objects.get(0));

		final DataTransducer<Object, SimpleClass> decoder = payLoadTransducerFactory.getDecoder();
		final List<SimpleClass> results = decoder.transform(objects.get(0));
		assertEquals(1, results.size());
		assertEquals(source, results.get(0));
	}

	public void testFailure01() {
		final CoercionTransducerFactory<SimpleClass> payLoadTransducerFactory = new CoercionTransducerFactory<SimpleClass>(SimpleClass.class);
		final DataTransducer<Object, SimpleClass> decoder = payLoadTransducerFactory.getDecoder();
		try {
			decoder.transform(new WrongSimpleClass());
			fail();
		} catch (DataTransducerException e) {
			// OK
		}
	}
}
