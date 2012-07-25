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

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

/**
 * <code>TestModel</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestModelChecker extends TestCase {

	public void testNominal01() throws JAXBException {
		final EcosystemModel ecosystem = new EcosystemModel();

		final TerminalModel terminal = new TerminalModel();
		terminal.setName("A");
		ecosystem.add(terminal);

		final BinderModel decoded = new BinderModel();
		decoded.setFlow("A");
		ecosystem.add(decoded);

		final List<String> freeVariables = EcosystemCheckerUtils.getUnknownReferences(ecosystem);

		assertEquals(0, freeVariables.size());
	}

	public void testNominal02() throws JAXBException {
		final EcosystemModel ecosystem = new EcosystemModel();

		for (String s : new String[] { "A", "B" }) {
			final TerminalModel terminal = new TerminalModel();
			terminal.setName(s);
			ecosystem.add(terminal);
		}

		final BinderModel decoded = new BinderModel();
		decoded.setFlow("A B");
		ecosystem.add(decoded);

		final List<String> freeVariables = EcosystemCheckerUtils.getUnknownReferences(ecosystem);

		assertEquals(0, freeVariables.size());
	}

	public void testNominal03() throws JAXBException {
		final EcosystemModel ecosystem = new EcosystemModel();

		for (String s : new String[] { "A", "B" }) {
			final TerminalModel terminal = new TerminalModel();
			terminal.setName(s);
			ecosystem.add(terminal);
		}

		for (String s : new String[] { "C", "D" }) {
			final PipelineModel pipeline = new PipelineModel();
			pipeline.setName(s);
			ecosystem.add(pipeline);
		}

		final BinderModel decoded = new BinderModel();
		decoded.setFlow("A C B D");
		ecosystem.add(decoded);

		final List<String> freeVariables = EcosystemCheckerUtils.getUnknownReferences(ecosystem);

		assertEquals(0, freeVariables.size());
	}

	public void testNominal04() throws JAXBException {
		final EcosystemModel ecosystem = new EcosystemModel();

		for (String s : new String[] { "A" }) {
			final TerminalModel terminal = new TerminalModel();
			terminal.setName(s);
			ecosystem.add(terminal);
		}

		for (String s : new String[] { "C" }) {
			final PipelineModel pipeline = new PipelineModel();
			pipeline.setName(s);
			ecosystem.add(pipeline);
		}

		final BinderModel decoded = new BinderModel();
		decoded.setFlow("A C B D");
		ecosystem.add(decoded);

		final List<String> freeVariables = EcosystemCheckerUtils.getUnknownReferences(ecosystem);

		assertEquals(2, freeVariables.size());
		assertEquals(Arrays.asList("B", "D"), freeVariables);
	}

	public void testNominal05() throws JAXBException {
		final EcosystemModel ecosystem = new EcosystemModel();

		for (String s : new String[] { "A" }) {
			final TerminalModel terminal = new TerminalModel();
			terminal.setName(s);
			ecosystem.add(terminal);
		}

		for (String s : new String[] { "C" }) {
			final PipelineModel pipeline = new PipelineModel();
			pipeline.setName(s);
			ecosystem.add(pipeline);
		}

		final BinderModel decoded = new BinderModel();
		decoded.setFlow("A B D");
		ecosystem.add(decoded);

		final List<String> unusedVariables = EcosystemCheckerUtils.getUnusedReferences(ecosystem);

		assertEquals(1, unusedVariables.size());
		assertEquals(Arrays.asList("C"), unusedVariables);
	}

	public void testNominal06() throws JAXBException {
		final EcosystemModel ecosystem = new EcosystemModel();

		for (String s : new String[] { "A" }) {
			final TerminalModel terminal = new TerminalModel();
			terminal.setName(s);
			ecosystem.add(terminal);
		}

		for (String s : new String[] { "C" }) {
			final PipelineModel pipeline = new PipelineModel();
			pipeline.setName(s);
			ecosystem.add(pipeline);
		}

		final BinderModel decoded = new BinderModel();
		decoded.setFlow("B D");
		ecosystem.add(decoded);

		final List<String> unusedVariables = EcosystemCheckerUtils.getUnusedReferences(ecosystem);

		assertEquals(2, unusedVariables.size());
		// Order is important in this test | TODO remove this constraint ASAP
		assertEquals(Arrays.asList("C", "A"), unusedVariables);
	}

	public void testNominal07() throws JAXBException {
		final EcosystemModel ecosystem = new EcosystemModel();

		for (String s : new String[] { "A", "B" }) {
			final TerminalModel terminal = new TerminalModel();
			terminal.setName(s);
			ecosystem.add(terminal);
		}

		for (String s : new String[] { "C", "D" }) {
			final PipelineModel pipeline = new PipelineModel();
			pipeline.setName(s);
			ecosystem.add(pipeline);
		}

		final RouterModel router = new RouterModel();
		final ClientModel client = new ClientModel();
		client.setFlow("A B C D");

		router.add(client);

		final List<String> freeVariables = EcosystemCheckerUtils.getUnknownReferences(ecosystem);

		assertEquals(0, freeVariables.size());
	}

	public void testNominal08() throws JAXBException {
		final EcosystemModel ecosystem = new EcosystemModel();

		for (String s : new String[] { "A", "B" }) {
			final TerminalModel terminal = new TerminalModel();
			terminal.setName(s);
			ecosystem.add(terminal);
		}

		for (String s : new String[] { "C", "D" }) {
			final PipelineModel pipeline = new PipelineModel();
			pipeline.setName(s);
			ecosystem.add(pipeline);
		}

		final RouterModel router = new RouterModel();
		for (String flow : new String[] { "A C", "B D" }) {
			final ClientModel client = new ClientModel();
			client.setFlow(flow);
			router.add(client);
		}

		ecosystem.add(router);

		final List<String> freeVariables = EcosystemCheckerUtils.getUnknownReferences(ecosystem);

		assertEquals(0, freeVariables.size());
	}

	public void testNominal09() throws JAXBException {
		final EcosystemModel ecosystem = new EcosystemModel();

		for (String s : new String[] { "A", "B" }) {
			final TerminalModel terminal = new TerminalModel();
			terminal.setName(s);
			ecosystem.add(terminal);
		}

		for (String s : new String[] { "C", "D" }) {
			final PipelineModel pipeline = new PipelineModel();
			pipeline.setName(s);
			ecosystem.add(pipeline);
		}

		final RouterModel router = new RouterModel();
		for (String flow : new String[] { "b A c=C", "b=B D c" }) {
			final ClientModel client = new ClientModel();
			client.setFlow(flow);
			router.add(client);
		}

		ecosystem.add(router);

		final List<String> freeVariables = EcosystemCheckerUtils.getUnknownReferences(ecosystem);

		assertEquals(0, freeVariables.size());
	}

	public void testNominal10() throws JAXBException {
		final EcosystemModel ecosystem = new EcosystemModel();

		for (String s : new String[] { "A", "B" }) {
			final TerminalModel terminal = new TerminalModel();
			terminal.setName(s);
			ecosystem.add(terminal);
		}

		for (String s : new String[] { "C", "D" }) {
			final PipelineModel pipeline = new PipelineModel();
			pipeline.setName(s);
			ecosystem.add(pipeline);
		}

		final RouterModel router = new RouterModel();
		for (String flow : new String[] { "b A c=C", "B D c" }) {
			final ClientModel client = new ClientModel();
			client.setFlow(flow);
			router.add(client);
		}

		ecosystem.add(router);

		final List<String> freeVariables = EcosystemCheckerUtils.getUnknownReferences(ecosystem);

		assertEquals(1, freeVariables.size());
		assertEquals(Arrays.asList("b"), freeVariables);
	}
}
