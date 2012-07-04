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
public class TestModel extends TestCase {

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

	public void testEntry() throws JAXBException {
		final String content = "<entry name='A.A'> <flow>Logger</flow> </entry>";
		final Entry decoded = decode(Entry.class, content);
		assertEquals("A.A", decoded.getName());
		assertEquals("Logger", decoded.getFlow());
	}

	// ------------------------------

	public void testClient() throws JAXBException {
		final String content = "<client name='A.B' filter='A.*' endpoint='tcp://localhost:6666'> " + "<flow>PayLoad Serialize Coercion NetworkRoute</flow> " + "</client>";
		final Client decoded = decode(Client.class, content);
		assertEquals("A.B", decoded.getName());
		assertEquals("A.*", decoded.getFilter());
		assertEquals("tcp://localhost:6666", decoded.getEndpoint());
		assertEquals("PayLoad Serialize Coercion NetworkRoute", decoded.getFlow());
	}

	// ------------------------------

	public void testRouter01() throws JAXBException {
		final String content = "<router name='NetworkRoute'	factory='org.wolfgang.contrail.network.component.NetworkFactory' singleton='yes'>" + "<self name='A.A'/>"
				+ "</router>";
		final Router decoded = decode(Router.class, content);
		assertEquals("NetworkRoute", decoded.getName());
		assertEquals("org.wolfgang.contrail.network.component.NetworkFactory", decoded.getFactory());
		assertEquals(0, decoded.getClients().size());
	}

	public void testRouter02() throws JAXBException {
		final String content = "<router name='NetworkRoute'	factory='org.wolfgang.contrail.network.component.NetworkFactory' singleton='yes'>" + "<self name='A.A'/>"
				+ "<client name='A.B' filter='A.*' endpoint='tcp://localhost:6666'><flow>PayLoad Serialize Coercion NetworkRoute</flow></client>" + "</router>";
		final Router decoded = decode(Router.class, content);
		assertEquals("NetworkRoute", decoded.getName());
		assertEquals("org.wolfgang.contrail.network.component.NetworkFactory", decoded.getFactory());
		assertEquals(1, decoded.getClients().size());

		final Client client = decoded.getClients().get(0);
		assertEquals("A.B", client.getName());
		assertEquals("A.*", client.getFilter());
		assertEquals("tcp://localhost:6666", client.getEndpoint());
		assertEquals("PayLoad Serialize Coercion NetworkRoute", client.getFlow());
	}

	public void testRouter03() throws JAXBException {
		final String content = "<router name='NetworkRoute'	factory='org.wolfgang.contrail.network.component.NetworkFactory' singleton='yes'>" + "<self name='A.A'/>"
				+ "<client name='A.B' filter='A.*' endpoint='tcp://localhost:6666'><flow>PayLoad Serialize Coercion NetworkRoute</flow></client>"
				+ "<client name='A.C' filter='A.*' endpoint='ws://localhost:6668'><flow>JSon Coercion NetworkRoute</flow></client>" + "</router>";
		final Router decoded = decode(Router.class, content);
		assertEquals("NetworkRoute", decoded.getName());
		assertEquals("org.wolfgang.contrail.network.component.NetworkFactory", decoded.getFactory());
		assertEquals(2, decoded.getClients().size());

		final Client client1 = decoded.getClients().get(0);
		assertEquals("A.B", client1.getName());
		assertEquals("A.*", client1.getFilter());
		assertEquals("tcp://localhost:6666", client1.getEndpoint());
		assertEquals("PayLoad Serialize Coercion NetworkRoute", client1.getFlow());

		final Client client2 = decoded.getClients().get(1);
		assertEquals("A.C", client2.getName());
		assertEquals("A.*", client2.getFilter());
		assertEquals("ws://localhost:6668", client2.getEndpoint());
		assertEquals("JSon Coercion NetworkRoute", client2.getFlow());
	}

	// ------------------------------

	public void testServer() throws JAXBException {
		final String content = "<server endpoint='tcp://localhost:6667'> <flow>PayLoad Serialize Coercion NetworkRoute</flow></server>";
		final Server decoded = decode(Server.class, content);
		assertEquals("tcp://localhost:6667", decoded.getEndpoint());
		assertEquals("PayLoad Serialize Coercion NetworkRoute", decoded.getFlow());
	}

	// ------------------------------

	public void testTransducer01() throws JAXBException {
		final String content = "<transducer name='Coercion' factory='org.wolfgang.contrail.codec.coercion.CoercionTransducerFactory'/>";
		final Transducer decoded = decode(Transducer.class, content);
		assertEquals("Coercion", decoded.getName());
		assertEquals("org.wolfgang.contrail.codec.coercion.CoercionTransducerFactory", decoded.getFactory());
		assertEquals(0, decoded.getParameters().size());
	}

	public void testTransducer02() throws JAXBException {
		final String content = "<transducer name='Coercion' factory='org.wolfgang.contrail.codec.coercion.CoercionTransducerFactory'>" + "<param>NetEvent</param> " + "</transducer>";
		final Transducer decoded = decode(Transducer.class, content);
		assertEquals("Coercion", decoded.getName());
		assertEquals("org.wolfgang.contrail.codec.coercion.CoercionTransducerFactory", decoded.getFactory());
		assertEquals(Arrays.asList("NetEvent"), decoded.getParameters());
	}

	public void testTransducer03() throws JAXBException {
		final String content = "<transducer name='Coercion' factory='org.wolfgang.contrail.codec.coercion.CoercionTransducerFactory'> " + "<param>NetEvent1</param> " + "<param>NetEvent2</param> "
				+ "</transducer>";
		final Transducer decoded = decode(Transducer.class, content);
		assertEquals("Coercion", decoded.getName());
		assertEquals("org.wolfgang.contrail.codec.coercion.CoercionTransducerFactory", decoded.getFactory());
		assertEquals(Arrays.asList("NetEvent1", "NetEvent2"), decoded.getParameters());
	}

	// ------------------------------

	public void testTerminal01() throws JAXBException {
		final String content = "<terminal name='Logger' factory='org.wolfgang.contrail.bound.Logger'> </terminal>";
		final Terminal decoded = decode(Terminal.class, content);
		assertEquals("Logger", decoded.getName());
		assertEquals("org.wolfgang.contrail.bound.Logger", decoded.getFactory());
		assertEquals(0, decoded.getParameters().size());
	}

	public void testTerminal02() throws JAXBException {
		final String content = "<terminal name='Logger' factory='org.wolfgang.contrail.bound.Logger'> " + "<param>NetEvent</param> " + "</terminal>";
		final Terminal decoded = decode(Terminal.class, content);
		assertEquals("Logger", decoded.getName());
		assertEquals("org.wolfgang.contrail.bound.Logger", decoded.getFactory());
		assertEquals(Arrays.asList("NetEvent"), decoded.getParameters());
	}

	public void testTerminal03() throws JAXBException {
		final String content = "<terminal name='Logger' factory='org.wolfgang.contrail.bound.Logger'> " + "<param>NetEvent1</param> " + "<param>NetEvent2</param> " + "</terminal>";
		final Terminal decoded = decode(Terminal.class, content);
		assertEquals("Logger", decoded.getName());
		assertEquals("org.wolfgang.contrail.bound.Logger", decoded.getFactory());
		assertEquals(Arrays.asList("NetEvent1", "NetEvent2"), decoded.getParameters());
	}

	// ------------------------------

	public void testEcosystem() throws JAXBException {
		final String content = "<ecosystem><transducer name='PayLoad' factory='org.wolfgang.contrail.codec.payload.PayLoadTransducerFactory' />"
				+ "<transducer name='Serialize' factory='org.wolfgang.contrail.codec.serializer.SerializerTransducerFactory'/>"
				+ "<transducer name='Coercion' factory='org.wolfgang.contrail.codec.coercion.CoercionTransducerFactory'><param>NetEvent</param></transducer>"
				+ "<terminal name='Logger' factory='org.wolfgang.contrail.bound.LoggerComponent'/>"
				+ "<router name='NetworkRoute' factory='org.wolfgang.contrail.network.component.NetworkFactory' singleton='yes'>" + "<self name='A.A'/>"
				+ "<client name='A.B' filter='A.*' endpoint='tcp://localhost:6666'>" + "<flow>PayLoad Serialize Coercion NetworkRoute</flow>" + "</client>"
				+ "<client name='A.C' filter='A.*' endpoint='ws://localhost:6668'>" + "<flow>JSon Coercion NetworkRoute</flow></client></router>"
				+ "<entry name='NetHook'><flow>PayLoad Serialize Coercion NetworkRoute</flow></entry>" + "<entry name='WebHook'><flow>JSon Coercion NetworkRoute</flow></entry>"
				+ "<server endpoint='tcp://localhost:6667'><flow>PayLoad Serialize Coercion NetworkRoute</flow></server>"
				+ "<server endpoint='ws://localhost:6667'><flow>JSon Coercion NetworkRoute</flow></server>" 
				+ "<flow>Network Logger</flow>"
				+ "</ecosystem>";
		final Ecosystem decoded = decode(Ecosystem.class, content);
		assertEquals("Network Logger", decoded.getFlow());
		assertEquals(2, decoded.getEntries().size());
		assertEquals(3, decoded.getTransducers().size());
		assertEquals(1, decoded.getTerminals().size());
		assertEquals(1, decoded.getRouters().size());
		assertEquals(2, decoded.getServers().size());
	}
}
