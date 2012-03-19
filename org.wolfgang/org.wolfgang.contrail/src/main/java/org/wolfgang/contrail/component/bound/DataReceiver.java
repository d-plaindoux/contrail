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

package org.wolfgang.contrail.component.bound;

import org.wolfgang.contrail.handler.DataHandlerException;

/**
 * <code>DataReceiver</code> is capable to receive data from the component
 * stream. This is mainly linked to an terminal upstream destination component.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface DataReceiver<E> {

	/**
	 * Method called whether a data shall be performed
	 * 
	 * @param data
	 *            The data to be performed
	 * @throws DataHandlerException
	 *             thrown is the data can not be handled correctly
	 */
	void receiveData(E data) throws DataHandlerException;
}
