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

package org.wolfgang.contrail.component.data;

import junit.framework.TestCase;

import org.wolfgang.contrail.data.DataInformation;
import org.wolfgang.contrail.data.DataInformationFactory;
import org.wolfgang.contrail.data.DataInformationValueAlreadyDefinedException;
import org.wolfgang.contrail.data.DataInformationValueNotFoundException;
import org.wolfgang.contrail.data.DataInformationValueTypeException;

/**
 * <code>TestDataInformation</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestDataInformation extends TestCase {

	public void testGetInformation01() {
		final DataInformation dataInformation = DataInformationFactory.createDataInformation();
		try {
			dataInformation.getValue("foo", String.class);
			fail();
		} catch (DataInformationValueNotFoundException e) {
			// OK
		} catch (DataInformationValueTypeException e) {
			fail();
		}
	}

	public void testSetInformation01() {
		final DataInformation dataInformation = DataInformationFactory.createDataInformation();
		try {
			dataInformation.setValue("foo", "bar");
		} catch (DataInformationValueAlreadyDefinedException e1) {
			fail();
		}
		try {
			dataInformation.setValue("foo", "baz");
			fail();
		} catch (DataInformationValueAlreadyDefinedException e1) {
			// OK
		}
	}

	public void testSetAndGetInformation02() {
		final DataInformation dataInformation = DataInformationFactory.createDataInformation();
		try {
			dataInformation.setValue("foo", "bar");
		} catch (DataInformationValueAlreadyDefinedException e1) {
			fail();
		}
		try {
			assertEquals("bar", dataInformation.getValue("foo", String.class));
		} catch (DataInformationValueNotFoundException e) {
			fail();
		} catch (DataInformationValueTypeException e) {
			fail();
		}
	}

	public void testSetAndGetInformation03() {
		final DataInformation dataInformation = DataInformationFactory.createDataInformation();
		try {
			dataInformation.setValue("foo", "bar");
		} catch (DataInformationValueAlreadyDefinedException e1) {
			fail();
		}
		try {
			dataInformation.getValue("foo", Integer.class);
			fail();
		} catch (DataInformationValueNotFoundException e) {
			fail();
		} catch (DataInformationValueTypeException e) {
			// OK
		}
	}
}
