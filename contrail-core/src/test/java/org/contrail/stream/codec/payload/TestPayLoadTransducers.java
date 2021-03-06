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

package org.contrail.stream.codec.payload;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.contrail.stream.component.pipeline.transducer.DataTransducer;
import org.contrail.stream.component.pipeline.transducer.DataTransducerException;
import org.contrail.stream.component.pipeline.transducer.factory.PayLoadTransducerFactory;
import org.junit.Test;

/**
 * <code>TestSerializer</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestPayLoadTransducers {
	
	@Test
	public void testNominal() throws DataTransducerException {
		final String source = "Hello, World!";

		final PayLoadTransducerFactory payLoadTransducerFactory = new PayLoadTransducerFactory();
		
		final DataTransducer<Bytes, byte[]> encoder = payLoadTransducerFactory.getEncoder();
		final List<byte[]> bytes = encoder.transform(new Bytes(source.getBytes()));
		assertEquals(1, bytes.size());

		final DataTransducer<byte[], Bytes> decoder = payLoadTransducerFactory.getDecoder();
		final List<Bytes> result = decoder.transform(bytes.get(0));
		assertEquals(1, result.size());

		assertEquals(source, new String(result.get(0).getContent()));
	}
	
	@Test
	public void testNominalSplit() throws DataTransducerException {
		final String source = "Hello, World!";

		final PayLoadTransducerFactory payLoadTransducerFactory = new PayLoadTransducerFactory();

		final DataTransducer<Bytes, byte[]> encoder = payLoadTransducerFactory.getEncoder();
		final List<byte[]> bytes = encoder.transform(new Bytes(source.getBytes()));
		encoder.finish();

		assertEquals(1, bytes.size());
		final byte[] intermediate = bytes.get(0);

		final DataTransducer<byte[], Bytes> decoder = payLoadTransducerFactory.getDecoder();
		final List<Bytes> fstResult = decoder.transform(Arrays.copyOfRange(intermediate, 0, intermediate.length / 2));
		assertEquals(0, fstResult.size());

		final List<Bytes> sndResult = decoder.transform(Arrays.copyOfRange(intermediate, intermediate.length / 2,
				intermediate.length));
		assertEquals(1, sndResult.size());

		assertEquals(source, new String(sndResult.get(0).getContent()));

		decoder.finish();
	}
}
