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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Test;
import org.wolfgang.contrail.network.connection.web.WebServerPage;

/**
 * <code>TestWebPage</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestWebPage {

	@SuppressWarnings("serial")
	@Test
	public void testWebPage01() throws IOException {
		final WebServerPage serverPage = new WebServerPage();

		final byte[] resource01 = serverPage.getResource("index.html").getContent(new HashMap<String, String>() {
			{
				this.put("title", "Hello");
				this.put("product", "Contrail");
			}
		});

		final byte[] resource02 = serverPage.getResource("index.html.orig").getContent(new HashMap<String, String>());

		assertEquals(new String(resource01), new String(resource02));
	}

	@Test
	public void testWebPage02() {
		final WebServerPage serverPage = new WebServerPage();

		try {
			serverPage.getResource("undefined");
			fail();
		} catch (IOException e) {
			// OK
		}
	}
}
