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

define( [ "Core/jObj", "IO/jMarshaller" ] , 
function(jObj, jMarshaller) {

	function Decoder() {
		jObj.bless(this);
		this.buffer = [];
	}

	Decoder.prototype.INT_LEN = 4;

	Decoder.init = jObj.constructor([], function () {
		return new Decoder();
	});
	
	Decoder.prototype.getNext = jObj.method([], jObj.types.Array, function() {
		if (this.buffer.length < Decoder.INT_LEN) {
			return undefined;
		} else {
			var payload = jMarshaller.bytesToInt(this.buffer);
			if (this.buffer.length - Decoder.INT_LEN < payload) {
				return undefined;
			} else {
				return this.buffer.slice(Decoder.INT_LEN, payload);
			}
		}
	});

	Decoder.prototype.transform = jObj.method([jObj.types.Array], jObj.types.Array, function(bytes) {
		var results = [], result;
		
		this.buffer = this.buffer.concat(bytes);
		
		for(result = Decoder.getNext(); result !== undefined;result = Decoder.getNext()) {
			results.push(result);
		}
		
		return results;
	});

	Decoder.prototype.finish = jObj.method([], jObj.types.Array, function() {
		if (this.buffer.length > 0) {
			throw jObj.exception("L.array.must.be.empty");
		} else {
			return [];
		}
	});

	return Decoder;
});