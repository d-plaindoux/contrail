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

define("Core/jIOUtils", [ "Core/jObj" ], 
function (jObj) {
	var jIO = {};

	jIO.readInt = function (i, bytes) {
		if (bytes.length >= i + 2) {
			return bytes[i] << 8 | bytes[i + 1];
		} else {
			throw jObj.exception("L.array.out.of.bound");
		}
	};

	jIO.pack = function (bytes) {
		var str = "", i;
		for(i = 0; i < bytes.length; i += 2) {
			str += String.fromCharCode(jObj.readInt(i, bytes));
		}
		return str;	
	};

	jIO.unpack = function (str) {
		var bytes = [], char, i;
		for(i = 0; i < str.length; i++) {
			char = str.charCodeAt(i);
			bytes.push(char >>> 8);
			bytes.push(char & 0xFF);
		}
		return bytes;
	};
});
