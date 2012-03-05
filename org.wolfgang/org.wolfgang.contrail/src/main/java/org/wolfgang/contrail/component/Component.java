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

package org.wolfgang.contrail.component;

import org.wolfgang.contrail.handler.DataHandlerCloseException;

/**
 * <code>Component</code> is the main specification for component definition. It
 * provides close behaviors for upstream and downstream. Then when a component
 * has its upstream closed it's just a component is able to manager outgoing
 * data. When the downstream is closed then the component is able to manage
 * incoming data e.g. listener. In addition each component is provided with a
 * given identifier which is locally unique i.e. in the current context.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface Component {

	/**
	 * Method providing the component identifier
	 */
	ComponentId getComponentId();

	/**
	 * Method called whether the upstream must be closed
	 * 
	 * @throws DataHandlerCloseException
	 *             thrown if an error occurs during the stream termination
	 *             process
	 */
	void closeUpStream() throws DataHandlerCloseException;

	/**
	 * Method called whether the downstream must be closed
	 * 
	 * @throws DataHandlerCloseException
	 *             thrown if an error occurs during the stream termination
	 *             process
	 */
	void closeDownStream() throws DataHandlerCloseException;

}
