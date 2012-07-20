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

package org.wolfgang.java.classes;

/**
 * 
 * <code>ClassMethod</code>
 * 
 * @author Didier Plaindoux
 * 
 */
public class ClassMethod {

	private final ConstantPool pool;
	private final int accessFlag;
	private final int name;
	private final int description;
	private final ClassAttribute[] attributes;

	public ClassMethod(int accessFlag, ConstantPool constantPool, int name, int description, ClassAttribute[] attributes) {
		super();
		this.accessFlag = accessFlag;
		this.pool = constantPool;
		this.name = name;
		this.description = description;
		this.attributes = attributes;
	}

	public int getAccessFlag() {
		return accessFlag;
	}

	public String getName() {
		return this.pool.getAt(name).toExternal();
	}

	public String getDescription() {
		return this.pool.getAt(description).toExternal();
	}

	public ClassAttribute[] getAttributes() {
		return attributes;
	}

	public ConstantPool getConstantPool() {
		return pool;
	}

	public String toExternal() {
		return "METHOD <" + getName() + ">[" + getDescription() + "]";
	}
}
