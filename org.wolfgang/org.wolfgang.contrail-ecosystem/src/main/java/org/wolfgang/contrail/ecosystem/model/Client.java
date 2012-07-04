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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <code>Client</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@XmlRootElement
public class Client {

	private String name;
	private String filter;
	private String endpoint;
	private String flow;

	/**
	 * Constructor
	 */
	public Client() {
		super();
	}

	/**
	 * Return the value of name
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the value of name
	 * 
	 * @param name
	 *            the name to set
	 */
	@XmlAttribute
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Return the value of filter
	 * 
	 * @return the filter
	 */
	@XmlAttribute
	public String getFilter() {
		return filter;
	}

	/**
	 * Set the value of filter
	 * 
	 * @param filter
	 *            the filter to set
	 */
	public void setFilter(String filter) {
		this.filter = filter;
	}

	/**
	 * Return the value of endpoint
	 * 
	 * @return the endpoint
	 */
	@XmlAttribute
	public String getEndpoint() {
		return endpoint;
	}

	/**
	 * Set the value of endpoint
	 * 
	 * @param endpoint
	 *            the endpoint to set
	 */
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	/**
	 * Return the value offlow
	 * 
	 * @return the flow
	 */
	@XmlElement
	public String getFlow() {
		return flow;
	}

	/**
	 * Set the value of flow
	 * 
	 * @param flow
	 *            the flow to set
	 */
	public void setFlow(String flow) {
		this.flow = flow;
	}
}
