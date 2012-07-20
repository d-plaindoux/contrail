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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.wolfgang.java.container.ContainerEntry;

/**
 * <code>FileContainerEntry</code>
 * 
 * @author Didier Plaindoux
 * 
 */
public class FileContainerEntry implements ContainerEntry {

	private final File file;

	public FileContainerEntry(File file) {
		super();
		this.file = file;
	}

	@Override
	public InputStream open() throws FileNotFoundException {
		return new FileInputStream(file);
	}

	@Override
	public String toString() {
		return "FileEntry [" + file + "]";
	}
}
