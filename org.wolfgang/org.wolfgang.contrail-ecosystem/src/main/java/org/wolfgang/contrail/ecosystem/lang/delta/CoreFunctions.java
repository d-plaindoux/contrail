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

import java.net.URISyntaxException;

import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.connection.ClientComponent;
import org.wolfgang.contrail.component.connection.ServerComponent;
import org.wolfgang.contrail.component.factory.Components;
import org.wolfgang.contrail.component.pipeline.concurrent.ParallelDestinationComponent;
import org.wolfgang.contrail.component.pipeline.concurrent.ParallelSourceComponent;
import org.wolfgang.contrail.component.pipeline.logger.LoggerDestinationComponent;
import org.wolfgang.contrail.component.pipeline.logger.LoggerSourceComponent;
import org.wolfgang.contrail.component.pipeline.transducer.coercion.CoercionTransducerFactory;
import org.wolfgang.contrail.component.pipeline.transducer.payload.PayLoadTransducerFactory;
import org.wolfgang.contrail.component.pipeline.transducer.serializer.SerializationTransducerFactory;
import org.wolfgang.contrail.connection.CannotCreateServerException;
import org.wolfgang.contrail.connection.ClientNotFoundException;
import org.wolfgang.contrail.connection.ComponentFactoryListener;
import org.wolfgang.contrail.connection.ContextFactory;
import org.wolfgang.contrail.connection.ServerNotFoundException;
import org.wolfgang.contrail.connection.process.ProcessClient;
import org.wolfgang.contrail.connection.process.ProcessServer;
import org.wolfgang.contrail.ecosystem.annotation.ContrailArgument;
import org.wolfgang.contrail.ecosystem.annotation.ContrailLibrary;
import org.wolfgang.contrail.ecosystem.annotation.ContrailMethod;

/**
 * <code>ReverseFunction</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@ContrailLibrary
public class CoreFunctions {

	@ContrailMethod
	public static NativeFunction<Void> init() {
		return new NativeFunction<Void>() {
			@Override
			public Void create(ContextFactory contextFactory) throws ComponentConnectionRejectedException {
				contextFactory.getClientFactory().declareScheme("ssh", ProcessClient.class);
				contextFactory.getServerFactory().declareScheme("ssh", ProcessServer.class);
				return null;
			}
		};
	}

	@ContrailMethod
	public static NativeFunction<Component> reverse(final @ContrailArgument("flow") Component component) throws ComponentConnectionRejectedException {
		return new NativeFunction<Component>() {
			@Override
			public Component create(ContextFactory contextFactory) throws ComponentConnectionRejectedException {
				return Components.reverse(contextFactory.getLinkManager(), component);
			}
		};
	}

	//
	// Client and Server hooks
	//

	@ContrailMethod
	public static NativeFunction<Component> client(final @ContrailArgument("uri") String reference) throws ComponentConnectionRejectedException, URISyntaxException, ClientNotFoundException {
		return new NativeFunction<Component>() {
			@Override
			public Component create(ContextFactory contextFactory) throws Exception {
				return new ClientComponent(contextFactory, reference);
			}
		};
	}

	@ContrailMethod
	public static NativeFunction<Component> server(final @ContrailArgument("uri") String reference, final @ContrailArgument("flow") ComponentFactoryListener listener) throws URISyntaxException,
			ServerNotFoundException, CannotCreateServerException {
		return new NativeFunction<Component>() {
			@Override
			public Component create(ContextFactory contextFactory) throws Exception {
				return new ServerComponent(contextFactory, reference, listener);
			}
		};
	}

	//
	// Basic pipelines and transducers
	//

	@SuppressWarnings("rawtypes")
	@ContrailMethod
	public static Component parallelSource() {
		return new ParallelSourceComponent();
	}

	@SuppressWarnings("rawtypes")
	@ContrailMethod
	public static Component parallelDestination() {
		return new ParallelDestinationComponent();
	}

	@SuppressWarnings("rawtypes")
	@ContrailMethod
	public static Component logSource(final @ContrailArgument("name") String prefix) {
		return new LoggerSourceComponent(prefix);
	}

	@SuppressWarnings("rawtypes")
	@ContrailMethod
	public static Component logDestination(final @ContrailArgument("name") String prefix) {
		return new LoggerDestinationComponent(prefix);
	}

	@ContrailMethod
	public static Component payload() {
		return new PayLoadTransducerFactory().createComponent();
	}

	@ContrailMethod
	public static Component object() {
		return new SerializationTransducerFactory().createComponent();
	}

	@SuppressWarnings("rawtypes")
	@ContrailMethod
	public static Component coerce(final @ContrailArgument("type") String type) throws ClassNotFoundException {
		return new CoercionTransducerFactory(CoercionTransducerFactory.class.getClassLoader(), type).createComponent();
	}
}
