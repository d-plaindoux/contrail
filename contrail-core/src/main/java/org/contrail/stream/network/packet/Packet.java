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

package org.contrail.stream.network.packet;

import org.contrail.stream.data.JSonifier;

/**
 * <code>Packet</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class Packet {

	private final String destinationId;
	private String sourceId;
	private Object data;

	public static JSonifier jSonifable() {
		return JSonifier.nameAndType("Packet", Packet.class.getName()).withKeys("sourceId", "destinationId", "data").withTypes(String.class, String.class, Object.class);
	}

	public Packet(String sourceId, String destinationId, Object data) {
		super();
		this.sourceId = sourceId;
		this.destinationId = destinationId;
		this.data = data;
	}

	public Packet(String destinationId, Object data) {
		this(null, destinationId, data);
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getDestinationId() {
		return destinationId;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
