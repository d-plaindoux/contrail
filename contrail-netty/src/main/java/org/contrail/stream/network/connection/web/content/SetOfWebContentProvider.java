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

package org.contrail.stream.network.connection.web.content;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>ServerPage</code> provides html pages on demand based on simple
 * resourceName TODO improve this code ASAP
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class SetOfWebContentProvider implements WebContentProvider {

	private final List<WebContentProvider> providers;

	public SetOfWebContentProvider(WebContentProvider... providers) {
		this.providers = new ArrayList<WebContentProvider>();
	}

	public SetOfWebContentProvider add(WebContentProvider provider) {
		this.providers.add(provider);
		return this;
	}

	@Override
	public boolean canProvideContent(String resourceName) {
		for (WebContentProvider provider : providers) {
			if (provider.canProvideContent(resourceName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public byte[] getContent(String resourceName) throws IOException {
		for (WebContentProvider provider : providers) {
			if (provider.canProvideContent(resourceName)) {
				return provider.getContent(resourceName);
			}
		}

		throw new IOException("Resource [" + resourceName + "] not found");
	}

}
