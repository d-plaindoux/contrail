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

package org.wolfgang.contrail.flow;

import org.wolfgang.contrail.flow.exception.CannotCreateDataFlowException;


/**
 * <code>DataFlowFactory</code> is capable to build data sender.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface DataFlowFactory<U, D> {

	/**
	 * Method called whether a data receiver shall be built for a given
	 * component
	 * 
	 * @param component
	 *            The component used to build the data sender
	 * @return a data sender (Never <code>null</code>)
	 * @throws CannotCreateDataFlowException
	 *             if the data sender cannot be correctly created
	 */
	DataFlow<D> create(DataFlow<U> component) throws CannotCreateDataFlowException;

}
