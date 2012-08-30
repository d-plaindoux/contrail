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

package org.wolfgang.contrail.ecosystem.lang.delta;

import java.net.URI;
import java.net.URISyntaxException;

import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.annotation.ContrailArgument;
import org.wolfgang.contrail.connection.Client;
import org.wolfgang.contrail.connection.ClientFactoryCreationException;
import org.wolfgang.contrail.connection.ContextFactory;
import org.wolfgang.contrail.ecosystem.lang.code.ClosureValue;

/**
 * <code>ComponentFactory</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ComponentFactory {

	@ContrailFactory("Client")
	public Component create(@ContrailArgument("context") ContextFactory context, @ContrailArgument("uri") String reference, @ContrailArgument("factory") ClosureValue code) throws URISyntaxException,
			ClientFactoryCreationException {
		final URI uri = new URI(reference);
		final Client client = context.getClientFactory().get(uri.getScheme());

		return null;
	}

}
