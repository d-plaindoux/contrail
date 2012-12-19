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

define([ "require", "Core/jObj", "Core/io/jMarshaller" ],
    function (require, jObj, Marshaller) {
        "use strict";

        function SerializeEncoder() {
            jObj.bless(this, require("Contrail/codec").core.encoder());
        }

        SerializeEncoder.init = jObj.constructor([],
            function () {
                return new SerializeEncoder();
            });

        SerializeEncoder.prototype.transform = jObj.method([jObj.types.Any], jObj.types.Array,
            function (value) {
                var type, result;

                if (jObj.ofType(value, jObj.types.Number)) {
                    type = Marshaller.types.Number;
                    result = Marshaller.numberToBytes(value);
                } else if (jObj.ofType(value, jObj.types.String)) {
                    type = Marshaller.types.String;
                    result = Marshaller.stringToBytes(value);
                } else if (jObj.ofType(value, jObj.types.Undefined)) {
                    type = Marshaller.types.Undefined;
                    result = [];
                } else if (jObj.ofType(value, jObj.types.Boolean)) {
                    if (value) {
                        type = Marshaller.types.BooleanTrue;
                    } else {
                        type = Marshaller.types.BooleanFalse;
                    }
                    result = [];
                } else {
                    throw jObj.exception("L.not.yet.implemented");
                }

                return [ [type].concat(result) ];
            });

        SerializeEncoder.prototype.finish = jObj.method([], jObj.types.Array,
            function () {
                return [];
            });

        return SerializeEncoder.init;
    });