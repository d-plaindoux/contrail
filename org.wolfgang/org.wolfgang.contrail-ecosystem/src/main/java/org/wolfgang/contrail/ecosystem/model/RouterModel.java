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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.wolfgang.common.message.MessagesProvider;

/**
 * <code>Router</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@XmlRootElement(name = "router")
@XmlSeeAlso({ BinderModel.class, ClientModel.class })
public class RouterModel implements Validation {

	private String name;
	private String factory;
	private String self;
	private List<String> parameters;
	private List<ClientModel> clients;

	{
		this.clients = new ArrayList<ClientModel>();
	}

	/**
	 * Constructor
	 */
	public RouterModel() {
		super();
	}

	/**
	 * Return the value ofname
	 * 
	 * @return the name
	 */
	@XmlAttribute
	public String getName() {
		return name;
	}

	/**
	 * Set the value of name
	 * 
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * Return the value of entry
	 * 
	 * @return the entry
	 */
	@XmlElement
	public String getSelf() {
		return self;
	}

	/**
	 * Set the value of entry
	 * 
	 * @param self
	 *            the entry to set
	 */
	public void setSelf(String self) {
		this.self = self;
	}

	/**
	 * Set the value of clients
	 * 
	 * @param clients
	 *            the clients to set
	 */
	public void setClients(List<ClientModel> clients) {
		this.clients = clients;
	}

	/**
	 * Return the value of parameters
	 * 
	 * @return the parameters
	 */
	@XmlElement(name = " param")
	public List<String> getParameters() {
		return parameters;
	}

	/**
	 * Set the value of parameters
	 * 
	 * @param parameter
	 *            the parameters to set
	 */
	public void add(String parameter) {
		this.parameters.add(parameter);
	}

	/**
	 * Return the value of clients
	 * 
	 * @return the clients
	 */
	@XmlElement(name = "client")
	public List<ClientModel> getClients() {
		return clients;
	}

	/**
	 * Set the value of clients
	 * 
	 * @param clients
	 *            the clients to set
	 */
	public void add(ClientModel client) {
		this.clients.add(client);
	}

	@Override
	public void validate() throws ValidationException {
		if (this.name == null) {
			throw new ValidationException(MessagesProvider.message("org.wolfgang.contrail.ecosystem", "name.undefined").format());
		} else if (this.factory == null) {
			throw new ValidationException(MessagesProvider.message("org.wolfgang.contrail.ecosystem", "factory.undefined").format(name));
		} else if (this.self == null) {
			throw new ValidationException(MessagesProvider.message("org.wolfgang.contrail.ecosystem", "self.undefined").format(name));
		} else {
			for (ClientModel client : clients) {
				client.validate();
			}
		}
	}

}