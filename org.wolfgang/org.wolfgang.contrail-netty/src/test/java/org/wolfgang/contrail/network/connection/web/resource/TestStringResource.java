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

package org.wolfgang.contrail.network.connection.web.resource;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;

/**
 * <code>TestResource</code>
 *
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestStringResource extends TestCase {
	
	public void testNominal() {
		final String content = "This is a ${simple} test with some ${meta.variables}";
		final Resource resource = new StringResourceImpl(content);
		final Collection<String> freeVariables = resource.getFreeVariables();
		
		System.err.print(Arrays.toString(freeVariables.toArray()));
		
		assertTrue(freeVariables.contains("simple"));
		assertTrue(freeVariables.contains("meta.variables"));
	}

}
