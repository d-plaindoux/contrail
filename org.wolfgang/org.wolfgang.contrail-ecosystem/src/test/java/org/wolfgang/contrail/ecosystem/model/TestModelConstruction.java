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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import junit.framework.TestCase;

/**
 * <code>TestModel</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestModelConstruction extends TestCase {

	@SuppressWarnings("unchecked")
	private <T> T decode(Class<T> type, String content) throws JAXBException {
		final InputStream stream = new ByteArrayInputStream(content.getBytes());
		try {
			final JAXBContext context = JAXBContext.newInstance(type);
			final Unmarshaller unmarshaller = context.createUnmarshaller();
			return (T) unmarshaller.unmarshal(stream);
		} finally {
			try {
				stream.close();
			} catch (IOException consume) {
				// Ignore
			}
		}
	}

	// ------------------------------

	public void testEntry() throws JAXBException, ValidationException {
		final String content = "<binder name='A.A' typein='String' typeout='String'> Logger </binder>";
		final BinderModel decoded = decode(BinderModel.class, content);
		decoded.validate();

		assertEquals("A.A", decoded.getName());
		assertEquals("Logger", decoded.getFlow());
	}

	// ------------------------------

	public void testClient() throws JAXBException, ValidationException {
		final String content = "<client factory='a' name='A.B' filter='A.*' endpoint='tcp://localhost:6666'> " + "PayLoad Serialize Coercion NetworkRoute " + "</client>";
		final ClientModel decoded = decode(ClientModel.class, content);
		decoded.validate();

		assertEquals("a", decoded.getFactory());
		assertEquals("A.B", decoded.getName());
		assertEquals("A.*", decoded.getFilter());
		assertEquals("tcp://localhost:6666", decoded.getEndpoint());
		assertEquals("PayLoad Serialize Coercion NetworkRoute", decoded.getFlow());
	}

	// ------------------------------

	public void testRouter01() throws JAXBException, ValidationException {
		final String content = "<router name='NetworkRoute'	factory='org.wolfgang.contrail.network.component.NetworkFactory' singleton='yes'>" + "<self name='A.A'/>"
				+ "</router>";
		final RouterModel decoded = decode(RouterModel.class, content);
		decoded.validate();

		assertEquals("NetworkRoute", decoded.getName());
		assertEquals("org.wolfgang.contrail.network.component.NetworkFactory", decoded.getFactory());
		assertEquals(0, decoded.getClients().size());
	}

	public void testRouter02() throws JAXBException, ValidationException {
		final String content = "<router name='NetworkRoute'	factory='org.wolfgang.contrail.network.component.NetworkFactory' singleton='yes'>" + "<self name='A.A'/>"
				+ "<client factory='a' name='A.B' filter='A.*' endpoint='tcp://localhost:6666'>PayLoad Serialize Coercion NetworkRoute</client>" + "</router>";
		final RouterModel decoded = decode(RouterModel.class, content);
		decoded.validate();

		assertEquals("NetworkRoute", decoded.getName());
		assertEquals("org.wolfgang.contrail.network.component.NetworkFactory", decoded.getFactory());
		assertEquals(1, decoded.getClients().size());

		final ClientModel client = decoded.getClients().get(0);
		assertEquals("A.B", client.getName());
		assertEquals("a", client.getFactory());
		assertEquals("A.*", client.getFilter());
		assertEquals("tcp://localhost:6666", client.getEndpoint());
		assertEquals("PayLoad Serialize Coercion NetworkRoute", client.getFlow());
	}

	public void testRouter03() throws JAXBException {
		final String content = "<router name='NetworkRoute'	factory='org.wolfgang.contrail.network.component.NetworkFactory' singleton='yes'>" + "<self name='A.A'/>"
				+ "<client factory='a' name='A.B' filter='A.*' endpoint='tcp://localhost:6666'>PayLoad Serialize Coercion NetworkRoute</client>"
				+ "<client factory='a' name='A.C' filter='A.*' endpoint='ws://localhost:6668'>JSon Coercion NetworkRoute</client>" + "</router>";
		final RouterModel decoded = decode(RouterModel.class, content);
		assertEquals("NetworkRoute", decoded.getName());
		assertEquals("org.wolfgang.contrail.network.component.NetworkFactory", decoded.getFactory());
		assertEquals(2, decoded.getClients().size());

		final ClientModel client1 = decoded.getClients().get(0);
		assertEquals("A.B", client1.getName());
		assertEquals("A.*", client1.getFilter());
		assertEquals("tcp://localhost:6666", client1.getEndpoint());
		assertEquals("PayLoad Serialize Coercion NetworkRoute", client1.getFlow());

		final ClientModel client2 = decoded.getClients().get(1);
		assertEquals("A.C", client2.getName());
		assertEquals("A.*", client2.getFilter());
		assertEquals("ws://localhost:6668", client2.getEndpoint());
		assertEquals("JSon Coercion NetworkRoute", client2.getFlow());
	}

	// ------------------------------

	public void testServer() throws JAXBException {
		final String content = "<server endpoint='tcp://localhost:6667'> PayLoad Serialize Coercion NetworkRoute </server>";
		final ServerModel decoded = decode(ServerModel.class, content);
		assertEquals("tcp://localhost:6667", decoded.getEndpoint());
		assertEquals("PayLoad Serialize Coercion NetworkRoute", decoded.getFlow());
	}

	// ------------------------------

	public void testTransducer01() throws JAXBException {
		final String content = "<pipeline name='Coercion' factory='org.wolfgang.contrail.codec.coercion.CoercionTransducerFactory'/>";
		final PipelineModel decoded = decode(PipelineModel.class, content);
		assertEquals("Coercion", decoded.getName());
		assertEquals("org.wolfgang.contrail.codec.coercion.CoercionTransducerFactory", decoded.getFactory());
		assertEquals(0, decoded.getParameters().size());
	}

	public void testTransducer02() throws JAXBException, ValidationException {
		final String content = "<pipeline name='Coercion' factory='org.wolfgang.contrail.codec.coercion.CoercionTransducerFactory'>" + "<param>NetEvent</param> " + "</pipeline>";
		final PipelineModel decoded = decode(PipelineModel.class, content);
		decoded.validate();

		assertEquals("Coercion", decoded.getName());
		assertEquals("org.wolfgang.contrail.codec.coercion.CoercionTransducerFactory", decoded.getFactory());
		assertEquals(Arrays.asList("NetEvent"), decoded.getParameters());
	}

	public void testTransducer03() throws JAXBException, ValidationException {
		final String content = "<pipeline name='Coercion' factory='org.wolfgang.contrail.codec.coercion.CoercionTransducerFactory'> " + "<param>NetEvent1</param> " + "<param>NetEvent2</param> "
				+ "</pipeline>";
		final PipelineModel decoded = decode(PipelineModel.class, content);
		decoded.validate();

		assertEquals("Coercion", decoded.getName());
		assertEquals("org.wolfgang.contrail.codec.coercion.CoercionTransducerFactory", decoded.getFactory());
		assertEquals(Arrays.asList("NetEvent1", "NetEvent2"), decoded.getParameters());
	}

	// ------------------------------

	public void testTerminal01() throws JAXBException, ValidationException {
		final String content = "<terminal name='Logger' factory='org.wolfgang.contrail.bound.Logger'> </terminal>";
		final TerminalModel decoded = decode(TerminalModel.class, content);
		decoded.validate();

		assertEquals("Logger", decoded.getName());
		assertEquals("org.wolfgang.contrail.bound.Logger", decoded.getFactory());
		assertEquals(0, decoded.getParameters().size());
	}

	public void testTerminal02() throws JAXBException, ValidationException {
		final String content = "<terminal name='Logger' factory='org.wolfgang.contrail.bound.Logger'> " + "<param>NetEvent</param> " + "</terminal>";
		final TerminalModel decoded = decode(TerminalModel.class, content);
		decoded.validate();

		assertEquals("Logger", decoded.getName());
		assertEquals("org.wolfgang.contrail.bound.Logger", decoded.getFactory());
		assertEquals(Arrays.asList("NetEvent"), decoded.getParameters());
	}

	public void testTerminal03() throws JAXBException, ValidationException {
		final String content = "<terminal name='Logger' factory='org.wolfgang.contrail.bound.Logger'> " + "<param>NetEvent1</param> " + "<param>NetEvent2</param> " + "</terminal>";
		final TerminalModel decoded = decode(TerminalModel.class, content);
		decoded.validate();

		assertEquals("Logger", decoded.getName());
		assertEquals("org.wolfgang.contrail.bound.Logger", decoded.getFactory());
		assertEquals(Arrays.asList("NetEvent1", "NetEvent2"), decoded.getParameters());
	}

	// ------------------------------

	public void testEcosystem() throws JAXBException, ValidationException {
		final String content = "<ecosystem><pipeline name='PayLoad' factory='org.wolfgang.contrail.codec.payload.PayLoadTransducerFactory' />"
				+ "<pipeline name='Serialize' factory='org.wolfgang.contrail.codec.serializer.SerializerTransducerFactory'/>"
				+ "<pipeline name='Coercion' factory='org.wolfgang.contrail.codec.coercion.CoercionTransducerFactory'><param>NetEvent</param></pipeline>"
				+ "<terminal name='Logger' factory='org.wolfgang.contrail.bound.LoggerComponent'/>"
				+ "<router name='NetworkRoute' factory='org.wolfgang.contrail.network.component.NetworkFactory' singleton='yes'>" + "<self name='A.A'/>"
				+ "<client factory='a' name='A.B' filter='A.*' endpoint='tcp://localhost:6666'>" + "PayLoad Serialize Coercion NetworkRoute" + "</client>"
				+ "<client factory='a' name='A.C' filter='A.*' endpoint='ws://localhost:6668'>" + "JSon Coercion NetworkRoute </client></router>"
				+ "<binder name='NetHook' typein='String' typeout='String'>PayLoad Serialize Coercion NetworkRoute</binder>" 
				+ "<binder name='WebHook' typein='String' typeout='String'>JSon Coercion NetworkRoute</binder>"
				+ "<server factory='a' endpoint='tcp://localhost:6667'>PayLoad Serialize Coercion NetworkRoute</server>"
				+ "<server factory='a' endpoint='ws://localhost:6667'>JSon Coercion NetworkRoute</server>" 
				+ "<flow name='bb'>Network Logger</flow>"
				+ "<flow name='aa'>Network Logger</flow>"
				+ "<main>Network Logger</main>"
				+ "</ecosystem>";
		final EcosystemModel decoded = decode(EcosystemModel.class, content);
		decoded.validate();
		
		assertEquals("Network Logger", decoded.getMain());
		assertEquals(2, decoded.getBinders().size());
		assertEquals(3, decoded.getPipelines().size());
		assertEquals(1, decoded.getTerminals().size());
		assertEquals(1, decoded.getRouters().size());
		assertEquals(2, decoded.getServers().size());
	}
}
