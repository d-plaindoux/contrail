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
import java.io.ByteArrayInputStream;
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

	private final ClassDescriptionImpl description;

	private ClassReader(InputStream in) throws IOException {
		final StreamReader stream = new StreamReader(in);
		this.description = new ClassDescriptionImpl();
		this.checkMagicHeader(stream);
		this.getVersion(stream);
		this.getConstantPool(stream);
		this.getSpecifications(stream);
		this.getFields(stream);
		this.getMethods(stream);
		this.getAttributes(stream);
	}

	public ClassDescription getDescription() {
		return description;
	}

	// -----------------------------------------------------------------------------------------------------------------
	// Dedicated part to Magic header and Version
	// -----------------------------------------------------------------------------------------------------------------

	private void checkMagicHeader(StreamReader stream) throws IOException {
		if (stream.getNextInt() != 0xCAFEBABE) {
			throw new IOException("Waiting for 0xCAFEBABE");
		}
	}

	private void getVersion(StreamReader stream) throws IOException {
		description.setMajorVersion(stream.getNextShort());
		description.setMinorVersion(stream.getNextShort());
	}

	// -----------------------------------------------------------------------------------------------------------------
	// Dedicated part to Constant Pool
	// -----------------------------------------------------------------------------------------------------------------

	private void getConstantPool(StreamReader stream) throws IOException {
		final int constantPoolSize = stream.getNextShort();
		final Constant[] constants = new Constant[constantPoolSize - 1];
		final ConstantPool pool = new ConstantPoolImpl(constants);
		for (int i = 0; i < constants.length; i++) {
			final int nextByte = stream.getNextUnsignedByte() - 1;
			final ConstantType constantType = ConstantType.values()[nextByte];
			switch (constantType) {
			case UTF8: {
				final int len = stream.getNextShort();
				final char[] chars = stream.getChars(len);
				constants[i] = new UTF8(chars);
				break;
			}
			case INTEGER: {
				constants[i] = new INTEGER(stream.getNextInt());
				break;
			}
			case FLOAT: {
				constants[i] = new FLOAT(stream.getNextFloat());
				break;
			}
			case LONG: {
				constants[i] = new LONG(stream.getNextLong());
				// Slot used by LONG / See Specifications
				constants[++i] = new UNUSED();
				break;
			}
			case DOUBLE: {
				constants[i] = new DOUBLE(stream.getNextDouble());
				// Slot used by LONG / See Specifications
				constants[++i] = new UNUSED();
				break;
			}
			case CLASS_REF: {
				final int reference = stream.getNextShort();
				constants[i] = new CLASS(pool, reference);
				break;
			}
			case STRING: {
				final int reference = stream.getNextShort();
				constants[i] = new STRING(pool, reference);
				break;
			}
			case FIELD_REF: {
				final int reference = stream.getNextShort();
				final int nameAndType = stream.getNextShort();
				constants[i] = new FIELD_REF(pool, reference, nameAndType);
				break;
			}
			case METHOD_REF: {
				final int reference = stream.getNextShort();
				final int nameAndType = stream.getNextShort();
				constants[i] = new METHOD_REF(pool, reference, nameAndType);
				break;
			}
			case INTERFACE_METHOD_REF: {
				final int reference = stream.getNextShort();
				final int nameAndType = stream.getNextShort();
				constants[i] = new INTERFACE_METHOD_REF(pool, reference, nameAndType);
				break;
			}
			case NAME_AND_TYPE: {
				final int reference = stream.getNextShort();
				final int nameAndType = stream.getNextShort();
				constants[i] = new NAME_AND_TYPE(pool, reference, nameAndType);
				break;
			}
			case UNUSED:
				// Not used as mentioned
			default:
				throw new IllegalArgumentException();
			}
		}

		description.setConstantPool(new ConstantPoolImpl(constants));
	}

	// -----------------------------------------------------------------------------------------------------------------
	// Dedicated part to Specifications
	// -----------------------------------------------------------------------------------------------------------------

	private void getSpecifications(StreamReader stream) throws IOException {
		// Access flag
		final int accessFlag = stream.getNextShort();
		description.setAccessFlags(accessFlag);

		// This class/interface index name in the CP
		final int classname = stream.getNextShort();
		description.setClassName(classname);

		// The super class index name in the CP
		final int superclass = stream.getNextShort();
		description.setSuperClassName(superclass);

		// The interfaces index name in the CP
		final int interfaceCount = stream.getNextShort();
		final int[] interfaces = new int[interfaceCount];
		for (int i = 0; i < interfaceCount; i++) {
			interfaces[i] = stream.getNextShort();
		}

		description.setInterfacesName(interfaces);
	}

	// -----------------------------------------------------------------------------------------------------------------
	// Dedicated part to Fields
	// -----------------------------------------------------------------------------------------------------------------

	private void getFields(StreamReader stream) throws IOException {
		final int fieldsCount = stream.getNextShort();
		final ClassField[] fields = new ClassField[fieldsCount];

		for (int i = 0; i < fieldsCount; i++) {
			fields[i] = this.getField(stream);
		}

		this.description.setFields(fields);
	}

	private ClassField getField(StreamReader stream) throws IOException {
		// Access flag
		final int accessFlag = stream.getNextShort();

		// Field name index
		final int nameIndex = stream.getNextShort();

		// Field name index
		final int descriptorIndex = stream.getNextShort();

		// Attributes
		final ClassAttribute[] attributes = this.getInnerAttributes(stream);

		return new ClassFieldImpl(accessFlag, this.description.getConstantPool(), nameIndex, descriptorIndex, attributes);
	}

	// -----------------------------------------------------------------------------------------------------------------
	// Dedicated part to Methods
	// -----------------------------------------------------------------------------------------------------------------

	private void getMethods(StreamReader stream) throws IOException {
		final int methodsCount = stream.getNextShort();
		final ClassMethod[] methods = new ClassMethod[methodsCount];

		for (int i = 0; i < methodsCount; i++) {
			methods[i] = this.getMethod(stream);
		}

		this.description.setMethods(methods);
	}

	private ClassMethod getMethod(StreamReader stream) throws IOException {
		// Access flag
		final int accessFlag = stream.getNextShort();

		// Field name index
		final int nameIndex = stream.getNextShort();

		// Field name index
		final int descriptorIndex = stream.getNextShort();

		// Attributes
		final ClassAttribute[] attributes = this.getInnerAttributes(stream);

		return new ClassMethodImpl(accessFlag, description.getConstantPool(), nameIndex, descriptorIndex, attributes);
	}

	// -----------------------------------------------------------------------------------------------------------------
	// Dedicated part to Attributes
	// -----------------------------------------------------------------------------------------------------------------

	private void getAttributes(StreamReader stream) throws IOException {
		this.description.setAttributes(getInnerAttributes(stream));
	}

	private ClassAttribute[] getInnerAttributes(StreamReader stream) throws IOException {
		// Field name index
		final int attributesCount = stream.getNextShort();

		// Attributes
		final ClassAttribute[] attributes = new ClassAttribute[attributesCount];
		for (int i = 0; i < attributesCount; i++) {
			attributes[i] = this.getInnerAttribute(stream);
		}

		return attributes;
	}

	private ClassAttribute getInnerAttribute(StreamReader stream) throws IOException {
		// Field name index
		final int nameIndex = stream.getNextShort();

		// Attribute length
		final int attributeLength = stream.getNextInt();

		// Attribute value
		final String name = this.description.getConstantPool().getAt(nameIndex).toExternal();
		final byte[] attributeValue = stream.getNextBytes(attributeLength);
		final StreamReader valueReader = new StreamReader(new ByteArrayInputStream(attributeValue));

		try {
			switch (AttributeType.get(name)) {
			case CODE:
				return new ClassAttribute.Generic(this.description.getConstantPool(), nameIndex, attributeValue);
			case GENERIC:
				return new ClassAttribute.Generic(this.description.getConstantPool(), nameIndex, attributeValue);
			case SIGNATURE:
				return new ClassAttribute.Signature(this.description.getConstantPool(), nameIndex, valueReader.getNextShort());
			case SOURCEFILE:
				return new ClassAttribute.SourceFile(this.description.getConstantPool(), nameIndex, valueReader.getNextShort());
			case ANNOTATION:
				// Read Annotations now
				// return new ClassAttribute.VisibleAnnotations(this.description.getConstantPool(), nameIndex, getAnnotations(valueReader));
			case PARAMETER_ANNOTATION:
				// Read Parameter Annotations now
				// return new ClassAttribute.VisibleParametersAnnotations(this.description.getConstantPool(), nameIndex, getParametersAnnotations(valueReader));
			default:
				return new ClassAttribute.Generic(this.description.getConstantPool(), nameIndex, attributeValue);
			}
		} finally {
			valueReader.close();
		}
	}

	// -----------------------------------------------------------------------------------------------------------------
	// Annotations readers
	// -----------------------------------------------------------------------------------------------------------------

	private Annotation[][] getParametersAnnotations(StreamReader stream) throws IOException {
		final int len = stream.getNextUnsignedByte();
		final Annotation[][] parameters = new Annotation[len][];
		for (int i = 0; i < len; i++) {
			parameters[i] = getAnnotations(stream);
		}
		return parameters;
	}

	private Annotation[] getAnnotations(StreamReader stream) throws IOException {
		final int len = stream.getNextShort();
		final Annotation[] annotations = new Annotation[len];

		for (int i = 0; i < len; i++) {
			annotations[i] = getAnnotation(stream);
		}

		return annotations;
	}

	private Annotation getAnnotation(StreamReader stream) throws IOException {
		final int type = stream.getNextShort();
		final int len = stream.getNextShort();
		final AnnotationElement[] parameters = new AnnotationElement[len];

		for (int i = 0; i < len; i++) {
			parameters[i] = getAnnotationElement(stream);
		}

		return new AnnotationImpl(this.description.getConstantPool(), type, parameters);
	}

	private AnnotationElement getAnnotationElement(StreamReader stream) throws IOException {
		final int elementName = stream.getNextShort();
		final AnnotationElementValue value = getAnnotationElementValue(stream);
		return new AnnotationElementImpl(this.description.getConstantPool(), elementName, value);
	}

	private AnnotationElementValue getAnnotationElementValue(StreamReader stream) throws IOException {
		switch (stream.getNextUnsignedByte()) {
		case 'B':
		case 'C':
		case 'D':
		case 'F':
		case 'I':
		case 'J':
		case 'S':
		case 'Z':
		case 's': {
			int value = stream.getNextShort();
			return new AnnotationElementValue.ObjectValue(this.description.getConstantPool(), value);
		}
		case 'e': {
			int enumType = stream.getNextShort();
			int enumName = stream.getNextShort();
			return new AnnotationElementValue.EnumValue(this.description.getConstantPool(), enumType, enumName);
		}
		case 'c': {
			int type = stream.getNextShort();
			return new AnnotationElementValue.ClassValue(this.description.getConstantPool(), type);
		}
		case '@': {
			final Annotation annotation = getAnnotation(stream);
			return new AnnotationElementValue.AnnotationValue(annotation);
		}
		case '[': {
			final int len = stream.getNextShort();
			final AnnotationElementValue[] values = new AnnotationElementValue[len];
			for (int i = 0; i < len; i++) {
				values[i] = new AnnotationElementValue.AnnotationValue(getAnnotation(stream));
			}
			return new AnnotationElementValue.ArrayValue(values);
		}
		default:
			throw new IllegalArgumentException();
		}
	}
}
