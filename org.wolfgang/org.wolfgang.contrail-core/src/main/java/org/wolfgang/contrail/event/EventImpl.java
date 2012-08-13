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
public class EventImpl implements Event, Serializable {

	/**
	 * The serialVersionUID attribute
	 */
	private static final long serialVersionUID = 1887763768371792297L;

	/**
	 * The last component which sent the event
	 */
	public DirectReference lastSender;

	/**
	 * The target
	 */
	private final IndirectReference destination;

	/**
	 * The content
	 */
	private final Serializable content;

	/**
	 * Constructor
	 * 
	 * @param source
	 * @param destination
	 * @param content
	 */
	public EventImpl(Serializable content, DirectReference... targets) {
		super();
		this.destination = ReferenceFactory.indirectReference();
		for (int i = targets.length; i > 0; i--) {
			this.destination.addFirst(targets[i - 1]);
		}
		this.content = content;
		this.lastSender = null;
	}

	@Override
	public IndirectReference getReferenceToDestination() {
		return this.destination;
	}

	@Override
	public Serializable getContent() {
		return this.content;
	}

	@Override
	public DirectReference getSender() {
		return lastSender;
	}

	@Override
	public Event sentBy(DirectReference reference) {
		this.lastSender = reference;
		return this;
	}

	@Override
	public String toString() {
		return "Event [destination=" + destination + "]";
	}

	@Override
	public void handledBy(DirectReference sender) {
		// Nothing
	}
}
