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

import org.wolfgang.contrail.component.annotation.ContrailArgument;
import org.wolfgang.contrail.component.annotation.ContrailConstructor;
import org.wolfgang.contrail.component.annotation.ContrailInitial;
import org.wolfgang.contrail.component.annotation.ContrailType;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.connection.CannotCreateClientException;
import org.wolfgang.contrail.connection.ClientFactoryCreationException;
import org.wolfgang.contrail.connection.ContextFactory;

/**
 * <code>ClientComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@ContrailInitial(name = "ClientHandler", type = @ContrailType(in = byte[].class, out = byte[].class))
public class ClientComponent2 extends InitialComponent<byte[], byte[]> {

	/**
	 * Constructor
	 * 
	 * @param args
	 * @throws URISyntaxException
	 * @throws CannotCreateClientException
	 * @throws ClientFactoryCreationException
	 */
	@ContrailConstructor
	public ClientComponent2(@ContrailArgument("context") ContextFactory connectionFactory, @ContrailArgument("uri") String uri) throws URISyntaxException, CannotCreateClientException,
			ClientFactoryCreationException {
		this(new ClientReceiver(connectionFactory, uri));
	}

	/**
	 * Constructor
	 * 
	 * @param receiver
	 */
	private ClientComponent2(ClientReceiver receiver) {
		super(receiver);
		receiver.setComponentSender(this.getDataSender());
	}

}
