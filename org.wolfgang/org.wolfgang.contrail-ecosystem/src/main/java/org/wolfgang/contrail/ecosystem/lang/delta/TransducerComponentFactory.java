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

package org.wolfgang.contrail.ecosystem.lang.delta;

import java.util.Map;

import org.wolfgang.contrail.component.CannotCreateComponentException;
import org.wolfgang.contrail.component.PipelineComponent;
import org.wolfgang.contrail.component.pipeline.transducer.TransducerFactory;
import org.wolfgang.contrail.connection.ContextFactory;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValue;
import org.wolfgang.contrail.link.ComponentLinkManager;

/**
 * <code>PipelineFactory</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class TransducerComponentFactory {

	/**
	 * Constructor
	 */
	private TransducerComponentFactory() {
		super();
	}

	@SuppressWarnings({ "rawtypes" })
	public static PipelineComponent create(ComponentLinkManager linkManager, ContextFactory ecosystemFactory, Class component, Map<String, CodeValue> environment)
			throws CannotCreateComponentException {
		try {
			final TransducerFactory factory = ComponentBuilder.<TransducerFactory> create(linkManager, ecosystemFactory, component, environment);
			return factory.createComponent();
		} catch (Exception e) {
			throw new CannotCreateComponentException(e);
		}
	}
}
