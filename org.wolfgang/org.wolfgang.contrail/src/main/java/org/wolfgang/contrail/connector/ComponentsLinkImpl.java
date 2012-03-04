/*
 * WolfGang Copyright (C)2012 D. Plaindoux.
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
import org.wolfgang.contrail.component.ComponentNotYetConnectedException;
import org.wolfgang.contrail.component.UpStreamDestinationComponent;
import org.wolfgang.contrail.component.UpStreamSourceComponent;

/**
 * <code>ComponentsLinkImpl</code> is the main implementation of the component
 * source and destination linkage.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class ComponentsLinkImpl<E> implements ComponentsLink<E> {

	/**
	 * The upstream source used for the interconnection
	 */
	private final UpStreamSourceComponent<E> source;

	/**
	 * The upstream destination used for the interconnection
	 */
	private final UpStreamDestinationComponent<E> destination;

	/**
	 * Constructor
	 * 
	 * @param source
	 *            The source
	 * @param destination
	 *            The destination
	 * @throws ComponentAlreadyConnectedException
	 *             thrown if components are already connected
	 */
	public ComponentsLinkImpl(UpStreamSourceComponent<E> source, UpStreamDestinationComponent<E> destination)
			throws ComponentAlreadyConnectedException {
		super();
		this.source = source;
		this.destination = destination;

		source.connect(destination);

		try { 
			destination.connect(source);
		} catch (ComponentAlreadyConnectedException e) {
			try {
				source.disconnect(destination);
			} catch (Exception consume) {
				// Ignore
			}

			throw e;
		}
	}

	@Override
	public UpStreamSourceComponent<E> getUpStreamSourceComponent() {
		return this.source;
	}

	@Override
	public UpStreamDestinationComponent<E> getUpStreamDestinationComponent() {
		return this.destination;
	}

	@Override
	public void dispose() throws ComponentNotYetConnectedException {
		try {
			this.source.disconnect(this.destination);
		} finally {
			this.destination.disconnect(this.source);
		}
	}
}
