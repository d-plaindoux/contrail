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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import junit.framework.TestCase;

import org.junit.Test;
import org.wolfgang.common.utils.Coercion;
import org.wolfgang.java.classes.ClassAttribute.VisibleAnnotations;
import org.wolfgang.java.classes.ClassAttribute.VisibleParametersAnnotations;

/**
 * <code>TestClassReader</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@XmlRootElement(name = "root")
@Deprecated
public class TestClassReader extends TestCase {

	@XmlAttribute(name = "toto", namespace = "titi")
	public int f(int j, @Deprecated int i) {
		return 1;
	}

	@Test
	public void testAnnotations01() throws IOException {
		final ClassLoader classLoader = this.getClass().getClassLoader();
		final URL resource = classLoader.getResource("org/wolfgang/java/classes/SampleClass.class");

		assertNotNull(resource);

		final InputStream openStream = resource.openStream();

		Annotation deprecatedClass = null;
		Annotation xmlClassElement = null;
		Annotation xmlMethodAttribute = null;
		Annotation deprecatedParameter = null;

		try {
			final ClassDescription classDescription = ClassReader.getClassDescription(openStream);
			for (ClassAttribute classAttribute : classDescription.getAttributes()) {
				if (Coercion.canCoerce(classAttribute, VisibleAnnotations.class)) {
					final VisibleAnnotations annotations = Coercion.coerce(classAttribute, VisibleAnnotations.class);
					deprecatedClass = annotations.searchByType(Deprecated.class.getCanonicalName());
					xmlClassElement = annotations.searchByType(XmlRootElement.class.getCanonicalName());
				}
			}

			for (ClassMethod classMethod : classDescription.getMethods()) {
				for (ClassAttribute classAttribute : classMethod.getAttributes()) {
					if (Coercion.canCoerce(classAttribute, VisibleAnnotations.class)) {
						final VisibleAnnotations annotations = Coercion.coerce(classAttribute, VisibleAnnotations.class);
						xmlMethodAttribute = annotations.searchByType(XmlAttribute.class.getCanonicalName());
					} else if (Coercion.canCoerce(classAttribute, VisibleParametersAnnotations.class)) {
						final VisibleParametersAnnotations annotations = Coercion.coerce(classAttribute, VisibleParametersAnnotations.class);
						deprecatedParameter = annotations.searchByType(1, Deprecated.class.getCanonicalName());
					}
				}
			}

			assertNotNull(deprecatedClass);
			assertNotNull(xmlClassElement);

			assertNotNull(xmlClassElement.findByName("name"));

			assertNotNull(deprecatedParameter);
			assertNotNull(xmlMethodAttribute);

			assertNotNull(xmlMethodAttribute.findByName("name"));
			assertNotNull(xmlMethodAttribute.findByName("namespace"));
		} finally {
			openStream.close();
		}
	}
}
