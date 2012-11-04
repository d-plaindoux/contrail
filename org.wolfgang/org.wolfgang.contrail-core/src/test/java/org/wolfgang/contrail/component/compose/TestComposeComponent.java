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

package org.wolfgang.contrail.component.compose;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.wolfgang.common.concurrent.FutureResponse;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.factory.Components;
import org.wolfgang.contrail.component.pipeline.transducer.coercion.CoercionTransducerFactory;
import org.wolfgang.contrail.component.pipeline.transducer.payload.PayLoadTransducerFactory;
import org.wolfgang.contrail.component.pipeline.transducer.serializer.SerializationTransducerFactory;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.DownStreamDataFlowAdapter;
import org.wolfgang.contrail.flow.UpStreamDataFlowAdapter;
import org.wolfgang.contrail.link.ComponentManager;

/**
 * <code>TestComposeComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestComposeComponent {

	@Test
	public void testCompose01() throws ComponentConnectionRejectedException, DataFlowException, InterruptedException, ExecutionException, ComponentNotConnectedException {
		final String source = new String("Hello, World!");
		final FutureResponse<byte[]> sourceFuture = new FutureResponse<byte[]>();
		final FutureResponse<String> terminalFuture = new FutureResponse<String>();

		final Component[] pipelines = { new PayLoadTransducerFactory().createComponent(), new SerializationTransducerFactory().createComponent(),
				new CoercionTransducerFactory<String>(String.class).createComponent() };

		final InitialComponent<byte[], byte[]> initialComponent = Components.initial(new DownStreamDataFlowAdapter<byte[]>() {
			@Override
			public void handleData(byte[] data) throws DataFlowException {
				super.handleData(data);
				sourceFuture.setValue(data);
			}
		});

		final ComponentManager componentLinkManagerImpl = new ComponentManager();
		final Component composedComponent = Components.compose(componentLinkManagerImpl, pipelines);

		final TerminalComponent<String, String> terminalComponent = Components.terminal(new UpStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				terminalFuture.setValue(data);
			}
		});

		componentLinkManagerImpl.connect(initialComponent, composedComponent);
		componentLinkManagerImpl.connect(composedComponent, terminalComponent);

		terminalComponent.getDownStreamDataHandler().handleData(source);
		initialComponent.getUpStreamDataFlow().handleData(sourceFuture.get());

		assertEquals(source, terminalFuture.get());
	}

	@Test
	public void testCompose02() throws ComponentConnectionRejectedException, DataFlowException, InterruptedException, ExecutionException, ComponentNotConnectedException {
		final String source = new String("Hello, World!");
		final FutureResponse<byte[]> sourceFuture = new FutureResponse<byte[]>();
		final FutureResponse<String> terminalFuture = new FutureResponse<String>();

		final InitialComponent<byte[], byte[]> initialComponent = Components.initial(new DownStreamDataFlowAdapter<byte[]>() {
			@Override
			public void handleData(byte[] data) throws DataFlowException {
				super.handleData(data);
				sourceFuture.setValue(data);
			}
		});

		final TerminalComponent<String, String> terminalComponent = Components.terminal(new UpStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				terminalFuture.setValue(data);
			}
		});

		final Component[] components = { initialComponent, new PayLoadTransducerFactory().createComponent(), new SerializationTransducerFactory().createComponent(),
				new CoercionTransducerFactory<String>(String.class).createComponent(), terminalComponent };

		final ComponentManager componentLinkManagerImpl = new ComponentManager();

		Components.compose(componentLinkManagerImpl, components);

		terminalComponent.getDownStreamDataHandler().handleData(source);
		initialComponent.getUpStreamDataFlow().handleData(sourceFuture.get());

		assertEquals(source, terminalFuture.get());
	}

	@Test
	public void testCompose03() throws ComponentConnectionRejectedException, DataFlowException, InterruptedException, ExecutionException, ComponentNotConnectedException {
		final String source = new String("Hello, World!");
		final FutureResponse<byte[]> sourceFuture = new FutureResponse<byte[]>();
		final FutureResponse<String> terminalFuture = new FutureResponse<String>();

		final InitialComponent<byte[], byte[]> initialComponent = Components.initial(new DownStreamDataFlowAdapter<byte[]>() {
			@Override
			public void handleData(byte[] data) throws DataFlowException {
				super.handleData(data);
				sourceFuture.setValue(data);
			}
		});

		final TerminalComponent<String, String> terminalComponent = Components.terminal(new UpStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				terminalFuture.setValue(data);
			}
		});

		final Component[] components = { new PayLoadTransducerFactory().createComponent(), new SerializationTransducerFactory().createComponent(),
				new CoercionTransducerFactory<String>(String.class).createComponent(), terminalComponent };

		final ComponentManager componentLinkManagerImpl = new ComponentManager();

		final Component compose = Components.compose(componentLinkManagerImpl, components);

		componentLinkManagerImpl.connect(initialComponent, compose);

		terminalComponent.getDownStreamDataHandler().handleData(source);
		initialComponent.getUpStreamDataFlow().handleData(sourceFuture.get());

		assertEquals(source, terminalFuture.get());
	}

	@Test
	public void testCompose04() throws ComponentConnectionRejectedException, DataFlowException, InterruptedException, ExecutionException, ComponentNotConnectedException {
		final String source = new String("Hello, World!");
		final FutureResponse<byte[]> sourceFuture = new FutureResponse<byte[]>();
		final FutureResponse<String> terminalFuture = new FutureResponse<String>();

		final InitialComponent<byte[], byte[]> initialComponent = Components.initial(new DownStreamDataFlowAdapter<byte[]>() {
			@Override
			public void handleData(byte[] data) throws DataFlowException {
				super.handleData(data);
				sourceFuture.setValue(data);
			}
		});

		final TerminalComponent<String, String> terminalComponent = Components.terminal(new UpStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				terminalFuture.setValue(data);
			}
		});

		final Component[] components = { initialComponent, new PayLoadTransducerFactory().createComponent(), new SerializationTransducerFactory().createComponent(),
				new CoercionTransducerFactory<String>(String.class).createComponent() };

		final ComponentManager componentLinkManagerImpl = new ComponentManager();

		final Component compose = Components.compose(componentLinkManagerImpl, components);

		componentLinkManagerImpl.connect(compose, terminalComponent);

		terminalComponent.getDownStreamDataHandler().handleData(source);
		initialComponent.getUpStreamDataFlow().handleData(sourceFuture.get());

		assertEquals(source, terminalFuture.get());
	}

	@Test
	public void testCompose05() throws ComponentConnectionRejectedException, DataFlowException, InterruptedException, ExecutionException, ComponentNotConnectedException {
		final String source = new String("Hello, World!");
		final FutureResponse<byte[]> sourceFuture = new FutureResponse<byte[]>();
		final FutureResponse<String> terminalFuture = new FutureResponse<String>();

		final InitialComponent<byte[], byte[]> initialComponent = Components.initial(new DownStreamDataFlowAdapter<byte[]>() {
			@Override
			public void handleData(byte[] data) throws DataFlowException {
				super.handleData(data);
				sourceFuture.setValue(data);
			}
		});

		final TerminalComponent<String, String> terminalComponent = Components.terminal(new UpStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				terminalFuture.setValue(data);
			}
		});

		final Component[] components1 = { initialComponent, new PayLoadTransducerFactory().createComponent(), new SerializationTransducerFactory().createComponent() };

		final Component[] components2 = { new CoercionTransducerFactory<String>(String.class).createComponent(), terminalComponent };

		final ComponentManager componentLinkManagerImpl = new ComponentManager();

		final Component compose1 = Components.compose(componentLinkManagerImpl, components1);
		final Component compose2 = Components.compose(componentLinkManagerImpl, components2);

		componentLinkManagerImpl.connect(compose1, compose2);

		terminalComponent.getDownStreamDataHandler().handleData(source);
		initialComponent.getUpStreamDataFlow().handleData(sourceFuture.get());

		assertEquals(source, terminalFuture.get());
	}

	@Test
	public void testFailure01() throws ComponentConnectionRejectedException, ComponentNotConnectedException {
		final String source = new String("Hello, World!");

		final Component[] pipelines = { new PayLoadTransducerFactory().createComponent(), new CoercionTransducerFactory<String>(String.class).createComponent() };

		final ComponentManager componentLinkManagerImpl = new ComponentManager();
		final InitialComponent<byte[], byte[]> initialComponent = Components.initial(new DownStreamDataFlowAdapter<byte[]>());
		final Component composedComponent = Components.compose(componentLinkManagerImpl, pipelines);

		final TerminalComponent<String, String> terminalComponent = Components.terminal(new UpStreamDataFlowAdapter<String>());

		componentLinkManagerImpl.connect(initialComponent, composedComponent);
		componentLinkManagerImpl.connect(composedComponent, terminalComponent);

		try {
			terminalComponent.getDownStreamDataHandler().handleData(source);
			fail();
		} catch (DataFlowException e) {
			assertEquals(ClassCastException.class, e.getCause().getClass());
		}

	}
}
