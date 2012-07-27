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
 * <code>ClassField</code>
 * 
 * @author Didier Plaindoux
 */
class ClassFieldImpl implements ClassField {

	private final ConstantPool pool;
	private final int accessFlag;
	private int name;
	private int description;
	private ClassAttribute[] attributes;

	/**
	 * Constructor
	 * 
	 * @param accessFlag
	 * @param constantPool
	 * @param name
	 * @param description
	 * @param attributes
	 */
	ClassFieldImpl(int accessFlag, ConstantPool constantPool, int name, int description, ClassAttribute[] attributes) {
		super();
		this.accessFlag = accessFlag;
		this.pool = constantPool;
		this.name = name;
		this.description = description;
		this.attributes = attributes;
	}

	@Override
	public int getAccessFlag() {
		return accessFlag;
	}

	@Override
	public String getName() {
		return this.pool.getAt(name).toExternal();
	}

	@Override
	public void setName(int name) {
		this.name = name;
	}

	@Override
	public String getDescription() {
		return this.pool.getAt(description).toExternal();
	}

	@Override
	public void setDescription(int description) {
		this.description = description;
	}

	@Override
	public ClassAttribute[] getAttributes() {
		return attributes;
	}

	@Override
	public void setAttributes(ClassAttribute[] attributes) {
		this.attributes = attributes;
	}

	@Override
	public ConstantPool getConstantPool() {
		return pool;
	}

	@Override
	public String toExternal() {
		return "FIELD <" + getName() + ">[" + getDescription() + "]";
	}
}