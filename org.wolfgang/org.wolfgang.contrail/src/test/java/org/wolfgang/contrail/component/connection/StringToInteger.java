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

package org.wolfgang.contrail.component.connection;

import java.util.Arrays;
import java.util.List;

import org.wolfgang.contrail.component.core.DataTransformation;
import org.wolfgang.contrail.component.core.DataTransformationException;

/**
 * <code>IntegerToString</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class StringToInteger implements DataTransformation<String, Integer> {

	@Override
	public List<Integer> transform(String s) throws DataTransformationException {
		try {
			return Arrays.asList(Integer.parseInt(s));
		} catch (NumberFormatException e) {
			throw new DataTransformationException(e);
		}
	}

	@Override
	public List<Integer> finish() throws DataTransformationException {
		return Arrays.asList();
	}
}
