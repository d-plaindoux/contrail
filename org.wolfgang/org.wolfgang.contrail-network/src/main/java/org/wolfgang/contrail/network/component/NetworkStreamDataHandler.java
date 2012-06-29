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

package org.wolfgang.contrail.network.component;

import java.util.Map.Entry;

import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.network.event.NetworkEvent;
import org.wolfgang.contrail.network.reference.ChainedReferences;
import org.wolfgang.contrail.network.reference.ClientReference;
import org.wolfgang.contrail.network.reference.DirectReference;
import org.wolfgang.contrail.network.reference.ReferenceEntryNotFoundException;
import org.wolfgang.contrail.network.reference.ReferenceVisitor;
import org.wolfgang.contrail.network.reference.ServerReference;

/**
 * A <code>NetworkRouterStreamDataHandler</code> is able to manage information
 * using filters owned buy each filtered upstream destination component linked
 * to the multiplexer component. The data is a network event in this case and
 * the management is done using a dedicated network router.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class NetworkStreamDataHandler implements DownStreamDataHandler<NetworkEvent> {

	/**
	 * The component in charge of managing this multiplexer
	 */
	private final NetworkComponent component;
	private final NetworkTable routerTable;
	private final DirectReference selfReference;

	/**
	 * Constructor
	 * 
	 * @param upStreamDeMultiplexer
	 */
	public NetworkStreamDataHandler(NetworkComponent component, DirectReference selfReference, NetworkTable routerTable) {
		super();
		this.component = component;
		this.selfReference = selfReference;
		this.routerTable = routerTable;
	}

	/**
	 * Return the value of routerTable
	 * 
	 * @return the routerTable
	 */
	NetworkTable getRouterTable() {
		return routerTable;
	}

	/**
	 * Return the value of selfReference
	 * 
	 * @return the selfReference
	 */
	DirectReference getSelfReference() {
		return selfReference;
	}

	@Override
	public void handleData(NetworkEvent data) throws DataHandlerException {

		/**
		 * Manage the packet removing the target if it has been reached
		 */
		if (data.getReferenceToDestination().hasNext()) {
			final DirectReference currentTarget = data.getReferenceToDestination().getNext();

			if (currentTarget.equals(this.getSelfReference())) {
				data.getReferenceToDestination().removeNext();
			}
		}

		/**
		 * Add the sender if the chosen route is private·
		 */
		if (data.getSender() != null && !this.routerTable.exist(data.getSender())) {
			data.getReferenceToSource().add(data.getSender());
		}

		/**
		 * Are we in the target ?
		 */
		if (!data.getReferenceToDestination().hasNext()) {
			component.getUpStreamDataHandler().handleData(data);
			return;
		}

		/**
		 * Pickup the next and may be intermediate target
		 */
		final DirectReference nextTarget = data.getReferenceToDestination().getNext();

		/**
		 * Use an already open source
		 */
		for (Entry<ComponentId, DirectReference> entry : component.getSourceFilters().entrySet()) {
			if (nextTarget.equals(entry.getValue())) {
				try {
					component.getSourceComponent(entry.getKey()).getDownStreamDataHandler().handleData(data);
					return;
				} catch (ComponentNotConnectedException consume) {
					// Ignore
				}
			}
		}

		/**
		 * Try to open a new source
		 */
		try {
			this.createSource(nextTarget).getDownStreamDataHandler().handleData(data.sentBy(this.getSelfReference()));
			return;
		} catch (CannotCreateComponentException e) {
			// Ignore
		}

		/**
		 * General failure ... no route available
		 */
		throw new DataHandlerException();
	}

	@Override
	public void handleClose() throws DataHandlerCloseException {
		component.closeDownStream();
		component.closeUpStream();
	}

	@Override
	public void handleLost() throws DataHandlerCloseException {
		component.closeDownStream();
		component.closeUpStream();
	}

	/**
	 * Method creating a source component linked to a given direct reference
	 * 
	 * @param reference
	 *            The direct reference
	 * @return a source component (never <code>null</code>)
	 * @throws CannotCreateComponentException
	 */
	private SourceComponent<NetworkEvent, NetworkEvent> createSource(DirectReference reference)
			throws CannotCreateComponentException {
		try {
			final NetworkTable.Entry retrieve = this.routerTable.retrieve(reference);
			return retrieve.create(reference);
		} catch (ReferenceEntryNotFoundException e) {
			throw new CannotCreateComponentException(e);
		}
	}
}
