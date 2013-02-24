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

package org.wolfgang.contrail.codec.stringify;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.wolfgang.common.utils.Coercion;
import org.wolfgang.common.utils.Marshall;
import org.wolfgang.contrail.codec.payload.Bytes;
import org.wolfgang.contrail.component.pipeline.transducer.DataTransducer;
import org.wolfgang.contrail.component.pipeline.transducer.DataTransducerException;
import org.wolfgang.contrail.data.ObjectRecord;

/**
 * <code>Encoder</code> is capable to transform objects to payload based byte
 * array.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class Encoder implements DataTransducer<Bytes, String> {

	/**
	 * Constructor
	 */
	public Encoder() {
		super();
	}

	@Override
	public List<String> transform(Bytes source) throws DataTransducerException {
		try {
			return Arrays.asList(new String(source.getContent()));
		} catch (IllegalArgumentException e) {
			throw new DataTransducerException(e);
		}
	}

	@Override
	public List<String> finish() throws DataTransducerException {
		return Arrays.asList();
	}
}