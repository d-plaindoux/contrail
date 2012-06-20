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
import java.util.Iterator;
import java.util.List;

import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.SourceComponent;

/**
 * The <code>ComponentsLinkFactory</code> is used when components link must be
 * established.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ComponentsLinkManagerImpl implements ComponentsLinkManager {

	/**
	 * Created links
	 */
	private final List<ComponentsLink<?, ?>> links;

	{
		links = new ArrayList<ComponentsLink<?, ?>>();
	}

	/**
	 * Constructor
	 */
	public ComponentsLinkManagerImpl() {
		// Nothing
	}

	@Override
	public final <U, D> ComponentsLink<U, D> connect(SourceComponent<U, D> source, DestinationComponent<U, D> destination)
			throws ComponentConnectionRejectedException {
		final ComponentsLinkImpl<U, D> link = new ComponentsLinkImpl<U, D>(source, destination) {
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

	@Override
	public final <U, D> boolean disconnect(SourceComponent<U, D> source, DestinationComponent<U, D> destination)
			throws ComponentDisconnectionRejectedException {
		for (ComponentsLink<?, ?> link : links) {
			if (link.getSourceComponent().equals(source) && link.getDestinationComponent().equals(destination)) {
				link.dispose();
				// Secured remove since we exit this method just after
				links.remove(link);
				return true;
			}
		}

		return false;
	}

	@Override
	public ComponentsLink<?, ?>[] getEstablishedLinks() {
		return links.toArray(new ComponentsLink[links.size()]);
	}

	@Override
	public SourceComponent<?, ?>[] getSources(ComponentId componentId) {
		final List<SourceComponent<?, ?>> sources = new ArrayList<SourceComponent<?, ?>>();
		for (ComponentsLink<?, ?> link : links) {
			if (link.getDestinationComponent().getComponentId().equals(componentId)) {
				sources.add(link.getSourceComponent());
			}
		}
		return sources.toArray(new SourceComponent[links.size()]);
	}

	@Override
	public DestinationComponent<?, ?>[] getDestinations(ComponentId componentId) {
		final List<DestinationComponent<?, ?>> destinations = new ArrayList<DestinationComponent<?, ?>>();
		for (ComponentsLink<?, ?> link : links) {
			if (link.getSourceComponent().getComponentId().equals(componentId)) {
				destinations.add(link.getDestinationComponent());
			}
		}
		return destinations.toArray(new DestinationComponent[links.size()]);
	}

}
