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

/*global define, require*/

define([ "require", "Core/object/jObj" ],
    function (require, jObj) {
        "use strict";

        function ObjectDecoder(drivers) {
            jObj.bless(this, require("Contrail/codec/jCodec").core.decoder());

            this.drivers = drivers;
        }

        ObjectDecoder.init = jObj.constructor([jObj.types.Object],
            function (drivers) {
                var key;
                for (key in drivers) {
                    if (drivers.hasOwnProperty(key) && !drivers[key].hasOwnProperty("toObject")) {
                        jObj.throwError(jObj.exception("L.encoding.to.object.undefined"));
                    }
                }

                return new ObjectDecoder(drivers);
            });

        ObjectDecoder.prototype.toObject = jObj.method([jObj.types.Any], jObj.types.Any,
            function (data) {
                var result, key, self = this;
                if (jObj.ofType(data, jObj.types.ObjectOf({jN:jObj.types.String, jV:jObj.types.Object}))) {
                    if (this.drivers[data.jN]) {
                        result = this.drivers[data.jN].toObject(data.jV, function (value) {
                            return self.toObject(value);
                        });
                    } else {
                        jObj.throwError(jObj.exception("L.deserialization.driver.not.found"));
                    }
                } else if (jObj.ofType(data, jObj.types.Object)) {
                    result = {};
                    for (key in data) {
                        if (data.hasOwnProperty(key)) {
                            result[key] = self.toObject(data[key]);
                        }
                    }
                } else if (jObj.ofType(data, jObj.types.Array)) {
                    result = [];
                    data.forEach(function (value) {
                        result.push(self.toObject(value));
                    });
                } else {
                    result = data;
                }

                return result;
            });

        ObjectDecoder.prototype.transform = jObj.method([jObj.types.Any], jObj.types.Array,
            function (data) {
                return [ this.toObject(data) ];
            });

        ObjectDecoder.prototype.finish = jObj.method([], jObj.types.Array,
            function () {
                return [];
            });

        return ObjectDecoder.init;
    });