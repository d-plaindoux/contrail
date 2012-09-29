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

package org.wolfgang.contrail.network.ecosystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.junit.Test;
import org.wolfgang.common.concurrent.FutureResponse;
import org.wolfgang.contrail.component.ComponentFactory;
import org.wolfgang.contrail.component.bound.TerminalComponent;
import org.wolfgang.contrail.component.factory.Components;
import org.wolfgang.contrail.dsl.ESLLanguage;
import org.wolfgang.contrail.dsl.SourceUnit;
import org.wolfgang.contrail.ecosystem.Ecosystem;
import org.wolfgang.contrail.ecosystem.key.EcosystemKeyFactory;
import org.wolfgang.contrail.ecosystem.lang.EcosystemFactoryImpl;
import org.wolfgang.contrail.ecosystem.lang.model.EcosystemModel;
import org.wolfgang.contrail.flow.DataFlowException;
import org.wolfgang.contrail.flow.UpStreamDataFlowAdapter;
import org.wolfgang.opala.lexing.exception.LexemeNotFoundException;
import org.wolfgang.opala.parsing.exception.ParsingException;
import org.wolfgang.opala.parsing.exception.ParsingUnitNotFound;
import org.wolfgang.opala.scanner.Scanner;
import org.wolfgang.opala.scanner.ScannerFactory;
import org.wolfgang.opala.scanner.exception.ScannerException;

/**
 * <code>TestNetworkEcosystem</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestNetworkEcosystemDSL {

	private EcosystemModel buildModel(String source) throws ScannerException, IOException, ParsingUnitNotFound, LexemeNotFoundException, ParsingException {
		final ESLLanguage celLanguage = new ESLLanguage();
		final Scanner scanner = ScannerFactory.create(TestNetworkEcosystemDSL.class.getClassLoader().getResource(source).openStream());
		final EcosystemModel ecosystemModel = new EcosystemModel();
		celLanguage.parse(SourceUnit.class, scanner, ecosystemModel);
		return ecosystemModel;
	}

	@Test
	public void testClientServer_0() throws Exception {
		final Ecosystem ecosystem01 = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), buildModel("sample01_0.esl"));
		final Ecosystem ecosystem02 = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), buildModel("sample02_0.esl"));

		try {
			final FutureResponse<String> response = new FutureResponse<String>();
			final TerminalComponent<byte[], byte[]> sender = Components.terminal(new UpStreamDataFlowAdapter<byte[]>() {
				@Override
				public void handleData(byte[] data) throws DataFlowException {
					response.setValue(new String(data));
				}
			});

			final ComponentFactory factory = ecosystem02.getFactory(EcosystemKeyFactory.named("Main"));
			factory.create("tcp://localhost:6666", sender);

			final String message = "Hello, World!";
			sender.getDownStreamDataHandler().handleData(message.getBytes());
			assertEquals(message, response.get(5, TimeUnit.SECONDS));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			ecosystem01.close();
			ecosystem02.close();
		}
	}

	@Test
	public void testClientServer_1() throws Exception {

		final Ecosystem ecosystem01 = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), buildModel("sample01_1.esl"));
		final Ecosystem ecosystem02 = EcosystemFactoryImpl.build(Logger.getAnonymousLogger(), buildModel("sample02_1.esl"));

		try {
			final FutureResponse<String> response = new FutureResponse<String>();
			final TerminalComponent<String, String> sender = Components.terminal(new UpStreamDataFlowAdapter<String>() {
				@Override
				public void handleData(String data) throws DataFlowException {
					response.setValue(data);
				}
			});

			final ComponentFactory factory = ecosystem02.getFactory(EcosystemKeyFactory.named("Main"));
			factory.create("tcp://localhost:6666", sender);

			final String message = "Hello, World!";
			sender.getDownStreamDataHandler().handleData(message);
			assertEquals(message, response.get(5, TimeUnit.SECONDS));

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			ecosystem01.close();
			ecosystem02.close();
		}
	}
}
