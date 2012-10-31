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

/*global define*/

define( [ "require", "Core/jObj" ] , 
function(require, jObj) {
    
	function TransducerDataFlow(type, transducer) {
		jObj.bless(this, require("Contrail/Factory").flow.basic(type));
		this.transducer = transducer;
	}

	TransducerDataFlow.init = jObj.constructor([jObj.types.Any, "DataTransducer"], function(type, transducer) {
		return new TransducerDataFlow(type, transducer);
	});

	TransducerDataFlow.prototype.getDataFlow = jObj.method([], "DataFlow");

	TransducerDataFlow.prototype.handleData = jObj.procedure([this.type], function(data) {
		var dataFlow = this.getDataFlow(),
			transformed= this.transducer.transform(data),
			newData;
		
		for(newData in transformed) {
			dataFlow.handleData(newData);
		}
	});

	TransducerDataFlow.prototype.handleClose = jObj.procedure([], function() {
		var dataFlow = this.getDataFlow(),
			transformed= this.transducer.finish(),
			newData;
			
		for(newData in transformed) {
			dataFlow.handleData(newData);
		}
	});

	return TransducerDataFlow;
});