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

package org.wolfgang.contrail.connection;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;

import org.wolfgang.contrail.flow.CannotCreateDataFlowException;
import org.wolfgang.contrail.flow.UpStreamDataFlowFactory;

/**
 * <code>Client</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface Server extends Closeable {

	/**
	 * Method called whether a client connection must be established . For this
	 * purpose an URI is given and the data sender factory is also used.
	 * 
	 * @param uri
	 *            The uniform resource information
	 * @param factory
	 *            The data sender factory
	 * @return a future denoting the connection liveness
	 * @throws IOException
	 * @throws CannotCreateDataFlowException
	 */
	Worker bind(URI uri, UpStreamDataFlowFactory<byte[], byte[]> factory) throws CannotCreateServerException;

}
