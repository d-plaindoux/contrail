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

class AbstractContentProvider {
	
	protected byte[] getContent(InputStream inputStream) throws IOException {

		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			final byte[] bytes = new byte[1024];
			int len;
			while ((len = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, len);
				outputStream.flush();
			}
		} finally {
			outputStream.close();
		}

		return outputStream.toByteArray();
	}
	
}
