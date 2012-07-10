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

/**
 * <code>EcosystemChecker</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class EcosystemChecker {

	/**
	 * 
	 */
	private final Ecosystem ecosystem;

	/**
	 * Constructor
	 * 
	 * @param ecosystem
	 */
	public EcosystemChecker(Ecosystem ecosystem) {
		super();
		this.ecosystem = ecosystem;
	};

	public List<String> getFreeVariables() {
		final List<String> flows = new ArrayList<String>();
		final List<String> boundVariables = new ArrayList<String>();
		final List<String> freeVariables = new ArrayList<String>();

		for (Pipeline pipeline : ecosystem.getPipelines()) {
			boundVariables.add(pipeline.getName());
			freeVariables.remove(pipeline.getName());
		}

		for (Terminal terminal : ecosystem.getTerminals()) {
			boundVariables.add(terminal.getName());
			freeVariables.remove(terminal.getName());
		}

		for (Router router : ecosystem.getRouters()) {
			boundVariables.add(router.getName());
			freeVariables.remove(router.getName());

			for (Client client : router.getClients()) {
				flows.add(client.getFlow());
			}
		}

		for (Server server : ecosystem.getServers()) {
			// Check the end point
			flows.add(server.getFlow());
		}

		for (Entry entry : ecosystem.getEntries()) {
			// Check the end point
			flows.add(entry.getFlow());
		}

		flows.add(ecosystem.getFlow());

		for (String client : flows) {
			for (String flow : Flow.decompose(client)) {
				if (!boundVariables.contains(flow) && !freeVariables.add(flow)) {
					freeVariables.add(flow);
				}
			}
		}

		return freeVariables;
	}
}
