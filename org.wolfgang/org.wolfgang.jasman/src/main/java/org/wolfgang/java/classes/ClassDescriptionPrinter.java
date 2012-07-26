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

import java.io.PrintStream;

/**
 * <code>ClassDescriptionPrinter</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class ClassDescriptionPrinter {

	/**
	 * Constructor
	 */
	private ClassDescriptionPrinter() {
		super();
	}

	/**
	 * Method dumping a class description in a given print stream
	 * 
	 * @param description
	 *            The class description to dump
	 * @param printStream
	 *            The print stream
	 */
	public static void dump(ClassDescription description, PrintStream printStream) {

		final ConstantPool constantPool = description.getConstantPool();
		int i = 0;
		for (Constant item : constantPool) {
			i += 1;
			try {
				printStream.println("\t[" + i + "] " + item.toExternal());
			} catch (ClassCastException e) {
				printStream.println("\t[" + i + "] <cast error> " + item.toString());
			}
		}

		printStream.println("\t<class> " + description.getClassName());
		printStream.println("\t<extends> " + description.getSuperClassName());

		final String[] interfacesName = description.getInterfacesName();
		for (String interfaceName : interfacesName) {
			printStream.println("\t<implements> " + interfaceName);
		}

		printStream.println("\t<attributes>");
		for (ClassAttribute attribute : description.getAttributes()) {
			printStream.println("\t - " + attribute.toExternal());
		}

		printStream.println("\t<fields>");
		final ClassField[] fields = description.getFields();
		for (ClassField field : fields) {
			printStream.println("\t - " + field.toExternal());
			if ((field.getAccessFlag() & 0x1000) == 0x1000) {
				printStream.println("\t + Synthetic");
			}
			for (ClassAttribute attribute : field.getAttributes()) {
				printStream.println("\t   o " + attribute.toExternal());
			}
		}

		printStream.println("\t<methods>");
		final ClassMethod[] methods = description.getMethods();
		for (ClassMethod method : methods) {
			printStream.println("\t - " + method.toExternal());
			if ((method.getAccessFlag() & 0x1000) == 0x1000) {
				printStream.println("\t + Synthetic");
			}
			for (ClassAttribute attribute : method.getAttributes()) {
				printStream.println("\t   o " + attribute.toExternal());
			}
		}
	}

}
