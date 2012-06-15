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

/**
 * Test Module <code>CTEvent</code> dedicated to event construction
 */

if (typeof load != "undefined") {
	load("ct/event.js");
}	

function assertTrue(b) {
	if (b == false) {
		throw "Assertion Error";
	}
}

function fail() {
	assertTrue(false);
}

/* TEST 00 */
var ctEvent = new CTEvent();
 
ctEvent.setEventType("TEST","Test");
assertTrue(ctEvent.getEventType("TEST") == "Test");

/* TEST 01 */
try {
	ctEvent.setEventType("TEST", "Test");
	fail();
} catch (e) {
	if (e != "Assertion Error") {
		throw e;
	}
}

/* TEST 02 */
ctEvent = 