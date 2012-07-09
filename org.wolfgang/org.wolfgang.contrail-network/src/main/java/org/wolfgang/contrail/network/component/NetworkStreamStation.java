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

import org.wolfgang.common.message.Message;
import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.DestinationComponent;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolfgang.contrail.network.event.NetworkEvent;
import org.wolfgang.contrail.network.reference.DirectReference;
import org.wolfgang.contrail.network.reference.ReferenceEntryNotFoundException;

/**
 * A <code>NetworkRouterStreamDataHandler</code> is able to manage information
 * using filters owned buy each filtered upstream destination component linked
 * to the multiplexer component. The data is a network event in this case and
 * the management is done using a dedicated network router.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class NetworkStreamStation implements DownStreamDataHandler<NetworkEvent>, UpStreamDataHandler<NetworkEvent> {

	/**
	 * The component in charge of managing this multiplexer
	 */
	private final NetworkComponent component;
	private final NetworkTable routerTable;
	private final DirectReference selfReference;

	/**
	 * Constructor
	 * 
	 * @param component
	 * @param selfReference
	 * @param routerTable
	 */
	public NetworkStreamStation(NetworkComponent component, DirectReference selfReference, NetworkTable routerTable) {
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
		 * Add the sender if the chosen route is private·
		 */
		final DirectReference sender = data.getSender();
		if (sender != null && !this.routerTable.exist(sender)) {
			data.getReferenceToSource().addFirst(sender);
		}

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
		 * Are we in the targeted network component ?
		 */
		if (!data.getReferenceToDestination().hasNext()) {
			try {
				final DestinationComponent<NetworkEvent, NetworkEvent> destination = component.getDestination();
				destination.getUpStreamDataHandler().handleData(data);
				return;
			} catch (ComponentNotConnectedException e) {
				throw new DataHandlerException();
			}
		} else {
			/**
			 * Pickup the next and may be intermediate target
			 */
			final DirectReference nextTarget = this.getRouterTable().getDirectTarget(data.getReferenceToDestination().getNext());

			data.sentBy(this.selfReference);

			/**
			 * Use an already open source
			 */
			for (Entry<ComponentId, DirectReference> entry : component.getSourceFilters().entrySet()) {
				if (nextTarget.equals(entry.getValue())) {
					try {
						final SourceComponent<NetworkEvent, NetworkEvent> source = component.getSource(entry.getKey());
						source.getDownStreamDataHandler().handleData(data);
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
				final SourceComponent<NetworkEvent, NetworkEvent> source = this.createSource(nextTarget);
				source.getDownStreamDataHandler().handleData(data.sentBy(this.getSelfReference()));
				return;
			} catch (CannotCreateComponentException e) {
				final Message message = MessagesProvider.message("org/wolfgang/contrail/network/message", "route.entry.not.defined");
				throw new DataHandlerException(message.format(nextTarget, selfReference), e);
			}
		}
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
	private SourceComponent<NetworkEvent, NetworkEvent> createSource(DirectReference reference) throws CannotCreateComponentException {
		try {
			final NetworkTable.Entry retrieve = this.routerTable.retrieve(reference);
			return retrieve.create();
		} catch (ReferenceEntryNotFoundException e) {
			throw new CannotCreateComponentException(e);
		}
	}
}
