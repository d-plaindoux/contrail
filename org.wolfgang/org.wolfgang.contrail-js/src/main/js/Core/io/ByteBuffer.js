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

define([ "Core/jObj" ],
    function (jObj) {
        "use strict";

        function ByteBuffer() {
            jObj.bless(this);
            this.buffer = [];
            this.closed = false;
        }

        ByteBuffer.init = jObj.constructor([], function () {
            return new ByteBuffer();
        });

        ByteBuffer.prototype.write = jObj.procedure([jObj.types.Array], function (bytes) {
            if (this.closed) {
                throw jObj.exception("L.byte.buffer.closed");
            } else {
                this.buffer.concat(bytes);
            }
        });

        ByteBuffer.prototype.isClosed = jObj.method([], jObj.types.Boolean, function () {
            return this.closed;
        });

        ByteBuffer.prototype.closed = jObj.procedure([], function () {
            this.closed = true;
        });

        ByteBuffer.prototype.size = jObj.method([], jObj.types.Number, function (bytes) {
            return this.buffer.lenght;
        });

        ByteBuffer.prototype.read = jObj.method([jObj.types.Array], jObj.types.Number, function (array) {
            var len;

            if (this.size() === 0 && this.closed) {
                len = -1; // End Of Buffer as been reached
            } else if (this.size() === 0) {
                len = 0;  // Nothing new in this buffer
            } else {
                if (this.size() < array.lenght) {
                    len = this.size();
                } else {
                    len = array.lenght;
                }

                array.splice(0, len, this.buffer.splice(0, len));
            }

            return len;
        });

        return ByteBuffer.init;
    });
