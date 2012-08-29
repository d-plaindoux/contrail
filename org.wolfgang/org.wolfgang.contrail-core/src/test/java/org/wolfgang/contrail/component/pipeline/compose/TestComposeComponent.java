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

package org.wolfgang.contrail.component.pipeline.compose;

import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.common.concurrent.FutureResponse;
import org.wolfgang.contrail.component.Component;
import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentNotConnectedException;
import org.wolfgang.contrail.component.bound.DataReceiverAdapter;
import org.wolfgang.contrail.component.bound.InitialComponent;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.pipeline.transducer.coercion.CoercionTransducerFactory;
import org.wolfgang.contrail.component.pipeline.transducer.payload.PayLoadTransducerFactory;
import org.wolfgang.contrail.component.pipeline.transducer.serializer.SerializationTransducerFactory;
import org.wolfgang.contrail.handler.DataHandlerException;
import org.wolfgang.contrail.handler.DownStreamDataHandlerAdapter;
import org.wolfgang.contrail.link.ComponentLinkManagerImpl;

/**
 * <code>TestConcurrentComponent</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestComposeComponent extends TestCase {

	@SuppressWarnings("rawtypes")
	@Test
	public void testCompose01() throws ComponentConnectionRejectedException, DataHandlerException, InterruptedException, ExecutionException, ComponentNotConnectedException {
		final String source = new String("Hello, World!");
		final FutureResponse<byte[]> sourceFuture = new FutureResponse<byte[]>();
		final FutureResponse<String> terminalFuture = new FutureResponse<String>();

		final Component[] pipelines = { new PayLoadTransducerFactory().createComponent(), new SerializationTransducerFactory().createComponent(),
				new CoercionTransducerFactory<String>(String.class).createComponent() };

		final InitialComponent<byte[], byte[]> initialComponent = new InitialComponent<byte[], byte[]>(new DownStreamDataHandlerAdapter<byte[]>() {
			@Override
			public void handleData(byte[] data) throws DataHandlerException {
				super.handleData(data);
				sourceFuture.setValue(data);
			}
		});

		final ComponentLinkManagerImpl componentLinkManagerImpl = new ComponentLinkManagerImpl();
		final Component composedComponent = CompositionFactory.compose(componentLinkManagerImpl, pipelines);

		final TerminalComponent<String, String> terminalComponent = new TerminalComponent<String, String>(new DataReceiverAdapter<String>() {
			@Override
			public void receiveData(String data) throws DataHandlerException {
				terminalFuture.setValue(data);
			}
		});

		componentLinkManagerImpl.connect(initialComponent, composedComponent);
		componentLinkManagerImpl.connect(composedComponent, terminalComponent);

		terminalComponent.getDownStreamDataHandler().handleData(source);
		initialComponent.getUpStreamDataHandler().handleData(sourceFuture.get());

		assertEquals(source, terminalFuture.get());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testFailure01() throws ComponentConnectionRejectedException, ComponentNotConnectedException {
		final String source = new String("Hello, World!");

		final Component[] pipelines = { new PayLoadTransducerFactory().createComponent(), new CoercionTransducerFactory<String>(String.class).createComponent() };

		final ComponentLinkManagerImpl componentLinkManagerImpl = new ComponentLinkManagerImpl();
		final InitialComponent<byte[], byte[]> initialComponent = new InitialComponent<byte[], byte[]>(new DownStreamDataHandlerAdapter<byte[]>());
		final Component composedComponent = CompositionFactory.compose(componentLinkManagerImpl, pipelines);

		final TerminalComponent<String, String> terminalComponent = new TerminalComponent<String, String>(new DataReceiverAdapter<String>());

		componentLinkManagerImpl.connect(initialComponent, composedComponent);
		componentLinkManagerImpl.connect(composedComponent, terminalComponent);

		try {
			terminalComponent.getDownStreamDataHandler().handleData(source);
			fail();
		} catch (DataHandlerException e) {
			assertEquals(ClassCastException.class, e.getCause().getClass());
		}

	}
}
