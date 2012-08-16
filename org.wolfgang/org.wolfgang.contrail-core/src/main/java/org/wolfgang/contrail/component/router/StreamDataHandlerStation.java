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

package org.wolfgang.contrail.component.router;

import java.util.Map.Entry;

import org.wolfgang.common.message.Message;
import org.wolfgang.common.message.MessagesProvider;
import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.ComponentId;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.event.Event;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolfgang.contrail.reference.DirectReference;
import org.wolfgang.contrail.reference.ReferenceEntryNotFoundException;

/**
 * A <code>StreamDataHandlerStation</code> is able to manage information using
 * filters owned buy each filtered upstream destination component linked to the
 * multiplexer component. The data is a network event in this case and the
 * management is done using a dedicated network router.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class StreamDataHandlerStation implements DownStreamDataHandler<Event>, UpStreamDataHandler<Event> {

	/**
	 * The component in charge of managing this multiplexer
	 */
	private final RouterSourceComponent component;
	private final RouterSourceTable routerTable;
	private final DirectReference selfReference;

	/**
	 * Constructor
	 * 
	 * @param component
	 * @param selfReference
	 * @param routerTable
	 */
	public StreamDataHandlerStation(RouterSourceComponent component, DirectReference selfReference, RouterSourceTable routerTable) {
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
	RouterSourceTable getRouterTable() {
		return routerTable;
	}

	@Override
	public void handleData(Event data) throws DataHandlerException {
		/**
		 * Add the sender if the chosen route is private·
		 */
		final DirectReference sender = data.getSender();
		if (sender != null && !this.routerTable.exist(sender)) {
			data.handledBy(sender);
		}

		/**
		 * Manage the packet removing the target if it has been reached
		 */
		if (data.getReferenceToDestination().hasNext()) {
			final DirectReference currentTarget = data.getReferenceToDestination().getNext();
			if (currentTarget.equals(this.selfReference)) {
				data.getReferenceToDestination().removeNext();
			}
		}

		/**
		 * Take the next entry if possible ...
		 */
		final DirectReference nextTarget;
		if (data.getReferenceToDestination().hasNext()) {
			nextTarget = this.getRouterTable().getDirectTarget(data.getReferenceToDestination().getNext());
		} else {
			// TODO
			nextTarget = this.selfReference;
		}

		/**
		 * Pickup the next and may be intermediate target
		 */

		data.sentBy(this.selfReference);

		/**
		 * Use an already open source
		 */
		for (Entry<ComponentId, DirectReference> entry : component.getFilters().entrySet()) {
			if (nextTarget.equals(entry.getValue())) {
				try {
					component.getDataHander(entry.getKey()).handleData(data);
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
			final SourceComponent<Event, Event> source = this.createSource(nextTarget);
			source.getDownStreamDataHandler().handleData(data.sentBy(this.selfReference));
			return;
		} catch (CannotCreateComponentException e) {
			final Message message = MessagesProvider.message("org/wolfgang/contrail/network/message", "route.entry.not.defined");
			throw new DataHandlerException(message.format(nextTarget, selfReference), e);
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
	private SourceComponent<Event, Event> createSource(DirectReference reference) throws CannotCreateComponentException {
		try {
			final RouterSourceTable.Entry retrieve = this.routerTable.retrieve(reference);
			return retrieve.create();
		} catch (ReferenceEntryNotFoundException e) {
			throw new CannotCreateComponentException(e);
		}
	}
}
