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

package org.wolfgang.common.message;

import java.text.MessageFormat;

/**
 * <code>Message</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public final class Message {

	/**
	 * The unformatted message
	 */
	private final String message;

	/**
	 * Constructor
	 * 
	 * @param message The unformatted message
	 */
	public Message(String message) {
		super();
		this.message = message;
	}

	/**
	 * Method called whether a message must be performed with specifics
	 * arguments
	 * 
	 * @param arguments Message arguments
	 * @return a string
	 */
	public String format(Object... arguments) {
		return MessageFormat.format(message, arguments);
	}
}
