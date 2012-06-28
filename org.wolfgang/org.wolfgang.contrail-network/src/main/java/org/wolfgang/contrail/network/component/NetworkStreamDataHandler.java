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
public class NetworkStreamDataHandler implements DownStreamDataHandler<NetworkEvent>,
		ReferenceVisitor<SourceComponent<NetworkEvent, NetworkEvent>, CannotCreateComponentException> {

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
		 * Local Routing
		 */
		if (data.getTargetReference().equals(getSelfReference())) {
			component.getUpStreamDataHandler().handleData(data);
			return;
		}

		for (Entry<ComponentId, DirectReference> entry : component.getSourceFilters().entrySet()) {
			if (data.getTargetReference().equals(entry.getValue())) {
				try {
					component.getSourceComponent(entry.getKey()).getDownStreamDataHandler().handleData(data);
					return;
				} catch (ComponentNotConnectedException consume) {
					// Ignore
				}
			}
		}

		try {
			data.getTargetReference().visit(this).getDownStreamDataHandler().handleData(data.sentBy(this.getSelfReference()));
			return;
		} catch (CannotCreateComponentException e) {
			// Ignore
		}

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

	private SourceComponent<NetworkEvent, NetworkEvent> createSource(DirectReference reference)
			throws CannotCreateComponentException {
		try {
			final NetworkTable.Entry retrieve = this.routerTable.retrieve(reference);
			return retrieve.create(reference);
		} catch (ReferenceEntryNotFoundException e) {
			throw new CannotCreateComponentException(e);
		}
	}

	@Override
	public SourceComponent<NetworkEvent, NetworkEvent> visit(ClientReference reference) throws CannotCreateComponentException {
		return this.createSource(reference);
	}

	@Override
	public SourceComponent<NetworkEvent, NetworkEvent> visit(ServerReference reference) throws CannotCreateComponentException {
		return this.createSource(reference);
	}

	@Override
	public SourceComponent<NetworkEvent, NetworkEvent> visit(ChainedReferences reference) throws CannotCreateComponentException {
		if (reference.hasNextReference(this.selfReference)) {
			final DirectReference nextReference = reference.getNextReference(this.selfReference);
			return this.createSource(nextReference);
		} else {
			throw new CannotCreateComponentException(/* TODO */);
		}
	}
}
