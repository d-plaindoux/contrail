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

package org.wolfgang.common.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <code>Methods</code>
 * 
 * @author Didier Plaindoux
 * @since v1.0
 */
public class MethodUtils {
	/*
	 * Functions for public method determination
	 */

	static private boolean methodEquals(Method m1, Method m2) {
		if (m1.getName().equals(m2.getName()) && m1.getReturnType().equals(m2.getReturnType())) {

			final Class<?>[] t1 = m1.getParameterTypes();
			final Class<?>[] t2 = m2.getParameterTypes();

			int len = t1.length;
			if (len == t2.length) {
				for (int k = 0; k < len; k++) {
					if (!t1[k].equals(t2[k])) {
						return false;
					}
				}
				return true;
			}
		}

		return false;
	}

	static private boolean methodIsIn(Method method, List<Method> methods) {
		for (Method method1 : methods) {
			if (methodEquals(method, method1)) {
				return true;
			}
		}

		return false;
	}

	static private boolean methodIsIn(Method method, Method[] methods) {
		for (Method aMethod : methods) {
			if (methodEquals(method, aMethod)) {
				return true;
			}
		}

		return false;
	}

	static private void getMethods(Class<?> aClass, List<Method> methodList) {
		for (Method method : aClass.getMethods()) {
			if (!method.getName().equals("finalize")) {
				if (!(methodIsIn(method, Object.class.getMethods()) || methodIsIn(method, methodList))) {
					methodList.add(method);
				}
			}
		}

		if (aClass.getSuperclass() != null && !aClass.getSuperclass().equals(Object.class)) {
			getMethods(aClass.getSuperclass(), methodList);
		}
	}

	private static Comparator<Method> comparator = new Comparator<Method>() {
		public int compare(Method o, Method o1) {
			return o.toString().compareTo(o1.toString());
		}
	};

	static public Method[] getPublicMethods(Class aClass) {
		List<Method> methods = new ArrayList<Method>();
		getMethods(aClass, methods);
		Collections.sort(methods, comparator);

		return methods.toArray(new Method[methods.size()]);
	}
}
