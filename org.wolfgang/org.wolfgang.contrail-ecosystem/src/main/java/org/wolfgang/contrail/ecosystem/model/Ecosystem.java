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
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * <code>Ecosystem</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@XmlRootElement
@XmlSeeAlso({ Binder.class, Pipeline.class, Router.class, Terminal.class, Server.class })
public class Ecosystem implements Validation {

	private String flow;
	private List<Binder> binders;
	private List<Pipeline> pipelines;
	private List<Router> routers;
	private List<Terminal> terminals;
	private List<Server> servers;

	{
		this.binders = new ArrayList<Binder>();
		this.pipelines = new ArrayList<Pipeline>();
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
	 * Return the value of flow
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

	/**
	 * Return the value of entries
	 * 
	 * @return the entries
	 */
	@XmlElement(name = "binder")
	public List<Binder> getEntries() {
		return binders;
	}

	/**
	 * Set the value of entries
	 * 
	 * @param binders
	 *            the entries to set
	 */
	public void add(Binder entry) {
		this.binders.add(entry);
	}

	/**
	 * Return the value of pipelines
	 * 
	 * @return the pipelines
	 */
	@XmlElement(name = "pipeline")
	public List<Pipeline> getPipelines() {
		return pipelines;
	}

	/**
	 * Set the value of pipelines
	 * 
	 * @param transducers
	 *            the pipelines to set
	 */
	public void add(Pipeline transducer) {
		this.pipelines.add(transducer);
	}

	/**
	 * Return the value of routers
	 * 
	 * @return the routers
	 */
	@XmlElement(name = "router")
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
	@XmlElement(name = "terminal")
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
	@XmlElement(name = "server")
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

	@Override
	public void validate() throws ValidationException {
		for (Terminal terminal : terminals) {
			terminal.validate();
		}

		for (Pipeline pipeline : pipelines) {
			pipeline.validate();
		}

		for (Router router : routers) {
			router.validate();
		}

		for (Server server : servers) {
			server.validate();
		}

		for (Binder binder : binders) {
			binder.validate();
		}
	}
}
