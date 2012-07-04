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

	public void testEntry() throws JAXBException {
		final String content = "<entry name='A.A'> <flow>Logger</flow> </entry>";
		final Entry decoded = decode(Entry.class, content);
		assertEquals("A.A", decoded.getName());
		assertEquals("Logger", decoded.getFlow());
	}

	public void testClient() throws JAXBException {
		final String content = "<client name='A.B' filter='A.*' endpoint='tcp://localhost:6666'> <flow>PayLoad Serialize Coercion NetworkRoute</flow> </client>";
		final Client decoded = decode(Client.class, content);
		assertEquals("A.B", decoded.getName());
		assertEquals("A.*", decoded.getFilter());
		assertEquals("tcp://localhost:6666", decoded.getEndpoint());
		assertEquals("PayLoad Serialize Coercion NetworkRoute", decoded.getFlow());
	}

	public void testRouter() throws JAXBException {
	}

	public void testServer() throws JAXBException {
		final String content = "<server endpoint='tcp://localhost:6667'> <flow>PayLoad Serialize Coercion NetworkRoute</flow> </server>";
		final Server decoded = decode(Server.class, content);
		assertEquals("tcp://localhost:6667", decoded.getEndpoint());
		assertEquals("PayLoad Serialize Coercion NetworkRoute", decoded.getFlow());
	}

	public void testTransducer() {

	}

	public void testTerminal() {

	}

	public void testEcosystem() {

	}

}
