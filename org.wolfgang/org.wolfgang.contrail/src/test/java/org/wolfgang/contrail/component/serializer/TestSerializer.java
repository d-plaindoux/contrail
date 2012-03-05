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

package org.wolfgang.contrail.component.serializer;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.wolfgang.contrail.component.core.DataTransformationException;

/**
 * <code>TestSerializer</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestSerializer extends TestCase {

	public void testDecoder() throws IOException, DataTransformationException {
		final String source = "Hello, World!";

		final PayLoadBasedSerializer.Encoder encoder = new PayLoadBasedSerializer.Encoder();
		final List<byte[]> bytes = encoder.transform(source);
		assertEquals(1, bytes.size());

		final PayLoadBasedSerializer.Decoder decoder = new PayLoadBasedSerializer.Decoder();
		final List<Object> transform = decoder.transform(bytes.get(0));
		assertEquals(1, transform.size());

		assertEquals(source, transform.get(0));
	}

}
