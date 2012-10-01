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

package org.wolfgang.contrail.ecosystem.lang.model;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

/**
 * <code>TestModel</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestModel {

	@SuppressWarnings("unchecked")
	private <T> T decode(String content, Class<T> type) throws JAXBException, IOException {
		final JAXBContext context = JAXBContext.newInstance(type);
		final Unmarshaller unmarshaller = context.createUnmarshaller();
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content.getBytes());
		try {
			return (T) unmarshaller.unmarshal(byteArrayInputStream);
		} finally {
			byteArrayInputStream.close();
		}
	}

	@Test
	public void testReference() throws JAXBException, IOException, ValidationException {
		final Reference decoded = decode("<ref>Value</ref>", Reference.class);
		decoded.validate();
		assertEquals(ModelFactory.reference("Value"), decoded);
	}

	@Test
	public void testAtom() throws JAXBException, IOException, ValidationException {
		final Atom decoded = decode("<atom>Value</atom>", Atom.class);
		decoded.validate();
		assertEquals(ModelFactory.atom("Value"), decoded);
	}

	@Test
	public void testApply01() throws JAXBException, IOException, ValidationException {
		final Apply decoded = decode("<apply><ref>Value0</ref><atom>Value1</atom></apply>", Apply.class);
		decoded.validate();
		assertEquals(ModelFactory.apply(ModelFactory.reference("Value0"), ModelFactory.atom("Value1")), decoded);
	}

	@Test
	public void testApply02() throws JAXBException, IOException, ValidationException {
		final Apply decoded = decode("<apply bind='param1'><ref>Value0</ref><atom>Value1</atom></apply>", Apply.class);
		decoded.validate();
		assertEquals(ModelFactory.apply("param1", ModelFactory.reference("Value0"), ModelFactory.atom("Value1")), decoded);
	}

	@Test
	public void testFunction() throws JAXBException, IOException, ValidationException {
		final Function decoded = decode("<function><var>a</var><atom>Value</atom></function>", Function.class);
		decoded.validate();
		assertEquals(ModelFactory.function(ModelFactory.atom("Value"), "a"), decoded);
	}

	@Test
	public void testDefine() throws JAXBException, IOException, ValidationException {
		final Definition decoded = decode("<define name='test'><function><var>a</var><atom>Value</atom></function></define>", Definition.class);
		decoded.validate();
		assertEquals(ModelFactory.define("test", ModelFactory.function(ModelFactory.atom("Value"), "a")), decoded);
	}

	@Test
	public void testCase() throws JAXBException, IOException, ValidationException {
		final Switch decoded = decode("<switch><case><function><var>self</var><atom>Value</atom></function></case></switch>", Switch.class);
		decoded.validate();
		assertEquals(ModelFactory.switchCase(ModelFactory.function(ModelFactory.atom("Value"), "self")), decoded);
	}
}
