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

package org.wolfgang.contrail.network.codec.serializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.wolfgang.contrail.component.transducer.DataTransducer;
import org.wolfgang.contrail.component.transducer.DataTransducerException;

/**
 * <code>TestSerializer</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestSerializer extends TestCase {

	public void testNominal() throws IOException, DataTransducerException {
		final String source = "Hello, World!";

		final Encoder encoder = new Encoder();
		final List<byte[]> bytes = encoder.transform(source);
		assertEquals(1, bytes.size());

		final DataTransducer<byte[], Object> decoder = PayLoadBasedSerializer.getDecoder();
		final List<Object> result = decoder.transform(bytes.get(0));
		assertEquals(1, result.size());

		assertEquals(source, result.get(0));
	}

	public void testNominalSplit() throws IOException, DataTransducerException {
		final String source = "Hello, World!";

		final DataTransducer<Object, byte[]> encoder = PayLoadBasedSerializer.getEncoder();
		final List<byte[]> bytes = encoder.transform(source);
		encoder.finish();
		
		assertEquals(1, bytes.size());
		final byte[] intermediate = bytes.get(0);

		final DataTransducer<byte[], Object> decoder = PayLoadBasedSerializer.getDecoder();
		final List<Object> fstResult = decoder.transform(Arrays.copyOfRange(intermediate, 0, intermediate.length / 2));
		assertEquals(0, fstResult.size());

		final List<Object> sndResult = decoder.transform(Arrays.copyOfRange(intermediate, intermediate.length / 2, intermediate.length));
		assertEquals(1, sndResult.size());

		assertEquals(source, sndResult.get(0));
		
		decoder.finish();
	}
}
