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

package org.wolfgang.contrail.link;

import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.SourceComponent;

/**
 * <code>ComponentsLinkManager</code>
 *
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface ComponentsLinkManager {

	/**
	 * Function able to create a link between to given component.
	 * 
	 * @param source
	 *            The link source
	 * @param destination
	 *            The link destination
	 * @return a components link (never <code>null</code>)
	 * @throws ComponentConnectionRejectedException
	 *             Thrown if the connection cannot be performed
	 */
	public abstract <U, D> ComponentsLink<U, D> connect(SourceComponent<U, D> source, DestinationComponent<U, D> destination)
			throws ComponentConnectionRejectedException;

	/**
	 * Method called when all established links must be retrieved
	 * 
	 * @return an array of established links
	 */
	public abstract ComponentsLink<?, ?>[] getEstablishedLinks();

}