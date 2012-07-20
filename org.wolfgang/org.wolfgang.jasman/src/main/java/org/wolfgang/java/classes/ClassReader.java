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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.wolfgang.java.classes.Constant.CLASS;
import org.wolfgang.java.classes.Constant.DOUBLE;
import org.wolfgang.java.classes.Constant.FIELD_REF;
import org.wolfgang.java.classes.Constant.FLOAT;
import org.wolfgang.java.classes.Constant.INTEGER;
import org.wolfgang.java.classes.Constant.INTERFACE_METHOD_REF;
import org.wolfgang.java.classes.Constant.LONG;
import org.wolfgang.java.classes.Constant.METHOD_REF;
import org.wolfgang.java.classes.Constant.NAME_AND_TYPE;
import org.wolfgang.java.classes.Constant.STRING;
import org.wolfgang.java.classes.Constant.UNUSED;
import org.wolfgang.java.classes.Constant.UTF8;

/**
 * <code>ClassReader</code>
 * 
 * @author Didier Plaindoux
 */
public class ClassReader {

	public static ClassDescription getClassDescription(File file) throws IOException {
		final FileInputStream stream = new FileInputStream(file);
		try {
			return new ClassReader(stream).getDescription();
		} finally {
			stream.close();
		}
	}

	public static ClassDescription getClassDescription(InputStream stream) throws IOException {
		return new ClassReader(new BufferedInputStream(stream)).getDescription();
	}

	// -----------------------------------------------------------------------------------------------------------------

	private final DataInputStream stream;
	private final ClassDescriptionImpl description;

	private ClassReader(InputStream in) throws IOException {
		this.stream = new DataInputStream(in);
		this.description = new ClassDescriptionImpl();

		final long t0 = System.currentTimeMillis();

		try {
			this.checkMagicHeader();
			this.getVersion();
			this.getConstantPool();
			this.getSpecifications();
			this.getFields();
			this.getMethods();
			this.getAttributes();
		} finally {
			description.setDecodingTime(System.currentTimeMillis() - t0);
		}
	}

	public ClassDescription getDescription() {
		return description;
	}

	private byte[] getNextBytes(int len) throws IOException {
		final byte[] buffer = new byte[len];
		this.stream.read(buffer);
		return buffer;
	}

	private int getNextUnsignedByte() throws IOException {
		return this.stream.readUnsignedByte();
	}

	private char[] getChars(int len) throws IOException {
		final char[] value = new char[len];
		for (int i = 0; i < len; i++) {
			value[i] = (char) getNextUnsignedByte();
		}
		return value;
	}

	private int getNextShort() throws IOException {
		return this.stream.readShort();
	}

	private int getNextInt() throws IOException {
		return this.stream.readInt();
	}

	private float getNextFloat() throws IOException {
		return this.stream.readFloat();
	}

	private long getNextLong() throws IOException {
		return this.stream.readLong();
	}

	private double getNextDouble() throws IOException {
		return this.stream.readDouble();
	}

	// -----------------------------------------------------------------------------------------------------------------
	// Dedicated part to Magic header and Version
	// -----------------------------------------------------------------------------------------------------------------

	private void checkMagicHeader() throws IOException {
		if (this.getNextInt() != 0xCAFEBABE) {
			throw new IOException("Waiting for 0xCAFEBABE");
		}
	}

	private void getVersion() throws IOException {
		description.setMajorVersion(this.getNextShort());
		description.setMinorVersion(this.getNextShort());
	}

	// -----------------------------------------------------------------------------------------------------------------
	// Dedicated part to Constant Pool
	// -----------------------------------------------------------------------------------------------------------------

	private void getConstantPool() throws IOException {
		final int constantPoolSize = this.getNextShort();
		final Constant[] constants = new Constant[constantPoolSize - 1];
		final ConstantPool pool = new ConstantPool(constants);
		for (int i = 0; i < constants.length; i++) {
			final int nextByte = this.getNextUnsignedByte() - 1;
			final ConstantType constantType = ConstantType.values()[nextByte];
			switch (constantType) {
			case UTF8: {
				final int len = this.getNextShort();
				final char[] chars = getChars(len);
				constants[i] = new UTF8(chars);
				break;
			}
			case INTEGER: {
				constants[i] = new INTEGER(getNextInt());
				break;
			}
			case FLOAT: {
				constants[i] = new FLOAT(getNextFloat());
				break;
			}
			case LONG: {
				constants[i] = new LONG(getNextLong());
				// Slot used by LONG / See Specifications
				constants[++i] = new UNUSED();
				break;
			}
			case DOUBLE: {
				constants[i] = new DOUBLE(getNextDouble());
				// Slot used by LONG / See Specifications
				constants[++i] = new UNUSED();
				break;
			}
			case CLASS_REF: {
				final int reference = getNextShort();
				constants[i] = new CLASS(pool, reference);
				break;
			}
			case STRING: {
				final int reference = getNextShort();
				constants[i] = new STRING(pool, reference);
				break;
			}
			case FIELD_REF: {
				final int reference = getNextShort();
				final int nameAndType = getNextShort();
				constants[i] = new FIELD_REF(pool, reference, nameAndType);
				break;
			}
			case METHOD_REF: {
				final int reference = getNextShort();
				final int nameAndType = getNextShort();
				constants[i] = new METHOD_REF(pool, reference, nameAndType);
				break;
			}
			case INTERFACE_METHOD_REF: {
				final int reference = getNextShort();
				final int nameAndType = getNextShort();
				constants[i] = new INTERFACE_METHOD_REF(pool, reference, nameAndType);
				break;
			}
			case NAME_AND_TYPE: {
				final int reference = getNextShort();
				final int nameAndType = getNextShort();
				constants[i] = new NAME_AND_TYPE(pool, reference, nameAndType);
				break;
			}
			case UNUSED:
				// Not used as mentioned
			default:
				throw new IllegalArgumentException();
			}
		}

		description.setConstantPool(new ConstantPool(constants));
	}

	// -----------------------------------------------------------------------------------------------------------------
	// Dedicated part to Specifications
	// -----------------------------------------------------------------------------------------------------------------

	private void getSpecifications() throws IOException {
		// Access flag
		final int accessFlag = this.getNextShort();
		description.setAccessFlags(accessFlag);

		// This class/interface index name in the CP
		final int classname = this.getNextShort();
		description.setClassName(classname);

		// The super class index name in the CP
		final int superclass = this.getNextShort();
		description.setSuperClassName(superclass);

		// The interfaces index name in the CP
		final int interfaceCount = this.getNextShort();
		final int[] interfaces = new int[interfaceCount];
		for (int i = 0; i < interfaceCount; i++) {
			interfaces[i] = this.getNextShort();
		}

		description.setInterfacesName(interfaces);
	}

	// -----------------------------------------------------------------------------------------------------------------
	// Dedicated part to Fields
	// -----------------------------------------------------------------------------------------------------------------

	private void getFields() throws IOException {
		final int fieldsCount = this.getNextShort();
		final ClassField[] fields = new ClassField[fieldsCount];

		for (int i = 0; i < fieldsCount; i++) {
			fields[i] = this.getField();
		}

		this.description.setFields(fields);
	}

	private ClassField getField() throws IOException {
		// Access flag
		final int accessFlag = this.getNextShort();

		// Field name index
		final int nameIndex = this.getNextShort();

		// Field name index
		final int descriptorIndex = this.getNextShort();

		// Attributes
		final ClassAttribute[] attributes = this.getInnerAttributes();

		return new ClassField(accessFlag, this.description.getConstantPool(), nameIndex, descriptorIndex, attributes);
	}

	// -----------------------------------------------------------------------------------------------------------------
	// Dedicated part to Methods
	// -----------------------------------------------------------------------------------------------------------------

	private void getMethods() throws IOException {
		final int methodsCount = this.getNextShort();
		final ClassMethod[] methods = new ClassMethod[methodsCount];

		for (int i = 0; i < methodsCount; i++) {
			methods[i] = this.getMethod();
		}

		this.description.setMethods(methods);
	}

	private ClassMethod getMethod() throws IOException {
		// Access flag
		final int accessFlag = this.getNextShort();

		// Field name index
		final int nameIndex = this.getNextShort();

		// Field name index
		final int descriptorIndex = this.getNextShort();

		// Attributes
		final ClassAttribute[] attributes = this.getInnerAttributes();

		return new ClassMethod(accessFlag, description.getConstantPool(), nameIndex, descriptorIndex, attributes);
	}

	// -----------------------------------------------------------------------------------------------------------------
	// Dedicated part to Attributes
	// -----------------------------------------------------------------------------------------------------------------

	private void getAttributes() throws IOException {
		this.description.setAttributes(getInnerAttributes());
	}

	// -----------------------------------------------------------------------------------------------------------------

	private ClassAttribute[] getInnerAttributes() throws IOException {
		// Field name index
		final int attributesCount = this.getNextShort();

		// Attributes
		final ClassAttribute[] attributes = new ClassAttribute[attributesCount];
		for (int i = 0; i < attributesCount; i++) {
			attributes[i] = this.getInnerAttribute();
		}

		return attributes;
	}

	private ClassAttribute getInnerAttribute() throws IOException {
		// Field name index
		final int nameIndex = this.getNextShort();

		// Attribute length
		final int attributeLength = this.getNextInt();

		// Attribute value
		final byte[] attributeValue = this.getNextBytes(attributeLength);

		final String name = this.description.getConstantPool().getAt(nameIndex).toExternal();
		switch (AttributeType.get(name)) {
		case CODE:
			return new ClassAttribute.Generic(this.description.getConstantPool(), nameIndex, attributeValue);
		case GENERIC:
			return new ClassAttribute.Generic(this.description.getConstantPool(), nameIndex, attributeValue);
		case SIGNATURE:
			return new ClassAttribute.Signature(this.description.getConstantPool(), nameIndex, attributeValue);
		case SOURCEFILE:
			return new ClassAttribute.SourceFile(this.description.getConstantPool(), nameIndex, attributeValue);
		default:
			return new ClassAttribute.Generic(this.description.getConstantPool(), nameIndex, attributeValue);
		}
	}

	// -----------------------------------------------------------------------------------------------------------------

	public static void main(String[] args) throws IOException {
		final String file = args[0];
		final FileInputStream inputStream = new FileInputStream(file);
		final ClassDescription description = ClassReader.getClassDescription(inputStream);
		description.dump(System.out);
	}
}
