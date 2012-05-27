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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

/**
 * <code>Resource</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class Resource {

	/**
	 * 
	 */
	private final String contentResource;

	/**
	 * Constructor
	 * 
	 * @param contentResource
	 */
	public Resource(String contentResource) {
		this.contentResource = contentResource;
	}

	/**
	 * @param resourceName
	 * @param definitions
	 * @return
	 * @throws IOException
	 */
	public ChannelBuffer getContent(Map<String, String> definitions) throws IOException {
		String content = contentResource;

		for (Entry<String, String> entry : definitions.entrySet()) {
			content = content.replace("${" + entry.getKey() + "}", entry.getValue());
		}

		return ChannelBuffers.copiedBuffer(content.getBytes());
	}

	/**
	 * @return
	 */
	public Collection<String> getFreeVariables() {
		final List<String> freeVariables = new ArrayList<String>();

		int start = 0;
		int end = start;
		while (start != -1) {
			start = contentResource.indexOf("${", start);
			if (start != -1) {
				end = contentResource.indexOf("}", start);
				if (end == -1) {
					start = -1;
				} else {
					final String token_name = contentResource.substring(start + 2, end);
					if (!freeVariables.contains(token_name)) {
						freeVariables.add(token_name);
					}
					start = end;
				}
			}
		}

		return freeVariables;
	}
}
