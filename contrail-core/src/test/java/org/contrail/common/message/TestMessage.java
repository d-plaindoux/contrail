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

package org.contrail.common.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * <code>TestMessage</code>
 * 
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestMessage {

	@Test
	public void GivenAValidMessageEntryReturnTheCorrectMessage() throws Exception {
		try {
			final Message message = MessagesProvider.from(this).get("org/contrail/common/message", "message");
			final String text = message.format();
			assertEquals("This is a test message", text);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void GivenAValidMessageEntryWithArgumentsReturnTheCorrectMessage() throws Exception {
		try {
			final Message message = MessagesProvider.from(this).get("org/contrail/common/message", "message.with.args");
			final String text = message.format("'Hello, World!'");
			assertEquals("This is a 'Hello, World!'", text);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void GivenAMessageEntryInAnInvalidMessageProviderReturnTheDefaultErrorMessage() throws Exception {
		try {
			final Message message = MessagesProvider.from(this).get("org/common/undef", "message.undefined");
			final String text = message.format("'Hello, World!'");
			assertEquals("message bundle not found for [org/common/undef]", text);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void GivenAnInvalidMessageEntryInAValidMessageProviderReturnTheDefaultErrorMessage() throws Exception {
		try {
			final Message message = MessagesProvider.from(this).get("org/contrail/common/message", "message.undefined");
			final String text = message.format("'Hello, World!'");
			assertEquals("message not found in [org/contrail/common/message] for [message.undefined]", text);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
