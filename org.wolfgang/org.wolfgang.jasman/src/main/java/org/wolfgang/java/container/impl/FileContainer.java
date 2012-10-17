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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.wolfgang.java.container.Container;
import org.wolfgang.java.container.ContainerEntry;

/**
 * <code>FileContainer</code>
 * 
 * @author Didier Plaindoux
 */
public class FileContainer implements Container {

	private final File root;
	private final List<Container> containers;

	public FileContainer(File root) {
		super();
		this.root = root;
		this.containers = new ArrayList<Container>();
	}

	@Override
	public Collection<ContainerEntry> getEntries(String... packagesName) {
		final Set<ContainerEntry> entries = new HashSet<ContainerEntry>();

		final List<File> visited = new ArrayList<File>();
		final List<File> toVisit = new ArrayList<File>();

		toVisit.add(this.root);

		while (toVisit.size() > 0) {
			try {
				final File current = toVisit.remove(0).getCanonicalFile();
				if (!visited.contains(current)) {
					visited.add(current);
					if (current.isDirectory()) {
						final File[] files = current.listFiles();
						if (files != null) {
							toVisit.addAll(Arrays.asList(files));
						}
					} else {
						if (current.getName().endsWith(".class")) {
							entries.add(new FileContainerEntry(current));
						} else if (current.getName().endsWith(".jar")) {
							final ZiPContainer container = new ZiPContainer(current);
							this.containers.add(container);
							final Collection<ContainerEntry> subEntries = container.getEntries(packagesName);
							entries.addAll(subEntries);
						}
					}
				}
			} catch (IOException e) {
				// Skip this file
			}
		}

		return entries;
	}

	@Override
	public void dispose() {
		for (Container container : this.containers) {
			container.dispose();
		}
		this.containers.clear();
	}

}
