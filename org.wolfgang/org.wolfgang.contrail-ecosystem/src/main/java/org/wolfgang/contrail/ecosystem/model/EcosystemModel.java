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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * <code>Ecosystem</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@XmlRootElement(name = "ecosystem")
@XmlSeeAlso({ BinderModel.class, PipelineModel.class, RouterModel.class, TerminalModel.class, ServerModel.class })
public class EcosystemModel implements Validation {

	private String main;
	private String requires;
	private List<FlowModel> flows;
	private List<BinderModel> binders;
	private List<PipelineModel> pipelines;
	private List<RouterModel> routers;
	private List<TerminalModel> terminals;
	private List<ServerModel> servers;

	{
		this.flows = new ArrayList<FlowModel>();
		this.binders = new ArrayList<BinderModel>();
		this.pipelines = new ArrayList<PipelineModel>();
		this.routers = new ArrayList<RouterModel>();
		this.terminals = new ArrayList<TerminalModel>();
		this.servers = new ArrayList<ServerModel>();
	}

	/**
	 * Constructor
	 */
	public EcosystemModel() {
		super();
	}

	/**
	 * Return the value of flow
	 * 
	 * @return the flow
	 */
	@XmlElement
	public String getMain() {
		return main;
	}

	/**
	 * Set the value of flow
	 * 
	 * @param flow
	 *            the flow to set
	 */
	public void setMain(String main) {
		this.main = main.trim();
	}

	/**
	 * Return the value ofrequires
	 * 
	 * @return the requires
	 */
	@XmlElement
	public String getRequires() {
		return requires;
	}

	/**
	 * Set the value of requires
	 * 
	 * @param requires
	 *            the requires to set
	 */
	public void setRequires(String requires) {
		this.requires = requires;
	}

	/**
	 * Return the value of flows
	 * 
	 * @return the flows
	 */
	@XmlElement(name = "flow")
	public List<FlowModel> getFlows() {
		return flows;
	}

	/**
	 * Set the value of flows
	 * 
	 * @param flows
	 *            the flows to set
	 */
	public void add(FlowModel flow) {
		this.flows.add(flow);
	}

	/**
	 * Return the value of entries
	 * 
	 * @return the entries
	 */
	@XmlElement(name = "binder")
	public List<BinderModel> getBinders() {
		return binders;
	}

	/**
	 * Set the value of entries
	 * 
	 * @param binders
	 *            the entries to set
	 */
	public void add(BinderModel entry) {
		this.binders.add(entry);
	}

	/**
	 * Return the value of pipelines
	 * 
	 * @return the pipelines
	 */
	@XmlElement(name = "pipeline")
	public List<PipelineModel> getPipelines() {
		return pipelines;
	}

	/**
	 * Set the value of pipelines
	 * 
	 * @param transducers
	 *            the pipelines to set
	 */
	public void add(PipelineModel transducer) {
		this.pipelines.add(transducer);
	}

	/**
	 * Return the value of routers
	 * 
	 * @return the routers
	 */
	@XmlElement(name = "router")
	public List<RouterModel> getRouters() {
		return routers;
	}

	/**
	 * Set the value of routers
	 * 
	 * @param routers
	 *            the routers to set
	 */
	public void add(RouterModel router) {
		this.routers.add(router);
	}

	/**
	 * Return the value of terminals
	 * 
	 * @return the terminals
	 */
	@XmlElement(name = "terminal")
	public List<TerminalModel> getTerminals() {
		return terminals;
	}

	/**
	 * Set the value of terminals
	 * 
	 * @param terminals
	 *            the terminals to set
	 */
	public void add(TerminalModel terminal) {
		this.terminals.add(terminal);
	}

	/**
	 * Return the value of servers
	 * 
	 * @return the servers
	 */
	@XmlElement(name = "server")
	public List<ServerModel> getServers() {
		return servers;
	}

	/**
	 * Set the value of servers
	 * 
	 * @param servers
	 *            the servers to set
	 */
	public void add(ServerModel server) {
		this.servers.add(server);
	}

	@Override
	public void validate() throws ValidationException {
		for (TerminalModel terminal : terminals) {
			terminal.validate();
		}

		for (PipelineModel pipeline : pipelines) {
			pipeline.validate();
		}

		for (RouterModel router : routers) {
			router.validate();
		}

		for (ServerModel server : servers) {
			server.validate();
		}

		for (BinderModel binder : binders) {
			binder.validate();
		}

		for (FlowModel flow : flows) {
			flow.validate();
		}
	}

	/**
	 * Method called to decode a ecosystem
	 * 
	 * @param stream
	 *            The input stream containing the ecosystem definition
	 * @return an ecosystem (Never <code>null</code>)
	 * @throws JAXBException
	 *             throws if the ecosystem cannot be decoded
	 */
	public static EcosystemModel decode(InputStream stream) throws JAXBException {
		final JAXBContext context = JAXBContext.newInstance(EcosystemModel.class);
		final Unmarshaller unmarshaller = context.createUnmarshaller();
		return (EcosystemModel) unmarshaller.unmarshal(stream);
	}
}
