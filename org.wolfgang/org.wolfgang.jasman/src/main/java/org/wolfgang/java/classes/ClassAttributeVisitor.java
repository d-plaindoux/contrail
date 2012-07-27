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

import org.wolfgang.java.classes.ClassAttribute.Code;
import org.wolfgang.java.classes.ClassAttribute.Generic;
import org.wolfgang.java.classes.ClassAttribute.Signature;
import org.wolfgang.java.classes.ClassAttribute.SourceFile;
import org.wolfgang.java.classes.ClassAttribute.VisibleAnnotations;
import org.wolfgang.java.classes.ClassAttribute.VisibleParametersAnnotations;

/**
 * <code>AnnotationFilter</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public interface ClassAttributeVisitor<E> {

	/**
	 * @param attribute
	 * @return
	 */
	E visit(VisibleAnnotations attribute);

	/**
	 * @param attribute
	 * @return
	 */
	E visit(VisibleParametersAnnotations attribute);

	/**
	 * @param attribute
	 * @return
	 */
	E visit(Code attribute);

	/**
	 * @param attribute
	 * @return
	 */
	E visit(Generic attribute);

	/**
	 * @param attribute
	 * @return
	 */
	E visit(Signature attribute);

	/**
	 * @param attribute
	 * @return
	 */
	E visit(SourceFile attribute);
}
