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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.wolfgang.common.utils.Marshall;
import org.wolfgang.common.utils.Option;
import org.wolfgang.contrail.component.core.DataTransformation;
import org.wolfgang.contrail.component.core.DataTransformationException;

/**
 * <code>PayLoadBasedSerializer</code> is in charge of transforming upstream
 * bytes to java object and vice-versa based on pay load.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface PayLoadBasedSerializer {

	public class Decoder implements DataTransformation<byte[], Object> {
		private static final int INT_LEN = 4;
		private static final byte[] EMPTY_BUFFER = new byte[0];

		private byte[] buffer;

		/**
		 * Constructor
		 */
		public Decoder() {
			super();
			this.buffer = EMPTY_BUFFER;
		}

		private Option<Object> getNext() throws IOException, ClassNotFoundException {
			if (buffer.length < INT_LEN) {
				return Option.none();
			} else {
				// Read the payload first
				final int payLoad = Marshall.bytesToInt(buffer);
				if (buffer.length - INT_LEN < payLoad) {
					return Option.none();
				} else {
					final byte[] array = Arrays.copyOfRange(buffer, INT_LEN, payLoad + INT_LEN);
					buffer = Arrays.copyOfRange(buffer, payLoad + INT_LEN, buffer.length);
					return Option.some(Marshall.bytesToObject(array));
				}
			}
		}

		@Override
		public List<Object> transform(byte[] source) throws DataTransformationException {
			try {
				// Catenate buffers ...
				final byte[] newBuffer = new byte[buffer.length + source.length];
				System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
				System.arraycopy(source, 0, newBuffer, buffer.length, source.length);
				buffer = newBuffer;

				// Try to decode objects
				final List<Object> objects = new ArrayList<Object>();
				Option<Object> option;
				while ((option = getNext()).getKind() == Option.Kind.Some) {
					objects.add(option.getValue());
				}

				return objects;
			} catch (Exception e) {
				throw new DataTransformationException(e);
			}
		}
	}

	public class Encoder implements DataTransformation<Serializable, byte[]> {
		/**
		 * Constructor
		 */
		public Encoder() {
			super();
		}

		@Override
		public List<byte[]> transform(Serializable source) throws DataTransformationException {
			try {
				final ByteArrayOutputStream stream = new ByteArrayOutputStream();
				try {
					final byte[] bytes = Marshall.objectToBytes(source);
					stream.write(Marshall.intToBytes(bytes.length));
					stream.write(bytes);
				} finally {
					stream.close();
				}
				return Arrays.asList(stream.toByteArray());
			} catch (Exception e) {
				throw new DataTransformationException(e);
			}
		}
	}
}