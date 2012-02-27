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

package org.wolfgang.contrail.handler;

import org.wolfgang.contrail.component.ComponentContext;

/**
 * The <code>DataHandler</code> provides basic mechanisms required when messages
 * shall be send or receive. This data type is not provided as-is but was mainly
 * used as a basis for upstream and downstream data channels.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
interface DataHandler<D> {

	/**
	 * Method called whether a data has to be managed
	 * 
	 * @param context The data context
	 * @param data The data
	 */
	void handleData(ComponentContext context, D data) throws HandleDataException;

	/**
	 * Method called when the channel is closed
	 */
	void handleClose();

	/**
	 * Method called when the channel is lost and therefore closed
	 */
	void handleLost();

}
