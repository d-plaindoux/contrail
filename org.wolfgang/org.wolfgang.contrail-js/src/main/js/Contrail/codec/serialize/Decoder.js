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

/*global define:true, require, module*/

if (typeof define !== "function") {
    var define = require("amdefine")(module);
}

define([ "require", "Core/object/jObj", "Core/io/jMarshaller" ],
    function (require, jObj, jMarshaller) {
        "use strict";

        function SerializeDecoder() {
            jObj.bless(this, require("Contrail/codec/jCodec").core.decoder());
            this.buffer = [];
        }

        SerializeDecoder.init = jObj.constructor([],
            function () {
                return new SerializeDecoder();
            });

        SerializeDecoder.prototype.decode = jObj.method([jObj.types.Array], jObj.types.Any,
            function (array) {
                var type, result, length;

                type = array[0];

                if (type === jMarshaller.types.String) {
                    length = jMarshaller.bytesToNumberWithOffset(array, 1);
                    result = jMarshaller.bytesToStringWithOffset(array, 1 + jMarshaller.sizeOf.Number, length);
                } else if (type === jMarshaller.types.Number) {
                    result = jMarshaller.bytesToNumberWithOffset(array, 1);
                } else if (type === jMarshaller.types.Undefined) {
                    result = undefined;
                } else if (type === jMarshaller.types.BooleanTrue) {
                    result = true;
                } else if (type === jMarshaller.types.BooleanFalse) {
                    result = false;
                } else if (type === jMarshaller.types.Array) {
                    length = jMarshaller.bytesToNumberWithOffset(array, 1);
                    result = [];
                } else {
                    throw jObj.exception("L.not.yet.implemented");
                }

                return result;
            });

        SerializeDecoder.prototype.transform = jObj.method([jObj.types.Array], jObj.types.Array,
            function (array) {
                return [ this.decode(array) ];
            });

        SerializeDecoder.prototype.finish = jObj.method([], jObj.types.Array,
            function () {
                return [];
            });

        return SerializeDecoder.init;
    });