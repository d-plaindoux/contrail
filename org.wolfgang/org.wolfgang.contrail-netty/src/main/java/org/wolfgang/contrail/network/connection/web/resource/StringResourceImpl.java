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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <code>StringResourceImpl</code> is a Resource implementation based a single
 * string.
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class StringResourceImpl implements Resource {

	/**
	 * The string template
	 */
	private final String contentResource;

	/**
	 * Constructor
	 * 
	 * @param contentResource
	 *            The string template
	 */
	public StringResourceImpl(String contentResource) {
		this.contentResource = contentResource;
	}

	@Override
	public byte[] getContent(Map<String, String> definitions) {
		String content = contentResource;

		for (Entry<String, String> entry : definitions.entrySet()) {
			content = content.replace("${" + entry.getKey() + "}", entry.getValue());
		}

		return content.getBytes();
	}

	@Override
	public Collection<String> getFreeVariables() {
		final Collection<String> freeVariables = new ArrayList<String>();

		int start = 0;
		int end;

		while (start != -1) {
			start = contentResource.indexOf("${", start);
			if (start != -1) {
				end = contentResource.indexOf("}", start);
				if (end == -1) {
					start = -1;
				} else {
					final String tokenName = contentResource.substring(start + 2, end);
					if (!freeVariables.contains(tokenName)) {
						freeVariables.add(tokenName);
					}
					start = end;
				}
			}
		}

		return freeVariables;
	}
}
