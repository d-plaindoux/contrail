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

package org.contrail.web.connection.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.contrail.web.connection.web.content.ResourceWebContentProvider;
import org.contrail.web.connection.web.content.WebContentProvider;

/**
 * <code>TestWebPage</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class WebPageTest {

	@Test
	public void testWebPage01() throws IOException {
		final WebContentProvider serverPage = new ResourceWebContentProvider();

		final byte[] resource01 = serverPage.getContent("index.html");
		final byte[] resource02 = serverPage.getContent("index.html.orig");

		assertEquals(new String(resource01), new String(resource02));
	}

	@Test
	public void testWebPage02() {
		final WebContentProvider serverPage = new ResourceWebContentProvider();

		try {
			serverPage.getContent("undefined");
			fail();
		} catch (IOException e) {
			// OK
		}
	}
}
