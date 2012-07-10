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

/**
 * <code>Router</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@XmlRootElement
@XmlSeeAlso({ Entry.class, Client.class })
public class Router implements Validation {

	private String name;
	private String factory;
	private String self;
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
	public void setClients(List<Client> clients) {
		this.clients = clients;
	}

	/**
	 * Return the value of clients
	 * 
	 * @return the clients
	 */
	@XmlElement(name = "client")
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

	@Override
	public void validate() throws ValidationException {
		// TODO Auto-generated method stub
		
	}

}
