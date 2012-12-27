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
    function (require, jObj, Marshaller) {
        "use strict";

        function SerializeDecoder() {
            jObj.bless(this, require("Contrail/codec").core.decoder());
            this.buffer = [];
        }

        SerializeDecoder.init = jObj.constructor([],
            function () {
                return new SerializeDecoder();
            });

        SerializeDecoder.prototype.transform = jObj.method([jObj.types.Array], jObj.types.Array,
            function (array) {
                var type, bytes = array, result;

                type = array[0];
                bytes.splice(0, 1);

                if (type === Marshaller.types.String) {
                    result = Marshaller.bytesToString(bytes);
                } else if (type === Marshaller.types.Number) {
                    result = Marshaller.bytesToNumber(bytes);
                } else if (type === Marshaller.types.Undefined) {
                    result = undefined;
                } else if (type === Marshaller.types.BooleanTrue) {
                    result = true;
                } else if (type === Marshaller.types.BooleanFalse) {
                    result = false;
                } else {
                    throw jObj.exception("L.not.yet.implemented");
                }

                return [ result ];
            });

        SerializeDecoder.prototype.finish = jObj.method([], jObj.types.Array,
            function () {
                return [];
            });

        return SerializeDecoder.init;
    });