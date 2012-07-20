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

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import org.wolfgang.java.container.ContainerEntry;

/**
 * <code>ZipContainerEntry</code>
 * 
 * @author Didier Plaindoux
 * 
 */
public class ZipContainerEntry implements ContainerEntry {

	private final ZipFile zip;
	private final String name;

	public ZipContainerEntry(ZipFile zip, String name) {
		super();
		this.zip = zip;
		this.name = name;
	}

	@Override
	public InputStream open() throws IOException {
		return zip.getInputStream(zip.getEntry(name));
	}

	@Override
	public String toString() {
		return "ZipEntry [" + name + "]";
	}	
}
