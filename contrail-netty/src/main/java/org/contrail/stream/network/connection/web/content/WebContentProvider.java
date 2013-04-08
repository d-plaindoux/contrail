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

package org.contrail.stream.network.connection.web.content;

import java.io.IOException;

/**
 * <code>ServerPage</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface WebContentProvider {

	/**
	 * Predicate checking the availability of a given resource
	 * 
	 * @param resourceName
	 *            The resource name
	 * @return true if it can be provided; false otherwise
	 */
	boolean canProvideContent(String resourceName);

	/**
	 * Method called whether a page must be retrieved (experimental)
	 * 
	 * @param resourceName
	 *            The resource name
	 * @param definitions
	 *            The definitions used for the document unfolding
	 * @return a channel buffer containing the page
	 * @throws IOException
	 *             if a problem occurs when the page retrieval fails
	 */
	byte[] getContent(String resourceName) throws IOException;

}