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

define([ "require", "Core/object/jObj" ],
    function (require, jObj) {
        "use strict";

        function ObjectEncoder(drivers) {
            jObj.bless(this, require("Contrail/codec/jCodec").core.encoder());

            this.drivers = drivers;
        }

        ObjectEncoder.init = jObj.constructor([jObj.types.Array],
            function (jSonifiers) {
                var drivers = {};

                jSonifiers.forEach(function (jSonifier) {
                    if (jSonifier.hasOwnProperty("jSonifable")) {
                        drivers[jSonifier.jSonifable.type] = jSonifier.jSonifable;
                    } else {
                        jObj.throwError(jObj.exception("L.encoding.to.object.undefined"));
                    }
                });

                return new ObjectEncoder(drivers);
            });

        ObjectEncoder.prototype.toStructure = jObj.method([jObj.types.Any], jObj.types.Any,
            function (data) {
                var result, name, value, key, self = this;

                if (jObj.isAClassInstance(data)) {
                    name = jObj.getClass(data);
                    if (this.drivers[name]) {
                        value = this.drivers[name].toStructure(data, function (object) {
                            return self.toStructure(object);
                        });
                        result = { jN:name, jV:value };
                    } else {
                        jObj.throwError(jObj.exception("L.serialization.driver.not.found"));
                    }
                } else if (jObj.ofType(data, jObj.types.Object)) {
                    result = {};
                    for (key in data) {
                        if (data.hasOwnProperty(key)) {
                            result[key] = self.toStructure(data[key]);
                        }
                    }
                } else if (jObj.ofType(data, jObj.types.Array)) {
                    result = [];
                    data.forEach(function (value) {
                        result.push(self.toStructure(value));
                    });
                } else {
                    result = data;
                }

                return result;
            });

        ObjectEncoder.prototype.encode = jObj.method([jObj.types.Any], jObj.types.Any,
            function (data) {
                return this.toStructure(data);
            });

        ObjectEncoder.prototype.transform = jObj.method([jObj.types.Any], jObj.types.Array,
            function (object) {
                return [ this.toStructure(object) ];
            });

        ObjectEncoder.prototype.finish = jObj.method([], jObj.types.Array,
            function () {
                return [];
            });

        return ObjectEncoder.init;
    });