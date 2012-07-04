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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <code>Ecosystem</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@XmlRootElement
public class Ecosystem {

	@XmlElement(name = "Transducer")
	private List<Transducer> transducers;

	@XmlElement(name = "Router")
	private List<Router> routers;

	@XmlElement(name = "Terminal")
	private List<Terminal> terminals;

	@XmlElement(name = "Server")
	private List<Server> servers;

	{
		this.transducers = new ArrayList<Transducer>();
		this.routers = new ArrayList<Router>();
		this.terminals = new ArrayList<Terminal>();
		this.servers = new ArrayList<Server>();
	}

	/**
	 * Constructor
	 */
	public Ecosystem() {
		super();
	}

	/**
	 * Return the value of transducers
	 * 
	 * @return the transducers
	 */
	public List<Transducer> getTransducers() {
		return transducers;
	}

	/**
	 * Set the value of transducers
	 * 
	 * @param transducers
	 *            the transducers to set
	 */
	public void add(Transducer transducer) {
		this.transducers.add(transducer);
	}

	/**
	 * Return the value of routers
	 * 
	 * @return the routers
	 */
	public List<Router> getRouters() {
		return routers;
	}

	/**
	 * Set the value of routers
	 * 
	 * @param routers
	 *            the routers to set
	 */
	public void add(Router router) {
		this.routers.add(router);
	}

	/**
	 * Return the value of terminals
	 * 
	 * @return the terminals
	 */
	public List<Terminal> getTerminals() {
		return terminals;
	}

	/**
	 * Set the value of terminals
	 * 
	 * @param terminals
	 *            the terminals to set
	 */
	public void add(Terminal terminal) {
		this.terminals.add(terminal);
	}

	/**
	 * Return the value of servers
	 * 
	 * @return the servers
	 */
	public List<Server> getServers() {
		return servers;
	}

	/**
	 * Set the value of servers
	 * 
	 * @param servers
	 *            the servers to set
	 */
	public void add(Server server) {
		this.servers.add(server);
	}
}
