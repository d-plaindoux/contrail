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

import org.wolfgang.common.utils.Coercion;
import org.wolfgang.java.classes.ClassAttribute.VisibleAnnotations;

import junit.framework.TestCase;

/**
 * <code>TestClassReader</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
@XmlRootElement
@Deprecated
public class TestClassReader extends TestCase {

	@XmlAttribute(name = "toto", namespace = "titi")
	public int f(int j, @Deprecated int i) {
		return 1;
	}

	public void testNominal01() throws IOException {
		final ClassLoader classLoader = TestClassReader.class.getClassLoader();
		final URL resource = classLoader.getResource("org/wolfgang/java/classes/TestClassReader.class");

		assertNotNull(resource);

		final InputStream openStream = resource.openStream();

		try {
			final ClassDescription classDescription = ClassReader.getClassDescription(openStream);

			for (ClassAttribute classAttribute : classDescription.getAttributes()) {
				if (Coercion.canCoerce(classAttribute, VisibleAnnotations.class)) {
					final VisibleAnnotations annotations = Coercion.coerce(classAttribute, VisibleAnnotations.class);
					assertNotNull(annotations.searchByType(Deprecated.class.getCanonicalName()));
					assertNotNull(annotations.searchByType(XmlRootElement.class.getCanonicalName()));
				}

			}
		} finally {
			openStream.close();
		}
	}
}
