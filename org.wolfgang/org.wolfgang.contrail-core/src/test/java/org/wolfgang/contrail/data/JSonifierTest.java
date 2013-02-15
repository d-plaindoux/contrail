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

package org.wolfgang.contrail.data;

import java.util.HashMap;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.contrail.codec.object.Decoder;
import org.wolfgang.contrail.codec.object.Encoder;
import org.wolfgang.contrail.component.pipeline.transducer.DataTransducerException;

/**
 * <code>JSonifierTest</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class JSonifierTest {

	public static class Simple {
		final public String name;
		final private int theAge;
		final public ObjectRecord data;

		public Simple(String name, int age, ObjectRecord data) {
			super();
			this.name = name;
			this.theAge = age;
			this.data = data;
		}

		public int getAge() {
			return this.theAge;
		}
	}

	@Test
	public void shouldHaveJSonifiedObjectWhenJSonifySimpleObject() throws DataTransducerException {
		final JSonifier jSonifier = JSonifier.withNames("name", "age", "data").withTypes(String.class, Integer.TYPE, ObjectRecord.class);

		final Simple simpleClass = new Simple("A", 42, new ObjectRecord());
		final ObjectRecord structure = jSonifier.toStructure(simpleClass, new Encoder(new HashMap<String, JSonifier>()));

		TestCase.assertEquals(Simple.class.getName(), structure.get(JSonifier.JSonName, String.class));

		final ObjectRecord parameters = structure.get(JSonifier.JSonValue, ObjectRecord.class);

		TestCase.assertEquals("A", parameters.get("name"));
		TestCase.assertEquals(42, parameters.get("age"));
		TestCase.assertEquals(new ObjectRecord(), parameters.get("data"));
	}

	@Test
	public void shouldHaveSimpleObjectWhenJSonifyJSonifiedObject() throws DataTransducerException {
		final JSonifier jSonifier = JSonifier.withNames("name", "age", "data").withTypes(String.class, Integer.TYPE, ObjectRecord.class);

		final ObjectRecord structure = new ObjectRecord().set(JSonifier.JSonName, Simple.class.getName()).set(JSonifier.JSonValue,
				new ObjectRecord().set("name", "B").set("age", 24).set("data", new ObjectRecord()));
		final Object object = jSonifier.toObject(structure, new Decoder(new HashMap<String, JSonifier>()));

		TestCase.assertEquals(Simple.class.getName(), object.getClass().getName());

		final Simple simple = (Simple) object;

		TestCase.assertEquals("B", simple.name);
		TestCase.assertEquals(24, simple.theAge);
		TestCase.assertEquals(new ObjectRecord(), simple.data);
	}
}
