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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.wolfgang.common.message.MessagesProvider;

/**
 * <code>Client</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@XmlRootElement(name = "server")
public class ServerModel implements Validation {

	private String factory;
	private String endpoint;
	private String flow;

	/**
	 * Constructor
	 */
	public ServerModel() {
		super();
	}

	/**
	 * Return the value of factory
	 * 
	 * @return the factory
	 */
	@XmlAttribute
	public String getFactory() {
		return factory;
	}

	/**
	 * Set the value of factory
	 * 
	 * @param factory
	 *            the factory to set
	 */
	public void setFactory(String factory) {
		this.factory = factory;
	}

	/**
	 * Return the value of endpoint
	 * 
	 * @return the endpoint
	 */
	@XmlAttribute
	String getEndpoint() {
		return endpoint;
	}

	/**
	 * Set the value of endpoint
	 * 
	 * @param endpoint
	 *            the endpoint to set
	 */
	void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	/**
	 * Return the value offlow
	 * 
	 * @return the flow
	 */
	@XmlValue
	String getFlow() {
		return flow;
	}

	/**
	 * Set the value of flow
	 * 
	 * @param flow
	 *            the flow to set
	 */
	void setFlow(String flow) {
		this.flow = flow.trim();
	}

	@Override
	public void validate() throws ValidationException {
		if (this.endpoint == null) {
			throw new ValidationException(MessagesProvider.message("org.wolfgang.contrail.ecosystem", "endpoint.server.undefined").format());
		} else if (this.factory == null) {
			throw new ValidationException(MessagesProvider.message("org.wolfgang.contrail.ecosystem", "factory.undefined").format(endpoint));
		} else if (this.flow == null || this.flow.trim().length() == 0) {
			throw new ValidationException(MessagesProvider.message("org.wolfgang.contrail.ecosystem", "flow.undefined").format(endpoint));
		}
	}
}
