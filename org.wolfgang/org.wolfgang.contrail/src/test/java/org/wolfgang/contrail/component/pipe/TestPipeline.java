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

package org.wolfgang.contrail.component.pipe;

import java.util.concurrent.atomic.AtomicReference;

import org.wolfgang.contrail.component.ComponentAlreadyConnectedException;
import org.wolfgang.contrail.component.ComponentNotYetConnectedException;
import org.wolfgang.contrail.component.core.DataReceiver;
import org.wolfgang.contrail.component.core.InitialDataReceiverFactory;
import org.wolfgang.contrail.component.core.InitialUpStreamSourceComponent;
import org.wolfgang.contrail.component.core.TerminalDataReceiverFactory;
import org.wolfgang.contrail.component.core.TerminalUpStreamDestinationComponent;
import org.wolfgang.contrail.component.core.TransformationBasedPipeLineComponent;
import org.wolfgang.contrail.connector.ComponentConnectionImpl;
import org.wolfgang.contrail.handler.HandleDataException;

import junit.framework.TestCase;

/**
 * <code>TestPipeline</code> is dedicated to transformation based pipeline test
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestPipeline extends TestCase {

	public void testNominal01() throws ComponentAlreadyConnectedException, ComponentNotYetConnectedException, HandleDataException {
		final TransformationBasedPipeLineComponent<String, Integer> pipeline = new TransformationBasedPipeLineComponent<String, Integer>(
				new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialUpStreamSourceComponent<String> initial = new InitialUpStreamSourceComponent<String>(
				new InitialDataReceiverFactory<String>() {
					@Override
					public DataReceiver<String> create(InitialUpStreamSourceComponent<String> initial) {
						return new DataReceiver<String>() {
							@Override
							public void receiveData(String data) throws HandleDataException {
								stringReference.set(data);
							}
						};
					}
				});

		final TerminalUpStreamDestinationComponent<Integer> terminal = new TerminalUpStreamDestinationComponent<Integer>(
				new TerminalDataReceiverFactory<Integer>() {
					@Override
					public DataReceiver<Integer> create(final TerminalUpStreamDestinationComponent<Integer> terminal) {
						return new DataReceiver<Integer>() {
							@Override
							public void receiveData(Integer data) throws HandleDataException {
								terminal.getDataSender().sendData(data * 10);
							}
						};
					}
				});

		final ComponentConnectionImpl<String> initialConnection = new ComponentConnectionImpl<String>(initial, pipeline);
		final ComponentConnectionImpl<Integer> terminalConnection = new ComponentConnectionImpl<Integer>(pipeline, terminal);

		initial.getDataSender().sendData("3");
		assertEquals("30", stringReference.get());

		initialConnection.dispose();
		terminalConnection.dispose();
	}

	public void testNominal02() throws ComponentAlreadyConnectedException, ComponentNotYetConnectedException, HandleDataException {
		final TransformationBasedPipeLineComponent<String, Integer> pipeline = new TransformationBasedPipeLineComponent<String, Integer>(
				new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialUpStreamSourceComponent<String> initial = new InitialUpStreamSourceComponent<String>(
				new InitialDataReceiverFactory<String>() {
					@Override
					public DataReceiver<String> create(InitialUpStreamSourceComponent<String> initial) {
						return new DataReceiver<String>() {
							@Override
							public void receiveData(String data) throws HandleDataException {
								stringReference.set(data);
							}
						};
					}
				});

		final TerminalUpStreamDestinationComponent<Integer> terminal = new TerminalUpStreamDestinationComponent<Integer>(
				new TerminalDataReceiverFactory<Integer>() {
					@Override
					public DataReceiver<Integer> create(final TerminalUpStreamDestinationComponent<Integer> terminal) {
						return new DataReceiver<Integer>() {
							@Override
							public void receiveData(Integer data) throws HandleDataException {
								terminal.getDataSender().sendData(data * 10);
							}
						};
					}
				});

		final ComponentConnectionImpl<String> initialConnection = new ComponentConnectionImpl<String>(initial, pipeline);
		final ComponentConnectionImpl<Integer> terminalConnection = new ComponentConnectionImpl<Integer>(pipeline, terminal);

		initial.getDataSender().sendData("0");
		assertEquals("0", stringReference.get());

		initialConnection.dispose();
		terminalConnection.dispose();
	}

	public void testFailure() throws ComponentAlreadyConnectedException, ComponentNotYetConnectedException, HandleDataException {
		final TransformationBasedPipeLineComponent<String, Integer> pipeline = new TransformationBasedPipeLineComponent<String, Integer>(
				new StringToInteger(), new IntegerToString());

		final AtomicReference<String> stringReference = new AtomicReference<String>();
		final InitialUpStreamSourceComponent<String> initial = new InitialUpStreamSourceComponent<String>(
				new InitialDataReceiverFactory<String>() {
					@Override
					public DataReceiver<String> create(InitialUpStreamSourceComponent<String> initial) {
						return new DataReceiver<String>() {
							@Override
							public void receiveData(String data) throws HandleDataException {
								stringReference.set(data);
							}
						};
					}
				});

		final TerminalUpStreamDestinationComponent<Integer> terminal = new TerminalUpStreamDestinationComponent<Integer>(
				new TerminalDataReceiverFactory<Integer>() {
					@Override
					public DataReceiver<Integer> create(final TerminalUpStreamDestinationComponent<Integer> terminal) {
						return new DataReceiver<Integer>() {
							@Override
							public void receiveData(Integer data) throws HandleDataException {
								terminal.getDataSender().sendData(data * 10);
							}
						};
					}
				});

		final ComponentConnectionImpl<String> initialConnection = new ComponentConnectionImpl<String>(initial, pipeline);
		final ComponentConnectionImpl<Integer> terminalConnection = new ComponentConnectionImpl<Integer>(pipeline, terminal);

		try {
			initial.getDataSender().sendData("NaN");
			fail();
		} catch (HandleDataException h) {
			assertTrue(h.getCause().getCause() instanceof NumberFormatException);
		}

		initialConnection.dispose();
		terminalConnection.dispose();
	}

}
