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
 * <code>AnnotationImpl</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
final class AnnotationImpl implements Annotation {

	private final ConstantPool constantPool;
	private final int name;
	private final AnnotationElement[] parameters;

	/**
	 * Constructor
	 * 
	 * @param constantPool
	 * @param name
	 * @param parameters
	 */
	AnnotationImpl(ConstantPool constantPool, int name, AnnotationElement[] parameters) {
		super();
		this.constantPool = constantPool;
		this.name = name;
		this.parameters = parameters;
	}

	@Override
	public String getName() {
		return TypeDecoder.getType(this.constantPool.getAt(name).toExternal());
	}

	@Override
	public AnnotationElement[] getParameters() {
		return parameters;
	}

	@Override
	public AnnotationElement findByName(String name) {
		for (AnnotationElement element : parameters) {
			if (element.getName().equals(name)) {
				return element;
			}
		}

		return null;
	}

	@Override
	public String toExternal() {
		final StringBuilder builder = new StringBuilder();

		builder.append('@').append(getName());
		if (parameters.length > 0) {
			builder.append('(');
			for (int i = 0; i < parameters.length; i++) {
				if (i > 0) {
					builder.append(',');
				}
				builder.append(parameters[i].getName());
				builder.append('=');
				builder.append(parameters[i].getValue().toExternal());
			}
			builder.append(')');
		}

		return builder.toString();
	}
}
