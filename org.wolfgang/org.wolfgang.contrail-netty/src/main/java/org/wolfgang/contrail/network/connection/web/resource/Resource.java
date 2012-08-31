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

package org.wolfgang.contrail.network.connection.web.resource;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * A <code>Resource</code> is the basic information sent to a client when an
 * http request has been received and managed.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface Resource {

	/**
	 * Method called when the content must be computed with a given set of
	 * defined variables identified by a name and bind to a given string value.
	 * 
	 * @param definitions
	 *            The set of variables
	 * @return a channel buffer containing the evaluated resource
	 * @throws IOException
	 */
	byte[] getContent(Map<String, String> definitions) throws IOException;

	/**
	 * @return the set of required variables
	 */
	Collection<String> getFreeVariables();
}
