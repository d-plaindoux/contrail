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

package org.wolfgang.contrail.link;

import junit.framework.TestCase;

import org.wolfgang.contrail.component.ComponentConnectionRejectedException;
import org.wolfgang.contrail.component.ComponentDisconnectionRejectedException;

/**
 * <code>TestComponentLink</code>
 *
 * @author Didier Plaindoux
 * @version 1.0
 */
public class TestComponentLink extends TestCase {
	
	public void testLink01() {
		final ComponentsLinkManager linkManager = new ComponentsLinkManager();
		try {
			linkManager.connect(new DummySourceComponent(), new DummyDestinationComponent());
		} catch (ComponentConnectionRejectedException e) {
			fail(e.getMessage());
		}
	}

	public void testLink02() {
		final ComponentsLinkManager linkManager = new ComponentsLinkManager();
		try {
			final ComponentsLink<Void,Void> connect = linkManager.connect(new DummySourceComponent(), new DummyDestinationComponent());
			try {
				connect.dispose();
			} catch (ComponentDisconnectionRejectedException e) {
				fail(e.getMessage());
			}
		} catch (ComponentConnectionRejectedException e) {
			fail(e.getMessage());
		}
	}

	public void testLink03() {
		final ComponentsLinkManager linkManager = new ComponentsLinkManager();
		try {
			final ComponentsLink<Void,Void> connect = linkManager.connect(new DummySourceComponent(), new DummyDestinationComponent());
			try {
				connect.dispose();
			} catch (ComponentDisconnectionRejectedException e) {
				fail(e.getMessage());
			}
			
			assertEquals(0, linkManager.getEstablishedLinks().length);			
		} catch (ComponentConnectionRejectedException e) {
			fail(e.getMessage());
		}
	}

	public void testLink04() {
		final ComponentsLinkManager linkManager = new ComponentsLinkManager();
		try {
			final ComponentsLink<Void,Void> connect = linkManager.connect(new DummySourceComponent(), new DummyDestinationComponent());
			try {
				connect.dispose();
			} catch (ComponentDisconnectionRejectedException e) {
				fail(e.getMessage());
			}
			try {
				connect.dispose();
				fail();
			} catch (ComponentDisconnectionRejectedException e) {
			}
		} catch (ComponentConnectionRejectedException e) {
			fail(e.getMessage());
		}
	}
}
