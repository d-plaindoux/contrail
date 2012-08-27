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

package org.wolfgang.contrail.ecosystem.lang.delta;

import org.wolfgang.contrail.ecosystem.lang.code.ClosureValue;
import org.wolfgang.contrail.ecosystem.lang.code.CodeValueVisitor;
import org.wolfgang.contrail.ecosystem.lang.code.ComponentValue;
import org.wolfgang.contrail.ecosystem.lang.code.ConstantValue;
import org.wolfgang.contrail.ecosystem.lang.code.FlowValue;

class ParameterCodeConverter implements CodeValueVisitor<Object, Exception> {

	@Override
	public Object visit(ClosureValue value) throws Exception {
		return null;
	}

	@Override
	public Object visit(ComponentValue value) throws Exception {
		return value.getComponent();
	}

	@Override
	public Object visit(ConstantValue value) throws Exception {
		return value.getValue();
	}

	@Override
	public Object visit(FlowValue value) throws Exception {
		return null;
	}
}