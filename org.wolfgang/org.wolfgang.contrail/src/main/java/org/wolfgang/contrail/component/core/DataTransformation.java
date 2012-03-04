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

package org.wolfgang.contrail.component.core;

import java.util.List;


/**
 * <code>DataTransducer</code> is able to transform any data from a given type
 * to another one.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface DataTransformation<S, D> {

	/**
	 * Method called when a data must be transformed from a type S to a set of type D
	 * 
	 * @param source
	 *            The data to be transformed
	 * @return the transformation results
	 * @throws DataTransformationException
	 *             thrown if the transformation fails
	 */
	List<D> transform(S source) throws DataTransformationException;
}
