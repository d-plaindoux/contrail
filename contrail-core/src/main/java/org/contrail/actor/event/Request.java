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

package org.contrail.actor.event;

import org.contrail.stream.data.JSonifier;
import org.contrail.stream.data.ObjectRecord;

/**
 * <code>Request</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class Request {

	private final String name;
	private final Object[] parameters;

	public static JSonifier jSonifable() {
		return JSonifier.nameAndType("Request", Request.class.getName()).withKeys("name", "parameters").withTypes(String.class, Object[].class);
	}

	public Request(String name, Object... parameters) {
		super();
		this.name = name;
		this.parameters = parameters;
	}

	public String getName() {
		return name;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public ObjectRecord toActor(String location, String responseId) {
		return new ObjectRecord().set("identifier", location).set("request", this).set("response", responseId);
	}
}
