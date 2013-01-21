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

define("Core/object/jObj", [ "./jModel", "./jType" ],
    function (jModel, jType, jTransducer) {
        "use strict";

        var jObj = {};

        /**
         * Private method for the inheritance mechanism
         *
         * @param instance
         * @param parameters
         * @param getDefinition
         */
        function inheritance(instance, parameters, getDefinition) {
            parameters.forEach(function (inherited) {
                var key, definition = getDefinition(inherited);
                for (key in definition) {
                    if (definition.hasOwnProperty(key) && !instance.hasOwnProperty(key)) {
                        instance[key] = definition[key];
                    }
                }
            });
        }

        /**
         * Method called whether an object must be extended and blessed as an instance
         * of the extended class model.
         */
        jObj.bless = function (/*arguments*/) {
            var instance, parameters = arguments;

            if (parameters.length === 0) {
                throw { message:"L.bless.requires.at.least.one.object"};
            } else if (!jType.ofType(parameters[0], jType.types.Object)) {
                throw { message:"L.bless.applied.to.object.only"};
            }

            instance = parameters[0];
            parameters = Array.prototype.slice.call(parameters, 1);

            inheritance(instance, parameters, function (parameter) {
                return parameter;
            });

            inheritance(Object.getPrototypeOf(instance), parameters, function (parameter) {
                return Object.getPrototypeOf(parameter);
            });

            // Inheritance definition
            instance.extension = {};

            if (parameters.length > 0) {
                parameters.forEach(function (inherited) {
                    var key, type;

                    type = jType.getClass(inherited);

                    if (type === "Object") {
                        for (key in inherited.extension) {
                            if (inherited.extension.hasOwnProperty(key)) {
                                instance.extension[key] = inherited.extension[key];
                            }
                        }
                    } else {
                        instance.extension[type] = inherited;
                    }
                });

                instance.superclass = parameters[0];
            }

            return instance;
        };

        /**
         * Facility used to determine a value using a default one when
         * the parametric one is undefined.
         *
         * @param v the value
         * @param d the default
         * @return v if not undefined; d otherwise
         */
        jObj.value = function (v, d) {
            var result;

            if (v === undefined) {
                result = d;
            } else {
                result = v;
            }

            return result;
        };

        return jObj.bless(jObj, jModel, jType);

    });
