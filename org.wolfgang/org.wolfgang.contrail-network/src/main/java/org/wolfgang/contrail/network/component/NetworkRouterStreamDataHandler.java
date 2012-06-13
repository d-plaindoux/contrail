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

/**
 * A <code>FilteredDeMultiplexerDataHandler</code> is able to manage information
 * using filters owned buy each filtered upstream destination component linked
 * to the multiplexer component.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class NetworkRouterStreamDataHandler implements UpStreamDataHandler<NetworkEvent>, DownStreamDataHandler<NetworkEvent> {

	/**
	 * The component in charge of managing this multiplexer
	 */
	private final NetworkRouterComponent component;

	/**
	 * Constructor
	 * 
	 * @param upStreamDeMultiplexer
	 */
	public NetworkRouterStreamDataHandler(NetworkRouterComponent component) {
		super();
		this.component = component;
	}

	@Override
	public void handleData(NetworkEvent data) throws DataHandlerException {
		boolean notHandled = true;

		for (Entry<ComponentId, DataFilter<NetworkEvent>> entry : component.getSourceFilters().entrySet()) {
			if (entry.getValue().accept(data)) {
				try {
					component.getSourceComponent(entry.getKey()).getDownStreamDataHandler().handleData(data);
					notHandled = false;
				} catch (ComponentNotConnectedException consume) {
					// Ignore
				}
			}
		}

		for (Entry<ComponentId, DataFilter<NetworkEvent>> entry : component.getDestinationFilters().entrySet()) {
			if (entry.getValue().accept(data)) {
				try {
					component.getDestinationComponent(entry.getKey()).getUpStreamDataHandler().handleData(data);
					notHandled = false;
				} catch (ComponentNotConnectedException consume) {
					// Ignore
				}
			}
		}

		if (notHandled) {
			// Can we handle this message ?
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
}
