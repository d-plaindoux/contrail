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

package org.wolfgang.contrail.component.reverse;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.wolfgang.common.concurrent.Promise;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.Components;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.pipeline.transducer.factory.CoercionTransducerFactory;
import org.wolfgang.contrail.component.pipeline.transducer.factory.PayLoadTransducerFactory;
import org.wolfgang.contrail.component.pipeline.transducer.factory.SerializationTransducerFactory;
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
public class TestReverseComponent {

	@Test
	public void testReverse01() throws ComponentConnectionRejectedException, DataFlowException, InterruptedException, ExecutionException, ComponentNotConnectedException {

		final String source = new String("Hello, World!");
		final Promise<String> pingFuture = Promise.create();
		final Promise<byte[]> pongFuture = Promise.create();

		final InitialComponent<String, String> pingComponent = Components.initial(new DownStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				pingFuture.success(data);
			}
		});

		final TerminalComponent<byte[], byte[]> pongComponent = Components.terminal(new UpStreamDataFlowAdapter<byte[]>() {
			@Override
			public void handleData(byte[] data) throws DataFlowException {
				super.handleData(data);
				pongFuture.success(data);
			}
		});

		final Component[] pipelines = { new PayLoadTransducerFactory().createComponent(), new SerializationTransducerFactory().createComponent(),
				new CoercionTransducerFactory<String>(String.class).createComponent() };
		final Component reversedComponent = Components.reverse(Components.compose(pipelines));

		ComponentManager.connect(pingComponent, reversedComponent);
		ComponentManager.connect(reversedComponent, pongComponent);

		pingComponent.getUpStreamDataFlow().handleData(source);
		pongComponent.getDownStreamDataHandler().handleData(pongFuture.getFuture().get());

		assertEquals(source, pingFuture.getFuture().get());

	}

	@Test
	public void testReverse02() throws ComponentConnectionRejectedException, DataFlowException, InterruptedException, ExecutionException, ComponentNotConnectedException {

		final String source = new String("Hello, World!");
		final Promise<String> pingFuture = Promise.create();
		final Promise<byte[]> pongFuture = Promise.create();

		final InitialComponent<String, String> pingComponent = Components.initial(new DownStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				pingFuture.success(data);
			}
		});

		final InitialComponent<byte[], byte[]> pongComponent = Components.initial(new DownStreamDataFlowAdapter<byte[]>() {
			@Override
			public void handleData(byte[] data) throws DataFlowException {
				super.handleData(data);
				pongFuture.success(data);
			}
		});

		final Component[] pipelines = { pongComponent, new PayLoadTransducerFactory().createComponent(), new SerializationTransducerFactory().createComponent(),
				new CoercionTransducerFactory<String>(String.class).createComponent() };
		final Component reversedComponent = Components.reverse(Components.compose(pipelines));

		ComponentManager.connect(pingComponent, reversedComponent);

		pingComponent.getUpStreamDataFlow().handleData(source);
		pongComponent.getUpStreamDataFlow().handleData(pongFuture.getFuture().get());

		assertEquals(source, pingFuture.getFuture().get());

	}

	@Test
	public void testReverse03() throws ComponentConnectionRejectedException, DataFlowException, InterruptedException, ExecutionException, ComponentNotConnectedException {

		final String source = new String("Hello, World!");
		final Promise<String> pingFuture = Promise.create();
		final Promise<byte[]> pongFuture = Promise.create();

		final TerminalComponent<String, String> pingComponent = Components.terminal(new UpStreamDataFlowAdapter<String>() {
			@Override
			public void handleData(String data) throws DataFlowException {
				pingFuture.success(data);
			}
		});

		final TerminalComponent<byte[], byte[]> pongComponent = Components.terminal(new UpStreamDataFlowAdapter<byte[]>() {
			@Override
			public void handleData(byte[] data) throws DataFlowException {
				super.handleData(data);
				pongFuture.success(data);
			}
		});

		final Component[] pipelines = { new PayLoadTransducerFactory().createComponent(), new SerializationTransducerFactory().createComponent(),
				new CoercionTransducerFactory<String>(String.class).createComponent(), pingComponent };
		final Component reversedComponent = Components.reverse(Components.compose(pipelines));

		ComponentManager.connect(reversedComponent, pongComponent);

		pingComponent.getDownStreamDataHandler().handleData(source);
		pongComponent.getDownStreamDataHandler().handleData(pongFuture.getFuture().get());

		assertEquals(source, pingFuture.getFuture().get());

	}

}
