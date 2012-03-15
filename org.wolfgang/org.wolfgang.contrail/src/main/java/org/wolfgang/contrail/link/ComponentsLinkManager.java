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

import java.util.ArrayList;
import java.util.List;

import org.wolfgang.contrail.component.ComponentConnectedException;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.UpStreamDestinationComponent;
import org.wolfgang.contrail.component.UpStreamSourceComponent;

/**
 * The <code>ComponentsLinkFactory</code> is used when components link must be
 * established.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ComponentsLinkManager {

	/**
	 * Created links
	 */
	private final List<ComponentsLink<?>> links;

	{
		links = new ArrayList<ComponentsLink<?>>();
	}

	/**
	 * Constructor
	 */
	public ComponentsLinkManager() {
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
	 * @throws ComponentConnectedException
	 *             Thrown if one component is linked
	 * @throws ComponentConnectionRejectedException
	 *             Thrown if the connection cannot be performed
	 */
	public final <E> ComponentsLink<E> connect(UpStreamSourceComponent<E> source, UpStreamDestinationComponent<E> destination)
			throws ComponentConnectionRejectedException {
		final ComponentsLinkImpl<E> link = new ComponentsLinkImpl<E>(source, destination) {

			@Override
			public void dispose() throws ComponentDisconnectionRejectedException {
				try {
					super.dispose();
				} finally {
					links.remove(this);
				}
			}

		};

		this.links.add(link);

		return link;
	}

	/**
	 * Method called when all established links must be retrieved
	 * 
	 * @return an array of established links
	 */
	public ComponentsLink<?>[] getEstablishedLinks() {
		return links.toArray(new ComponentsLink[links.size()]);
	}

}
