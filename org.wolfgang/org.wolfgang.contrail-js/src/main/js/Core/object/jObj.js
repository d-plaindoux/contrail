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

define("Core/jObj", [ "./jModel", "./jType", "./jTransducer" ],
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

            /*
             parameters.forEach(function (value) {
             if (!jType.ofType(value, jType.types.Object)) {
             throw { message:"L.bless.applied.to.object.only"};
             }
             });
             */

            instance = parameters[0];
            parameters = Array.prototype.slice.call(parameters, 1);

            inheritance(instance, parameters, function (parameter) {
                return parameter;
            });

            inheritance(Object.getPrototypeOf(instance), parameters, function (parameter) {
                return Object.getPrototypeOf(parameter);
            });

            // Inheritance definition
            instance.inherit = {};

            parameters.forEach(function (inherited) {
                var key;
                for (key in inherited.inherit) {
                    if (inherited.inherit.hasOwnProperty(key)) {
                        instance.inherit[key] = true;
                    }
                }
                instance.inherit[jType.getClass(inherited)] = true;
            });

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

        return jObj.bless(jObj, jModel, jType, jTransducer);

    });
