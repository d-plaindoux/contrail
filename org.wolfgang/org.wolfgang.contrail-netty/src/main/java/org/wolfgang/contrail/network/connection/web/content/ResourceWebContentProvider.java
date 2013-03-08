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

package org.wolfgang.contrail.network.connection.web.content;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * <code>ServerPage</code> provides html pages on demand based on simple
 * resourceName TODO improve this code ASAP
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class ResourceWebContentProvider implements WebContentProvider {

	private static final String HTDOCS = "/htdocs/";

	public ResourceWebContentProvider() {
		// TODO
	}

	@Override
	public boolean canProvideContent(String resourceName) {
		return ResourceWebContentProvider.class.getResource(HTDOCS + resourceName) != null;
	}

	@Override
	public byte[] getContent(String resourceName) throws IOException {
		final URL resource = ResourceWebContentProvider.class.getResource(HTDOCS + resourceName);

		if (resource == null) {
			throw new IOException();
		}

		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			final InputStream inputStream = resource.openStream();
			try {
				final byte[] bytes = new byte[1024];
				int len;
				while ((len = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, len);
					outputStream.flush();
				}
			} finally {
				inputStream.close();
			}
		} finally {
			outputStream.close();
		}

		// Unfold HTML document

		return outputStream.toByteArray();
	}
}
