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

/**
 * The <code>DataHandler</code> provides basic mechanisms required when messages
 * shall be managed. This data type is not provided as-is but was mainly used as
 * a basis for upstream and downstream data channels which are public version of
 * this data handler.
 * 
 * @author Didier Plaindoux
 * @version 1.0@
 */
public interface DataFlow<D> {
		
	/**
	 * Method called whether a data has to be managed
	 * 
	 * @param data
	 *            The data
	 */
	void handleData(D data) throws DataFlowException;

	/**
	 * Method called when the channel is closed
	 * 
	 * @throws DataFlowCloseException
	 *             thrown if any error occurs when operation fails
	 */
	void handleClose() throws DataFlowCloseException;

	/**
	 * Method called when the channel is lost and therefore closed
	 * 
	 * @throws DataFlowCloseException
	 *             thrown if any error occurs when operation fails
	 */
	void handleLost() throws DataFlowCloseException;

}
