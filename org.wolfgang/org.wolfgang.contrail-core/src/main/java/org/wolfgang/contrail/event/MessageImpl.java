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

package org.wolfgang.contrail.event;

import java.io.Serializable;

import org.wolfgang.contrail.reference.DirectReference;
import org.wolfgang.contrail.reference.IndirectReference;
import org.wolfgang.contrail.reference.ReferenceFactory;

/**
 * <code>NetworkEventImpl</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class MessageImpl extends EventImpl implements Message {

	/**
	 * The serialVersionUID attribute
	 */
	private static final long serialVersionUID = -3548456774165827840L;

	/**
	 * The source
	 */
	private final IndirectReference source;

	/**
	 * Constructor
	 * 
	 * @param source
	 * @param destination
	 * @param content
	 */
	public MessageImpl(Serializable content, DirectReference... targets) {
		super(content, targets);
		this.source = ReferenceFactory.indirectReference();
	}

	@Override
	public IndirectReference getReferenceToSource() {
		return this.source;
	}

	@Override
	public String toString() {
		return "Message [source=" + source + "], " + super.toString();
	}

	@Override
	public void handledBy(DirectReference sender) {
		this.source.addFirst(sender);
	}
}
