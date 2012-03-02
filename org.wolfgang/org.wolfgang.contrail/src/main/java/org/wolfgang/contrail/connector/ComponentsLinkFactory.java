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

package org.wolfgang.contrail.connector;

import org.wolfgang.contrail.component.ComponentAlreadyConnectedException;
import org.wolfgang.contrail.component.UpStreamDestinationComponent;
import org.wolfgang.contrail.component.UpStreamSourceComponent;

/**
 * The <code>ComponentsLinkFactory</code> is used when components link must be
 * established.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class ComponentsLinkFactory {
	
	/**
	 * Constructor
	 */
	private ComponentsLinkFactory(){
		// Nothing
	}

	/**
	 * Function able to create a link between to given component.
	 * 
	 * @param source
	 *            The link source
	 * @param destination
	 *            The link destination
	 * @return a components link (never <code>null</code>)
	 * @throws ComponentAlreadyConnectedException
	 *             Thrown if one component is linked
	 */
	public static final <E> ComponentsLink<E> connect(UpStreamSourceComponent<E> source, UpStreamDestinationComponent<E> destination) throws ComponentAlreadyConnectedException {
		return new ComponentsLinkImpl<E>(source, destination);
	}

}
