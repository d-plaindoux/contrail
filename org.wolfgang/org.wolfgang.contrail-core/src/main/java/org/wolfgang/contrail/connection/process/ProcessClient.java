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

package org.wolfgang.contrail.connection.process;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.UnknownHostException;

import org.wolfgang.common.utils.Pair;
import org.wolfgang.contrail.connection.AbstractClient;
import org.wolfgang.contrail.connection.CannotCreateClientException;

/**
 * The <code>ProcessClient</code> provides a client implementation using
 * standard libraries runtime process creation. This can be used to create a
 * connection between two framework using SSH for example.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ProcessClient extends AbstractClient {
	@Override
	protected Pair<InputStream, OutputStream> getClient(URI uri) throws CannotCreateClientException {
		try {
			final Process client = Runtime.getRuntime().exec(uri.getPath());
			return new Pair<InputStream, OutputStream>(client.getInputStream(), client.getOutputStream());
		} catch (UnknownHostException e) {
			throw new CannotCreateClientException(e);
		} catch (IOException e) {
			throw new CannotCreateClientException(e);
		}
	}
}
