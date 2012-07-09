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

import org.wolfgang.contrail.component.PipelineComponent;
import org.wolfgang.contrail.component.pipeline.PipelineComponentCreationException;
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
public final class EcosystemFactory {

	/**
	 * Declared pipelines
	 */
	private final Map<String, LazyPipeline> pipelines;
	private final ClassLoader classLoader;

	public interface LazyPipeline {
		public PipelineComponent<?, ?, ?, ?> create() throws PipelineComponentCreationException;
	}

	{
		this.pipelines = new HashMap<String, EcosystemFactory.LazyPipeline>();
		this.classLoader = EcosystemFactory.class.getClassLoader();
	}

	/**
	 * @param pipeline
	 * @return
	 */
	private LazyPipeline create(Pipeline pipeline) {
		final String factory = pipeline.getFactory();
		final List<String> parameters = pipeline.getParameters();
		return new LazyPipeline() {
			@Override
			public PipelineComponent<?, ?, ?, ?> create() throws PipelineComponentCreationException {
				return PipelineFactory.create(classLoader, factory, parameters.toArray(new String[parameters.size()]));
			}
		};
	}

	/**
	 * @param terminal
	 */
	private void register(Terminal terminal) {
		// TODO Auto-generated method stub
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
