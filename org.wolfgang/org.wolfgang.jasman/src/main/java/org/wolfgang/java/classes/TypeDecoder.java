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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <code>TypeDecoder</code> is dedicated to string representation decoding.
 * 
 * @author Didier Plaindoux
 */
public final class TypeDecoder {

	private static final String singleType = "(\\[*[BCDFIJVSZ]|\\[*L[^;]+;)";
	private static final String methodType = "\\(([^\\)]*)\\)(.*)";

	/**
	 * Constructor
	 */
	private TypeDecoder() {
		// Prevent useless object creation
	}

	/**
	 * Provides the normalized class name using a path like convention.
	 * 
	 * <pre>
	 *  "a.b.c.D" :equals: getClassName("a/b/c/D");
	 * </pre>
	 * 
	 * @param type
	 * @return
	 */
	public static String getClassName(String type) {
		return type.replace('/', '.');
	}

	/**
	 * String type decoder
	 * 
	 * @param type
	 *            The type denoted by a string
	 * @return a class (Never <code>null</code>)
	 */
	private static String getTypeName(String type) {
		if (type.equals("B")) {
			return Byte.TYPE.getName();
		} else if (type.equals("C")) {
			return Character.TYPE.getName();
		} else if (type.equals("D")) {
			return Double.TYPE.getName();
		} else if (type.equals("F")) {
			return Float.TYPE.getName();
		} else if (type.equals("I")) {
			return Integer.TYPE.getName();
		} else if (type.equals("J")) {
			return Long.TYPE.getName();
		} else if (type.equals("S")) {
			return Short.TYPE.getName();
		} else if (type.equals("V")) {
			return Void.TYPE.getName();
		} else if (type.equals("Z")) {
			return Boolean.TYPE.getName();
		} else if (type.startsWith("L") && type.endsWith(";")) {
			return getClassName(type.substring(1, type.length() - 1));
		} else if (type.startsWith("[")) {
			// TODO -- Decompose this type
			return Array.class.getName();
		} else {
			throw new IllegalArgumentException(type);
		}
	}

	/**
	 * String types decoder.
	 * 
	 * <pre>
	 * 	{ "Byte", "java.lang.String" } :equals: getTypes("BLjava/lang/String;")
	 * </pre>
	 * 
	 * @param string
	 *            The types representation
	 * @return a class names array (Never <code>null</code>)
	 */
	private static String[] getTypes(String string) {
		final Pattern compile = Pattern.compile(singleType);
		final Matcher matcher = compile.matcher(string);

		final List<String> result = new ArrayList<String>();
		while (matcher.find()) {
			result.add(getTypeName(matcher.group()));
		}

		if (matcher.hitEnd()) {
			return result.toArray(new String[result.size()]);
		} else {
			throw new IllegalArgumentException("Failed at <" + matcher.end() + "> with " + string);
		}
	}

	/**
	 * String type decoder.
	 * 
	 * <pre>
	 * 	"java.lang.String" :equals: getType("Ljava/lang/String;")
	 * </pre>
	 * 
	 * @param string
	 *            The type representation
	 * @return a class name (Never <code>null</code>)
	 */
	public static String getType(String string) {
		final Pattern compile = Pattern.compile(singleType);
		final Matcher matcher = compile.matcher(string);

		if (matcher.matches()) {
			return getTypeName(matcher.group(1));
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Method result type decoder.
	 * 
	 * <pre>
	 * 	"java.lang.String" :equals: getMethodResult("(BLjava/lang/String;)Ljava/lang/String;")
	 * </pre>
	 * 
	 * @param string
	 *            The method profile representation
	 * @return a class name (Never <code>null</code>)
	 */
	public static String getMethodResult(String string) {
		final Pattern compile = Pattern.compile(methodType);
		final Matcher matcher = compile.matcher(string);

		if (matcher.matches()) {
			return getTypeName(matcher.group(2));
		} else {
			throw new IllegalArgumentException(string);
		}
	}

	/**
	 * Method parameters type decoder.
	 * 
	 * <pre>
	 * 	{ "Byte", "java.lang.String" } :equals: getMethodParameters("(BLjava/lang/String;)Ljava/lang/String;")
	 * </pre>
	 * 
	 * @param string
	 *            The method profile representation
	 * @return a class names array (Never <code>null</code>)
	 */
	public static String[] getMethodParameters(String string) {
		final Pattern compile = Pattern.compile(methodType);
		final Matcher matcher = compile.matcher(string);

		if (matcher.matches()) {
			return getTypes(matcher.group(1));
		} else {
			throw new IllegalArgumentException();
		}
	}
}