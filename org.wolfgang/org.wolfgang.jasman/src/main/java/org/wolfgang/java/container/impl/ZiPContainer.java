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
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.wolfgang.java.container.Container;
import org.wolfgang.java.container.ContainerEntry;

/**
 * <code>FileContainer</code>
 * 
 * @author Didier Plaindoux
 */
public class ZiPContainer implements Container {

	private final File zipName;

	private ZipFile zipFile;

	public ZiPContainer(File root) {
		super();
		this.zipName = root;
		this.zipFile = null;
	}

	@Override
	public Collection<ContainerEntry> getEntries(String... packagesName) {
		final Set<ContainerEntry> entries = new HashSet<ContainerEntry>();

		try {
			this.zipFile = new ZipFile(zipName);

			final Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
			for (; zipEntries.hasMoreElements();) {
				final ZipEntry zipEntry = zipEntries.nextElement();
				if (zipEntry.getName().endsWith(".class")) {
					entries.add(new ZipContainerEntry(zipFile, zipEntry.getName()));
				}
			}
		} catch (IOException e) {
			// Skip it for the moment
		}

		return entries;
	}

	@Override
	public void dispose() {
		if (this.zipFile != null) {
			try {
				this.zipFile.close();
			} catch (IOException e) {
				// skip it
			}
			this.zipFile = null;
		}
	}

}
