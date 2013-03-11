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

package org.wolfgang.contrail.link;

import java.util.concurrent.atomic.AtomicBoolean;

import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.SourceComponent;

/**
 * <code>ComponentsLinkImpl</code> is the main implementation of the component
 * source and destination linkage.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ComponentLink<U, D> implements SourceComponentLink<U, D>, DestinationComponentLink<U, D> {

	/**
	 * The upstream source used for the interconnection
	 */
	private final SourceComponent<U, D> source;
	private final DisposableLink sourceConnection;

	/**
	 * The upstream destination used for the interconnection
	 */
	private final DestinationComponent<U, D> destination;
	private final DisposableLink destinationConnection;

	private final AtomicBoolean disposed;

	{
		this.disposed = new AtomicBoolean(false);
	}

	/**
	 * Constructor
	 * 
	 * @param source
	 *            The source
	 * @param destination
	 *            The destination
	 * @throws ComponentConnectionRejectedException
	 *             thrown if the connection cannot be performed
	 */
	ComponentLink(SourceComponent<U, D> source, DestinationComponent<U, D> destination) throws ComponentConnectionRejectedException {
		super();
		this.source = source;
		this.destination = destination;

		this.sourceConnection = source.connectDestination(this);
		this.destinationConnection = destination.connectSource(this);
	}

	@Override
	public SourceComponent<U, D> getSourceComponent() {
		return this.source;
	}

	@Override
	public DestinationComponent<U, D> getDestinationComponent() {
		return this.destination;
	}

	@Override
	public void dispose() throws ComponentDisconnectionRejectedException {
		if (!this.disposed.getAndSet(true)) {
			try {
				if (this.sourceConnection != null) {
					this.sourceConnection.dispose();
				}
			} finally {
				if (this.destinationConnection != null) {
					this.destinationConnection.dispose();
				}
			}
		} else {
			throw new ComponentNotConnectedException();
		}
	}
}
