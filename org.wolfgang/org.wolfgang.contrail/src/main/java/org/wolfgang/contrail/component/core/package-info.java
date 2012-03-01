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
 * Package providing generic component implementations. For this purpose an initial component
 * and a terminal component in order to start and finish a given component connection. These 
 * components therefore exhibits a given interface for the data  management 
 * {@link org.wolfgang.contrail.component.core.DataSender} and requires a given a receiver factory
 * for the construction of a {@link org.wolfgang.contrail.component.core.DataReceiver} which 
 * is able to manage data managed by the network components.
 *
 * @author Didier Plaindoux
 * @version 1.0
 */
package org.wolfgang.contrail.component.core;