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

package org.wolfgang.java.container.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.wolfgang.java.container.Container;
import org.wolfgang.java.container.ContainerEntry;

/**
 * <code>PackageVisitor</code>
 * 
 * @author Didier Plaindoux
 */
public class PackageContainer implements Container {

	/**
	 * Containers provided by the package
	 */
	private final List<Container> containers;

	/**
	 * Constructor
	 * 
	 * @param containerSpecification
	 */
	public PackageContainer(List<String> containerSpecification) {
		super();
		this.containers = new ArrayList<Container>();
		for (String string : containerSpecification) {
			final File file = new File(string);
			if (file.getName().endsWith(".jar") && file.isFile() && file.canRead()) {
				this.containers.add(new ZiPContainer(file));
			} else if (file.isDirectory()) {
				this.containers.add(new FileContainer(file));
			} else {
				// Skip this file entry
			}
		}
	}

	@Override
	public Collection<ContainerEntry> getEntries(String[] packagesName) {
		final Set<ContainerEntry> entries = new HashSet<ContainerEntry>();

		for (Container container : containers) {
			entries.addAll(container.getEntries(packagesName));
		}

		return entries;
	}

	public void dispose() {
		// Do nothing
	}
}
