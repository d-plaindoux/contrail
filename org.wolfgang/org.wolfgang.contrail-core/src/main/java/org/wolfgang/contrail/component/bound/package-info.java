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
 * Package providing generic component implementations for initial and terminal
 * components dedicated to frontier link management. External interaction is therefore 
 * done using {@link org.wolfgang.contrail.handler.UpStreamDataHandler} and 
 * {@link org.wolfgang.contrail.handler.DownStreamDataHandler} implementations. The 
 * initial is used as the first component able to receive and manage upstream
 * data i.e. from low level software layer and the terminal is dedicated to 
 * send data to the application layer.
 *
 * @author Didier Plaindoux
 * @version 1.0
 */
package org.wolfgang.contrail.component.bound;