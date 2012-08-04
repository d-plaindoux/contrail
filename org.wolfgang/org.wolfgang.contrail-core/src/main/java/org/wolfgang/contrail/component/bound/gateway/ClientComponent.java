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

package org.wolfgang.contrail.component.bound.gateway;

import java.net.URISyntaxException;

import org.wolfgang.contrail.component.annotation.ContrailConstructor;
import org.wolfgang.contrail.component.annotation.ContrailDownType;
import org.wolfgang.contrail.component.annotation.ContrailTerminal;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.pipeline.transducer.payload.Bytes;
import org.wolfgang.contrail.connection.CannotCreateClientException;
import org.wolfgang.contrail.connection.ClientFactoryCreationException;
import org.wolfgang.contrail.connection.ContextFactory;

/**
 * <code>TestComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@ContrailTerminal(name = "Destination.Client")
@ContrailDownType(in = Bytes.class, out = Bytes.class)
public class ClientComponent extends TerminalComponent<byte[], byte[]> {

	/**
	 * Constructor
	 * 
	 * @param args
	 * @throws URISyntaxException
	 * @throws CannotCreateClientException
	 * @throws ClientFactoryCreationException
	 */
	@ContrailConstructor(arguments = { "uri" })
	public ClientComponent(ContextFactory connectionFactory, String[] uri) throws URISyntaxException, CannotCreateClientException, ClientFactoryCreationException {
		this(new ClientReceiver(connectionFactory, uri[0]));
	}

	/**
	 * Constructor
	 * 
	 * @param receiver
	 */
	private ClientComponent(ClientReceiver receiver) {
		super(receiver);
		receiver.setComponentSender(this.getDataSender());
	}

}
