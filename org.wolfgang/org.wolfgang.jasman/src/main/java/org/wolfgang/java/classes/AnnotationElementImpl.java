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
 * <code>AnnotationElementImpl</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
class AnnotationElementImpl implements AnnotationElement {

	private final ConstantPool constantPool;
	private final int name;
	private final AnnotationElementValue value;

	/**
	 * Constructor
	 * 
	 * @param name
	 * @param value
	 */
	AnnotationElementImpl(ConstantPool constantPool, int name, AnnotationElementValue value) {
		super();
		this.constantPool = constantPool;
		this.name = name;
		this.value = value;
	}

	@Override
	public String getName() {
		return this.constantPool.getAt(name).toExternal();
	}

	@Override
	public AnnotationElementValue getValue() {
		return this.value;
	}

}
