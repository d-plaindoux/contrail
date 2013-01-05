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

        function SerializeEncoder() {
            jObj.bless(this, require("Contrail/codec/jCodec").core.encoder());
        }

        SerializeEncoder.init = jObj.constructor([],
            function () {
                return new SerializeEncoder();
            });

        SerializeEncoder.prototype.encode = jObj.method([jObj.types.Any], jObj.types.Array,
            function (value) {
                var type, i, result = [], keys;

                if (jObj.ofType(value, jObj.types.Number)) {
                    type = jMarshaller.types.Number;
                    result = jMarshaller.numberToBytes(value);
                } else if (jObj.ofType(value, jObj.types.String)) {
                    type = jMarshaller.types.String;
                    result = jMarshaller.shortNumberToBytes(value.length);
                    result = result.concat(jMarshaller.stringToBytes(value));
                } else if (jObj.ofType(value, jObj.types.Undefined)) {
                    type = jMarshaller.types.Undefined;
                    result = [];
                } else if (jObj.ofType(value, jObj.types.Boolean)) {
                    if (value) {
                        type = jMarshaller.types.BooleanTrue;
                    } else {
                        type = jMarshaller.types.BooleanFalse;
                    }
                    result = [];
                } else if (jObj.ofType(value, jObj.types.Array)) {
                    type = jMarshaller.types.Array;
                    result = jMarshaller.shortNumberToBytes(value.length);
                    for (i = 0; i < value.length; i += 1) {
                        result = result.concat(this.encode(value[i]));
                    }
                } else if (jObj.ofType(value, jObj.types.Object)) {
                    type = jMarshaller.types.Object;
                    keys = [];
                    Object.keys(value).forEach(function (key) {
                        if (value.hasOwnProperty(key)) {
                            keys.push(key);
                        }
                    });
                    result = jMarshaller.shortNumberToBytes(keys.length);
                    for (i = 0; i < keys.length; i += 1) {
                        result = result.concat(jMarshaller.shortNumberToBytes(keys[i].length));
                        result = result.concat(jMarshaller.stringToBytes(keys[i]));
                        result = result.concat(this.encode(value[keys[i]]));
                    }
                } else {
                    throw jObj.exception("L.not.yet.implemented");
                }

                return [type].concat(result);
            });

        SerializeEncoder.prototype.transform = jObj.method([jObj.types.Any], jObj.types.Array,
            function (value) {
                return [ this.encode(value) ];
            });

        SerializeEncoder.prototype.finish = jObj.method([], jObj.types.Array,
            function () {
                return [];
            });

        return SerializeEncoder.init;
    });