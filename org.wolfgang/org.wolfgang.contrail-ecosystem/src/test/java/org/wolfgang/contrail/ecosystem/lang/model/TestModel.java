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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * <code>TestModel</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestModel extends TestCase {

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
		assertEquals("Value", decoded.getValue());
	}

	@Test
	public void testAtom() throws JAXBException, IOException {
		final Atom decoded = decode("<atom>Value</atom>", Atom.class);
		assertEquals("Value", decoded.getValue());
	}

	@Test
	public void testApply() throws JAXBException, IOException, ValidationException {
		final Apply decoded = decode("<apply><ref>Value0</ref><atom>Value1</atom></apply>", Apply.class);
		decoded.validate();
		assertEquals(2, decoded.getExpressions().size());
		final Expression actual0 = decoded.getExpressions().get(0);
		assertEquals(Reference.class, actual0.getClass());
		assertEquals("Value0", ((Reference) actual0).getValue());
		final Expression actual1 = decoded.getExpressions().get(1);
		assertEquals(Atom.class, actual1.getClass());
		assertEquals("Value1", ((Atom) actual1).getValue());
	}

	@Test
	public void testFunction() throws JAXBException, IOException, ValidationException {
		final Function decoded = decode("<function><var>a</var><atom>Value</atom></function>", Function.class);
		decoded.validate();
		assertEquals("a", decoded.getParameters().get(0));
		final Expression actual = decoded.getExpressions().get(0);
		assertEquals(Atom.class, actual.getClass());
		assertEquals("Value", ((Atom) actual).getValue());
	}

	@Test
	public void testDefine() throws JAXBException, IOException, ValidationException {
		final Definition decoded = decode("<define name='test'><function><var>a</var><atom>Value</atom></function></define>", Definition.class);
		decoded.validate();
		assertEquals("test", decoded.getName());
		assertEquals(1, decoded.getExpressions().size());
		assertEquals(Function.class, decoded.getExpressions().get(0).getClass());
		final Function function = (Function) decoded.getExpressions().get(0);
		assertEquals("a", function.getParameters().get(0));
		assertEquals(1, function.getExpressions().size());
		final Expression actual = function.getExpressions().get(0);
		assertEquals(Atom.class, actual.getClass());
		assertEquals("Value", ((Atom) actual).getValue());
	}
}
