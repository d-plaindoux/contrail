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
import java.util.HashSet;
import java.util.Set;

import org.wolfgang.java.classes.Constant.CLASS;

/**
 * <code>ClassDescriptionImpl</code>
 * 
 * @author Didier Plaindoux
 */
public class ClassDescriptionImpl implements ClassDescription {

	/**
	 * Attributes
	 */

	private int majorVersion;
	private int minorVersion;
	private ConstantPool constantPool;
	private int accessFlags;
	private int className;
	private int superClassName;
	private int[] interfacesName;
	private ClassField[] fields;
	private ClassMethod[] methods;
	private ClassAttribute[] attributes;
	private long decodingTime;

	/**
	 * Constructor
	 */
	ClassDescriptionImpl() {
		super();
	}

	@Override
	public long getDecodingTime() {
		return decodingTime;
	}

	public void setDecodingTime(long decodingTime) {
		this.decodingTime = decodingTime;
	}

	@Override
	public int getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}

	@Override
	public int getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}

	@Override
	public ConstantPool getConstantPool() {
		return constantPool;
	}

	public void setConstantPool(ConstantPool constantPool) {
		this.constantPool = constantPool;
	}

	@Override
	public int getAccessFlags() {
		return accessFlags;
	}

	public void setAccessFlags(int accessFlags) {
		this.accessFlags = accessFlags;
	}

	@Override
	public String getClassName() {
		return this.constantPool.getAt(className).toExternal();
	}

	public void setClassName(int className) {
		this.className = className;
	}

	@Override
	public String[] getDeclaredTypes() {
		final Set<String> declared = new HashSet<String>();

		for (Constant constant : this.getConstantPool()) {
			if (constant instanceof CLASS) {
				declared.add(constant.toExternal());
			}
		}

		return declared.toArray(new String[declared.size()]);
	}

	@Override
	public String getSuperClassName() {
		return this.constantPool.getAt(superClassName).toExternal();
	}

	public void setSuperClassName(int superClassName) {
		this.superClassName = superClassName;
	}

	@Override
	public String[] getInterfacesName() {
		final String[] names = new String[this.interfacesName.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = this.constantPool.getAt(interfacesName[i]).toExternal();
		}
		return names;
	}

	public void setInterfacesName(int[] interfaces) {
		this.interfacesName = interfaces.clone();
	}

	@Override
	public ClassField findFieldByName(String name) throws DefinitionNotFound {
		for (ClassField field : getFields()) {
			if (name.equals(field.getName())) {
				return field;
			}
		}

		throw new DefinitionNotFound();
	}

	@Override
	public ClassField[] getFields() {
		return fields;
	}

	public void setFields(ClassField[] fields) {
		this.fields = fields;
	}

	@Override
	public ClassMethod findMethodByName(String name) throws DefinitionNotFound {
		for (ClassMethod method : getMethods()) {
			if (name.equals(method.getName())) {
				return method;
			}
		}

		throw new DefinitionNotFound();
	}

	@Override
	public ClassMethod[] getMethods() {
		return methods;
	}

	public void setMethods(ClassMethod[] methods) {
		this.methods = methods;
	}

	@Override
	public ClassAttribute[] getAttributes() {
		return attributes;
	}

	public void setAttributes(ClassAttribute[] attributes) {
		this.attributes = attributes;
	}

	@Override
	public void dump(PrintStream printStream) {

		final ConstantPool constantPool = this.getConstantPool();
		int i = 0;
		for (Constant item : constantPool) {
			i += 1;
			try {
				printStream.println("\t[" + i + "] " + item.toExternal());
			} catch (ClassCastException e) {
				printStream.println("\t[" + i + "] <cast error> " + item.toString());
			}
		}

		printStream.println("\t<class> " + this.getClassName());
		printStream.println("\t<extends> " + this.getSuperClassName());

		final String[] interfacesName = this.getInterfacesName();
		for (String interfaceName : interfacesName) {
			printStream.println("\t<implements> " + interfaceName);
		}

		printStream.println("\t<attributes>");
		for (ClassAttribute attribute : this.getAttributes()) {
			printStream.println("\t - " + attribute.toExternal());
		}

		printStream.println("\t<fields>");
		final ClassField[] fields = this.getFields();
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
		final ClassMethod[] methods = this.getMethods();
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
