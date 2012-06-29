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

package org.wolfgang.contrail.network.event;

import java.io.Serializable;

import org.wolfgang.contrail.network.reference.DirectReference;
import org.wolfgang.contrail.network.reference.IndirectReference;
import org.wolfgang.contrail.network.reference.ReferenceFactory;

/**
 * <code>NetworkEventImpl</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class NetworkEventImpl implements NetworkEvent, Serializable {

	/**
	 * The serialVersionUID attribute
	 */
	private static final long serialVersionUID = 1887763768371792297L;

	/**
	 * The last component which sent the event
	 */
	public DirectReference lastSender;

	/**
	 * The source
	 */
	private final IndirectReference source;

	/**
	 * The target
	 */
	private final IndirectReference target;

	/**
	 * The content
	 */
	private final Serializable content;

	/**
	 * Constructor
	 * 
	 * @param source
	 * @param target
	 * @param content
	 */
	public NetworkEventImpl(DirectReference target, Serializable content) {
		super();
		this.source = ReferenceFactory.emptyIndirectReference();
		this.target = ReferenceFactory.emptyIndirectReference().add(target);
		this.content = content;
		this.lastSender = null;
	}

	@Override
	public IndirectReference getReferenceToDestination() {
		return this.target;
	}

	@Override
	public IndirectReference getReferenceToSource() {
		return this.source;
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
	public NetworkEvent sentBy(DirectReference reference) {
		this.lastSender = reference;
		return this;
	}
}
