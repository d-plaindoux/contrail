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

package org.wolfgang.contrail.component.connection;

import java.net.URI;
import java.net.URISyntaxException;

import org.wolfgang.contrail.component.ComponentConnectedException;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;
import org.wolfgang.contrail.component.annotation.ContrailArgument;
import org.wolfgang.contrail.component.annotation.ContrailConstructor;
import org.wolfgang.contrail.component.annotation.ContrailPipeline;
import org.wolfgang.contrail.component.pipeline.AbstractPipelineComponent;
import org.wolfgang.contrail.connection.CannotCreateClientException;
import org.wolfgang.contrail.connection.Client;
import org.wolfgang.contrail.connection.ClientNotFoundException;
import org.wolfgang.contrail.connection.ContextFactory;
import org.wolfgang.contrail.flow.DownStreamDataFlow;
import org.wolfgang.contrail.flow.UpStreamDataFlow;
import org.wolfgang.contrail.link.ComponentLink;
import org.wolfgang.contrail.link.DestinationComponentLink;

/**
 * <code>ClientComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@ContrailPipeline(name = "ClientComponent")
public class ClientComponent extends AbstractPipelineComponent<byte[], byte[], byte[], byte[]> {

	private final Client client;
	private final URI uri;

	/**
	 * Constructor
	 * 
	 * @param client
	 * @throws URISyntaxException
	 * @throws ClientNotFoundException
	 */
	@ContrailConstructor
	public ClientComponent(@ContrailArgument("context") ContextFactory contextFactory, @ContrailArgument("uri") String reference) throws URISyntaxException, ClientNotFoundException {
		super();
		this.uri = new URI(reference);
		this.client = contextFactory.getClientFactory().get(uri.getScheme());
	}

	@Override
	public UpStreamDataFlow<byte[]> getUpStreamDataFlow() {
		return getDestinationComponentLink().getDestinationComponent().getUpStreamDataFlow();
	}

	@Override
	public DownStreamDataFlow<byte[]> getDownStreamDataFlow() {
		return getSourceComponentLink().getSourceComponent().getDownStreamDataFlow();
	}

	@Override
	public ComponentLink connectDestination(DestinationComponentLink<byte[], byte[]> handler) throws ComponentConnectedException {
		final ComponentLink connectDestination = super.connectDestination(handler);

		try {
			handler.getComponentLinkManager().connect(client.connect(uri), this);
		} catch (ComponentConnectionRejectedException e) {
			try {
				connectDestination.dispose();
			} catch (ComponentDisconnectionRejectedException e1) {
				// Ignore
			}
			throw new ComponentConnectedException(e);
		} catch (CannotCreateClientException e) {
			try {
				connectDestination.dispose();
			} catch (ComponentDisconnectionRejectedException e1) {
				// Ignore
			}
			throw new ComponentConnectedException(e);
		}

		return connectDestination;
	}
}
