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

package org.wolfgang.contrail.connection.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;

import org.wolfgang.common.utils.Pair;
import org.wolfgang.contrail.component.annotation.ContrailClient;
import org.wolfgang.contrail.component.annotation.ContrailType;
import org.wolfgang.contrail.connection.AbstractClient;
import org.wolfgang.contrail.connection.CannotCreateClientException;

/**
 * The <code>NetClient</code> provides a client implementation using standard
 * libraries like sockets and server sockets. The current implementation don't
 * use the new IO libraries and select mechanism. As a consequence this
 * implementation is not meant to be scalable as required for modern framework
 * like web portal. Nevertheless this can be enough for an optimized network
 * layer relaying on federation network links between components particularly on
 * presence of multiple hop network links.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@ContrailClient(scheme = "tcp", type = @ContrailType(in = byte[].class, out = byte[].class))
public class NetClient extends AbstractClient {

	@Override
	protected Pair<InputStream, OutputStream> getClient(URI uri) throws CannotCreateClientException {
		try {
			final Socket client = new Socket(uri.getHost(), uri.getPort());
			return new Pair<InputStream, OutputStream>(client.getInputStream(), client.getOutputStream());
		} catch (UnknownHostException e) {
			throw new CannotCreateClientException(e);
		} catch (IOException e) {
			throw new CannotCreateClientException(e);
		}
	}

}
