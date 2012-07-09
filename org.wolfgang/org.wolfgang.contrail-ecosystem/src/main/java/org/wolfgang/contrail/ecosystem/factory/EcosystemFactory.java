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

package org.wolfgang.contrail.ecosystem.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.PipelineComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.bound.TerminalFactory;
import org.wolfgang.contrail.component.pipeline.PipelineFactory;
import org.wolfgang.contrail.ecosystem.model.Ecosystem;
import org.wolfgang.contrail.ecosystem.model.Pipeline;
import org.wolfgang.contrail.ecosystem.model.Router;
import org.wolfgang.contrail.ecosystem.model.Terminal;

/**
 * <code>EcosystemFactory</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@SuppressWarnings("rawtypes")
public final class EcosystemFactory {

	/**
	 * <code>Lazy</code>
	 * 
	 * @author Didier Plaindoux
	 * @version 1.0
	 */
	public interface Lazy<C extends Component> {
		public C create() throws CannotCreateComponentException;
	}

	/**
	 * The class loader to use when components must be created
	 */
	private final ClassLoader classLoader;

	/**
	 * Declared pipelines
	 */
	private final Map<String, Lazy<PipelineComponent>> pipelines;

	/**
	 * Declared terminal
	 */
	private final Map<String, Lazy<TerminalComponent>> terminals;

	{
		this.pipelines = new HashMap<String, Lazy<PipelineComponent>>();
		this.terminals = new HashMap<String, Lazy<TerminalComponent>>();
		this.classLoader = EcosystemFactory.class.getClassLoader();
	}

	/**
	 * @param pipeline
	 * @return
	 */
	private Lazy<PipelineComponent> create(Pipeline pipeline) {
		final String factory = pipeline.getFactory();
		final List<String> parameters = pipeline.getParameters();
		return new Lazy<PipelineComponent>() {
			@Override
			public PipelineComponent create() throws CannotCreateComponentException {
				return PipelineFactory.create(classLoader, factory, parameters.toArray(new String[parameters.size()]));
			}
		};
	}

	/**
	 * @param pipeline
	 * @return
	 */
	private Lazy<TerminalComponent> create(Terminal terminal) {
		final String factory = terminal.getFactory();
		final List<String> parameters = terminal.getParameters();
		return new Lazy<TerminalComponent>() {
			@Override
			public TerminalComponent create() throws CannotCreateComponentException {
				return TerminalFactory.create(classLoader, factory, parameters.toArray(new String[parameters.size()]));
			}
		};
	}

	/**
	 * @param terminal
	 */
	private void register(Terminal terminal) {
		this.terminals.put(terminal.getName(), create(terminal));
	}

	/**
	 * @param pipeline
	 */
	private void register(Pipeline pipeline) {
		this.pipelines.put(pipeline.getName(), create(pipeline));
	}

	/**
	 * @param router
	 */
	private void register(Router router) {
		// TODO Auto-generated method stub
	}

	/**
	 * Main method called whether an ecosystem must be created
	 * 
	 * @param ecosystem
	 */
	public org.wolfgang.contrail.ecosystem.Ecosystem build(Ecosystem ecosystem) {
		for (Terminal terminal : ecosystem.getTerminals()) {
			register(terminal);
		}

		for (Pipeline pipeline : ecosystem.getPipelines()) {
			register(pipeline);
		}

		for (Router router : ecosystem.getRouters()) {
			register(router);
		}

		return null;
	}
}
