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

define("Core/jIO", [ ], 
function () {
	var jIO = {};

	jIO.pack = function (bytes) {
		var str = "", ch, i;
		for(i = 0; i < bytes.length; i += 2) {
			ch = bytes[i] << 8;
			if (bytes[i + 1]) {
				ch |= bytes[i + 1];
				}
			str += String.fromCharCode(ch);
		}
		return str;	
	};

	jIO.unpack = function (str) {
		var bytes = [], ch, i;
		for(i = 0; i < str.length; i++) {
			ch = str.charCodeAt(i);
			bytes.push(ch >>> 8);
			bytes.push(ch & 0xFF);
		}
		return bytes;
	};
});
