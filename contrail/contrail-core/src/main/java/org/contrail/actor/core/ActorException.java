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

package org.contrail.actor.core;

import org.contrail.stream.data.JSonifier;

/**
 * <code>ActorException</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class ActorException extends Exception {

	private static final long serialVersionUID = 6299142557828319229L;

	public static JSonifier jSonifable() {
		return JSonifier.nameAndType("ActorException", ActorException.class.getName()).withKeys("message").withTypes(String.class);
	}

	public ActorException(String message) {
		super(message);
	}

	public ActorException(String message, Throwable cause) {
		super(message, cause);
	}
}
