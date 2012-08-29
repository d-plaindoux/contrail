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

import java.net.URI;
import java.net.URISyntaxException;

import org.wolfgang.contrail.component.annotation.ContrailArgument;
import org.wolfgang.contrail.component.annotation.ContrailConstructor;
import org.wolfgang.contrail.component.annotation.ContrailPipeline;
import org.wolfgang.contrail.component.bound.DataSenderFactory;
import org.wolfgang.contrail.component.pipeline.identity.IdentityComponent;
import org.wolfgang.contrail.connection.CannotCreateServerException;
import org.wolfgang.contrail.connection.ContextFactory;
import org.wolfgang.contrail.connection.Server;
import org.wolfgang.contrail.connection.ServerFactoryCreationException;

/**
 * <code>ClientComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@ContrailPipeline(name = "ServerHandler")
public class ServerComponent<U, D> extends IdentityComponent<U, D> {

	/**
	 * Constructor
	 * 
	 * @param args
	 * @throws URISyntaxException
	 * @throws CannotCreateServerException
	 * @throws ServerFactoryCreationException
	 */
	@ContrailConstructor
	public ServerComponent(@ContrailArgument("context") ContextFactory connectionFactory, @ContrailArgument("uri") String uri, @ContrailArgument("factory") DataSenderFactory<byte[], byte[]> factory)
			throws URISyntaxException, ServerFactoryCreationException, CannotCreateServerException {

		final URI reference = new URI(uri);
		final Server server = connectionFactory.getServerFactory().get(reference.getScheme());

		server.bind(reference, factory);
	}
}
