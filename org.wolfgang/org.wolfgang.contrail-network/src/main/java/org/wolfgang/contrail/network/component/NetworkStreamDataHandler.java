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
import org.wolfgang.contrail.component.multiple.DataFilter;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandler;
import org.wolfgang.contrail.handler.UpStreamDataHandler;
import org.wolgang.contrail.network.event.NetworkEvent;
import org.wolgang.contrail.network.reference.ChainedReferences;
import org.wolgang.contrail.network.reference.ClientReference;
import org.wolgang.contrail.network.reference.DirectReference;
import org.wolgang.contrail.network.reference.ReferenceEntryNotFoundException;
import org.wolgang.contrail.network.reference.ReferenceVisitor;
import org.wolgang.contrail.network.reference.ServerReference;

/**
 * A <code>NetworkRouterStreamDataHandler</code> is able to manage information
 * using filters owned buy each filtered upstream destination component linked
 * to the multiplexer component. The data is a network event in this case and
 * the management is done using a dedicated network router.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class NetworkStreamDataHandler implements UpStreamDataHandler<NetworkEvent>, DownStreamDataHandler<NetworkEvent>,
		ReferenceVisitor<NetworkTable.Entry, ReferenceEntryNotFoundException> {

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

	private void handleData(NetworkEvent data, ComponentId componentId) throws DataHandlerException,
			ComponentNotConnectedException {
		component.getSourceComponent(componentId).getDownStreamDataHandler().handleData(data);
	}

	@Override
	public void handleData(NetworkEvent data) throws DataHandlerException {
		boolean notHandled = true;

		for (Entry<ComponentId, DataFilter<NetworkEvent>> entry : component.getSourceFilters().entrySet()) {
			if (entry.getValue().accept(data)) {
				try {
					this.handleData(data, entry.getKey());
					notHandled = false;
				} catch (ComponentNotConnectedException consume) {
					// Ignore
				}
			}
		}

		for (Entry<ComponentId, DataFilter<NetworkEvent>> entry : component.getDestinationFilters().entrySet()) {
			if (entry.getValue().accept(data)) {
				try {
					this.handleData(data, entry.getKey());
					notHandled = false;
				} catch (ComponentNotConnectedException consume) {
					// Ignore
				}
			}
		}

		if (notHandled) {
			try {
				final NetworkTable.Entry entry = data.getTargetReference().visit(this);
				if (entry != null) {
					this.handleData(data, entry.createDataHandler());
					notHandled = false;
				}
			} catch (ComponentNotConnectedException e) {
				// Ignore
			} catch (ReferenceEntryNotFoundException e1) {
				// Ignore
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

	@Override
	public NetworkTable.Entry visit(ClientReference reference) throws ReferenceEntryNotFoundException {
		return this.routerTable.retrieve(reference);
	}

	@Override
	public NetworkTable.Entry visit(ServerReference reference) throws ReferenceEntryNotFoundException {
		return this.routerTable.retrieve(reference);
	}

	@Override
	public NetworkTable.Entry visit(ChainedReferences reference) throws ReferenceEntryNotFoundException {
		return this.routerTable.retrieve(reference.getNextReference(this.selfReference));
	}
}
