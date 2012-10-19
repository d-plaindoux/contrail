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

define( [ "require" ], 
function (require) {

	var Strict = {};
    
	function AssertTypeError(message) {
		require("../core/jObj").bless(this);
		this.message = message;
	}
    
	Strict.assertType = function (object, type) {
		var jObj = require("../core/jObj");
        
		if (!jObj.instanceOf(type, "String")) {
			throw new AssertTypeError(type + " must be an instance of String");
		} else if (!jObj.instanceOf(object,type)) {
			throw new AssertTypeError(object + " must be an instance of " + type);
		}
	};
    
	return Strict;
});
