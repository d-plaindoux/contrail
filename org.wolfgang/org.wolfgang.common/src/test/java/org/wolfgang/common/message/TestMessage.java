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

import junit.framework.TestCase;

/**
 * 
 * @author Didier Plaindoux
 * @version 1.0
 * 
 */
public class TestMessage extends TestCase {
	
    public void testMessage01() throws Exception {
        try {
            String message = MessagesProvider.get("org/wolfgang/common/message/test", "test.message").format();
            assertEquals("This is a test message", message);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
	
    public void testMessage02() throws Exception {
        try {
            String message = MessagesProvider.get("org/wolfgang/common/message/test", "test.message.with.args").format("'Hello, World!'");
            assertEquals("This is a 'Hello, World!'", message);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
	
}
