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

define([ "require", "Core/jObj", "IO/jMarshaller" ],
    function (require, jObj, jMarshaller) {
        "use strict";

        function PayloadDecoder() {
            jObj.bless(this, require("Codec/Factory").core.decoder());
            this.buffer = [];
        }

        PayloadDecoder.INT_LEN = 4;

        PayloadDecoder.init = jObj.constructor([], function () {
            return new PayloadDecoder();
        });

        PayloadDecoder.prototype.getNext = jObj.method([], jObj.types.Array, function () {
            var result, payload;

            if (this.buffer.length < PayloadDecoder.INT_LEN) {
                result = [];
            } else {
                payload = jMarshaller.bytesToInt(this.buffer, 0);
                if (this.buffer.length - PayloadDecoder.INT_LEN < payload) {
                    result = [];
                } else {
                    this.buffer.splice(0, PayloadDecoder.INT_LEN);
                    result = this.buffer.splice(0, payload);
                }
            }

            return result;
        });

        PayloadDecoder.prototype.transform = jObj.method([jObj.types.Array], jObj.types.Array, function (bytes) {
            var results = [], result;

            this.buffer = this.buffer.concat(bytes);

            for (result = this.getNext(); result.length > 0; result = this.getNext()) {
                results.push(result);
            }

            return results;
        });

        PayloadDecoder.prototype.finish = jObj.method([], jObj.types.Array, function () {
            if (this.buffer.length > 0) {
                throw jObj.exception("L.array.must.be.empty");
            } else {
                return [];
            }
        });

        return PayloadDecoder.init;
    });