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

import java.io.IOException;
import java.util.concurrent.Future;

import org.wolfgang.contrail.component.SourceComponent;
import org.wolfgang.contrail.component.bound.DataReceiver;
import org.wolfgang.contrail.handler.DataHandlerCloseException;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.link.ComponentLink;
import org.wolfgang.contrail.link.ComponentLinkImpl;
import org.wolfgang.contrail.link.DestinationComponentLink;
import org.wolfgang.contrail.network.event.NetworkEvent;
import org.wolfgang.contrail.network.reference.DirectReference;

/**
 * <code>NetworkHandShake</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class NetworkHandShake implements DataReceiver<NetworkEvent> {

	private final Future<ComponentLinkImpl> future;
	private final NetworkComponent component;
	private final SourceComponent<NetworkEvent, NetworkEvent> sourceComponent;

	/**
	 * Constructor
	 * 
	 * @param component
	 * @param future
	 * @param componentLinkManager
	 * @param sourceComponent
	 */
	public NetworkHandShake(Future<ComponentLinkImpl> future, NetworkComponent component, SourceComponent<NetworkEvent, NetworkEvent> sourceComponent) {
		super();
		this.future = future;
		this.component = component;
		this.sourceComponent = sourceComponent;
	}

	@Override
	public void close() throws IOException {
		try {
			this.sourceComponent.closeDownStream();
		} catch (DataHandlerCloseException consume) {
			// Ignore
		}
		try {
			this.sourceComponent.closeDownStream();
		} catch (DataHandlerCloseException consume) {
			// Ignore
		}
	}

	@Override
	public void receiveData(NetworkEvent data) throws DataHandlerException {
		try {
			// Retrieve the component reference
			final DirectReference senderReference = data.getSender();
			final DirectReference receiverReference = component.getSelfReference();

			System.err.println(receiverReference + " - Accept a client from " + senderReference + " [Finishing handshake stage]");

			// Re-set the established link
			final ComponentLinkImpl destinationLink = future.get();

			destinationLink.dispose();
			destinationLink.getComponentLinkManager().connect(sourceComponent, component);

			if (senderReference == null || senderReference.equals(component.getSelfReference())) {
				sourceComponent.closeDownStream();
			} else {
				component.filterSource(sourceComponent.getComponentId(), senderReference);
			}
			// Re-send the event to the network
			// component
			component.getDownStreamDataHandler().handleData(data);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataHandlerException(e);
		}
	}
}
