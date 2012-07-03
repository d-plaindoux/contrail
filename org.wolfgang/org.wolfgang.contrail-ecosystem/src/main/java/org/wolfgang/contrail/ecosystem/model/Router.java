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

/**
 * <code>Router</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@XmlRootElement
public class Router {

	@XmlAttribute(name = "name")
	private String name;

	@XmlElement(name = "Entry")
	private Entry self;

	@XmlElement(name = "Client")
	private List<Client> clients;

	{
		this.clients = new ArrayList<Client>();
	}

	/**
	 * Constructor
	 */
	public Router() {
		super();
	}

	/**
	 * Return the value ofname
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
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Return the value ofself
	 * 
	 * @return the self
	 */
	public Entry getSelf() {
		return self;
	}

	/**
	 * Set the value of self
	 * 
	 * @param self
	 *            the self to set
	 */
	public void setSelf(Entry self) {
		this.self = self;
	}

	/**
	 * Return the value ofclients
	 * 
	 * @return the clients
	 */
	public List<Client> getClients() {
		return clients;
	}

	/**
	 * Set the value of clients
	 * 
	 * @param clients
	 *            the clients to set
	 */
	public void add(Client client) {
		this.clients.add(client);
	}

}
