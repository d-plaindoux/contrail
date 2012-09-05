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
import org.wolfgang.contrail.flow.DataFlowCloseException;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.reference.DirectReference;
import org.wolfgang.contrail.reference.ReferenceEntryNotFoundException;

/**
 * A <code>AbstractDataHandlerStation</code> is able to manage information using
 * filters owned buy each filtered upstream destination component linked to the
 * multiplexer component. The data is a network event in this case and the
 * management is done using a dedicated network router.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
abstract class AbstractDataHandlerStation implements DownStreamDataFlow<Event>, UpStreamDataFlow<Event> {

	/**
	 * Private message
	 */
	private static final Message NOT_FOUND;

	static {
		NOT_FOUND = MessagesProvider.message("org/wolfgang/contrail/message", "entry.not.defined");
	}

	/**
	 * The component in charge of managing this multiplexer
	 */
	private final RouterComponent component;
	private final RouterSourceTable routerTable;

	/**
	 * Constructor
	 * 
	 * @param component
	 * @param selfReference
	 * @param routerTable
	 */
	public AbstractDataHandlerStation(RouterComponent component, RouterSourceTable routerTable) {
		super();
		this.component = component;
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
	public void handleData(Event data) throws DataFlowException {
		/**
		 * Add the sender if the chosen route is private·
		 */
		final DirectReference sender = data.getSender();

		if (sender != null && !this.routerTable.exist(sender)) {
			data.handledBy(sender);
		}

		/**
		 * Compute the next entry
		 */
		final DirectReference nextTarget = this.getNextTarget(data);

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
			source.getDownStreamDataFlow().handleData(data);
			return;
		} catch (CannotCreateComponentException e) {
			throw new DataFlowException(NOT_FOUND.format(nextTarget, this.toString()), e);
		}

	}

	/**
	 * Method called whether the next target must be computed
	 * 
	 * @param data
	 * @return
	 */
	protected abstract DirectReference getNextTarget(Event data);

	@Override
	public void handleClose() throws DataFlowCloseException {
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
