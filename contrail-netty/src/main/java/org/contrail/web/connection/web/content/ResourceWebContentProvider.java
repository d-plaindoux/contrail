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

package org.contrail.web.connection.web.content;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public final class ResourceWebContentProvider extends AbstractContentProvider implements WebContentProvider {

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
			throw new IOException("Resource [" + resourceName + "] not found");
		} else {
			final InputStream inputStream = resource.openStream();
			try {
				return this.getContent(inputStream);
			} finally {
				inputStream.close();
			}
		}
	}
}
