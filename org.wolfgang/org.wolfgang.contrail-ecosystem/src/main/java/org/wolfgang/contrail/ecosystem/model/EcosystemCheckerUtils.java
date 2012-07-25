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

import org.wolfgang.contrail.ecosystem.model.FlowModel.Item;

/**
 * <code>EcosystemCheckerUtils</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class EcosystemCheckerUtils {

	/**
	 * Constructor
	 * 
	 * @param ecosystem
	 */
	private EcosystemCheckerUtils(EcosystemModel ecosystem) {
		super();
	};

	/**
	 * Method called whether the list of unused reference is required
	 * 
	 * @return a string list
	 */
	public static List<String> getUnusedReferences(EcosystemModel ecosystem) {
		final List<String> flows = new ArrayList<String>();
		final List<String> unusedVariables = new ArrayList<String>();

		for (PipelineModel pipeline : ecosystem.getPipelines()) {
			unusedVariables.add(pipeline.getName());
		}

		for (TerminalModel terminal : ecosystem.getTerminals()) {
			unusedVariables.add(terminal.getName());
		}

		for (RouterModel router : ecosystem.getRouters()) {
			unusedVariables.add(router.getName());

			for (ClientModel client : router.getClients()) {
				flows.add(client.getFlow());
			}
		}

		for (FlowModel entry : ecosystem.getFlows()) {
			unusedVariables.add(entry.getName());
			flows.add(entry.getValue());
		}

		for (ServerModel server : ecosystem.getServers()) {
			// Check the end point
			flows.add(server.getFlow());
		}

		for (BinderModel entry : ecosystem.getBinders()) {
			// Check the end point
			flows.add(entry.getFlow());
		}

		flows.add(ecosystem.getMain());

		for (String client : flows) {
			for (Item flow : FlowModel.decompose(client)) {
				if (unusedVariables.contains(flow.getName())) {
					unusedVariables.remove(flow.getName());
				}
			}
		}

		return unusedVariables;
	}

	/**
	 * Method called whether the list of unknown reference is required. Unknown
	 * means a dangling reference has been found will validation the proposed
	 * model
	 * 
	 * @return a string list
	 */
	public static List<String> getUnknownReferences(EcosystemModel ecosystem) {
		final List<String> flows = new ArrayList<String>();
		final List<String> boundVariables = new ArrayList<String>();
		final List<String> unknownVariables = new ArrayList<String>();

		for (PipelineModel pipeline : ecosystem.getPipelines()) {
			boundVariables.add(pipeline.getName());
		}

		for (TerminalModel terminal : ecosystem.getTerminals()) {
			boundVariables.add(terminal.getName());
		}

		for (RouterModel router : ecosystem.getRouters()) {
			boundVariables.add(router.getName());

			for (ClientModel client : router.getClients()) {
				flows.add(client.getFlow());
			}
		}

		for (FlowModel entry : ecosystem.getFlows()) {
			boundVariables.add(entry.getName());
			flows.add(entry.getValue());
		}

		for (ServerModel server : ecosystem.getServers()) {
			// Check the end point
			flows.add(server.getFlow());
		}

		for (BinderModel entry : ecosystem.getBinders()) {
			// Check the end point
			flows.add(entry.getFlow());
		}

		flows.add(ecosystem.getMain());

		for (String client : flows) {
			for (Item flow : FlowModel.decompose(client)) {
				if (!boundVariables.contains(flow.getName()) && !unknownVariables.add(flow.getName())) {
					unknownVariables.add(flow.getName());
				}

				if (flow.asAlias() && !boundVariables.contains(flow.getAlias())) {
					boundVariables.add(flow.getAlias());
					unknownVariables.remove(flow.getAlias());
				}
			}
		}

		return unknownVariables;
	}
}
