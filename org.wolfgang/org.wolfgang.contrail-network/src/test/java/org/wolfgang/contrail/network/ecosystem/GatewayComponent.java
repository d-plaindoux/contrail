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

package org.wolfgang.contrail.network.ecosystem;

import java.net.URISyntaxException;

import org.wolfgang.contrail.component.annotation.ContrailTerminal;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.connection.CannotCreateClientException;
import org.wolfgang.contrail.connection.ClientFactoryCreationException;
import org.wolfgang.contrail.ecosystem.factory.EcosystemFactory;

/**
 * <code>TestComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@ContrailTerminal(name = "GateWay")
public class GatewayComponent extends TerminalComponent<byte[], byte[]> {

	/**
	 * Constructor
	 * 
	 * @param args
	 * @throws URISyntaxException
	 * @throws CannotCreateClientException
	 * @throws ClientFactoryCreationException 
	 */
	public GatewayComponent(EcosystemFactory ecosystemFactory, String... args) throws URISyntaxException, CannotCreateClientException, ClientFactoryCreationException {
		this(new GatewayReceiver(ecosystemFactory, args));
	}

	/**
	 * Constructor
	 * 
	 * @param receiver
	 */
	private GatewayComponent(GatewayReceiver receiver) {
		super(receiver);
		receiver.setComponentSender(this.getDataSender());
	}

}
