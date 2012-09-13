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

package org.wolfgang.contrail.component.router;

import java.util.Arrays;
import java.util.List;

import org.wolfgang.contrail.component.station.CannotAcceptDataException;
import org.wolfgang.contrail.component.station.IDataStreamHandler;
import org.wolfgang.contrail.event.Event;
import org.wolfgang.contrail.reference.DirectReference;

/**
 * <code>EventDataStreanHandler</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class EventDataStreamHandler implements IDataStreamHandler<Event> {

	private final DirectReference reference;
	private final List<DirectReference> references;

	/**
	 * Constructor
	 * 
	 * @param reference
	 */
	public EventDataStreamHandler(DirectReference reference, DirectReference... references) {
		super();
		this.reference = reference;
		this.references = Arrays.asList(references);
	}

	public boolean canAccept(Event data) {
		final DirectReference destination = data.getReferenceToDestination().getCurrent();
		return reference.equals(destination) || references.contains(destination);
	}

	@Override
	public Event accept(Event data) throws CannotAcceptDataException {
		if (canAccept(data)) {
			final DirectReference destination = data.getReferenceToDestination().getCurrent();
			if (reference.equals(destination)) {
				data.getReferenceToDestination().removeCurrent();
			}
			return data;
		} else {
			throw new CannotAcceptDataException();
		}
	}

}
