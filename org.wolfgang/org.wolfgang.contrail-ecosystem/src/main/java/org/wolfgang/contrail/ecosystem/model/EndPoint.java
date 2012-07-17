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

package org.wolfgang.contrail.ecosystem.model;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * <code>EndPoint</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class EndPoint {

	private final URI endPoint;

	/**
	 * Constructor
	 * 
	 * @param endPoint
	 * @throws URISyntaxException
	 */
	public EndPoint(String endPoint) throws URISyntaxException {
		super();
		this.endPoint = new URI(endPoint);
	}

	/**
	 * @return the scheme
	 */
	public String getScheme() {
		return this.endPoint.getScheme();
	}

	/**
	 * @return the specified host IP or name
	 */
	public String getHost() {
		return this.endPoint.getHost();
	}

	/**
	 * @return the specified port number
	 */
	public int getPort() {
		return this.endPoint.getPort();
	}
}
