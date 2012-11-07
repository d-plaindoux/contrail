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

define([ "require" ],
    function (require) {
        "use strict";

        var jType = {};

        function RuntimeTypeError(message) {
            require("Core/jObj").bless(this);
            this.message = message;
        }

        jType.checkType = function (object, type) {
            if (type !== undefined && !jType.instanceOf(type, jType.types.String)) {
                throw new RuntimeTypeError(type + " must be an instance of String");
            } else if (!jType.instanceOf(object, type)) {
                throw new RuntimeTypeError(object + " must be an instance of " + type);
            } else {
                return object;
            }
        };

        /**
         * Type definitions
         */
        jType.types = {
            Any:"Any",
            Array:"Array",
            Object:"Object",
            Number:"number",
            String:"string",
            Boolean:"boolean",
            Undefined:"undefined",
            Named:function (str) {
                return str;
            }
        };

        /**
         * Method called whether the class name nust be retrieved
         *
         * @param object The object
         * @return the type if it's an object; undefined otherwise
         */
        jType.getClass = function (object) {
            if (object && object.constructor && object.constructor.toString) {
                var arr = object.constructor.toString().match(/function\s*(\w+)/);
                if (arr && arr.length === 2) {
                    return arr[1];
                }
            }

            return undefined;
        };

        /**
         * Check if the parameter is an object with a given type
         * @param object
         * @param type
         * @return true if the object is a type of type; false otherwise
         */
        jType.isObjectType = function (object, type) {
            return Object.prototype.toString.apply(object) === '[object ' + type + ']';
        };

        /**
         * Method called to check if a given object has a given type
         *
         * @param object
         * @param type
         * @return true if the object is a type of type; false otherwise
         */
        jType.instanceOf = function (object, type) {
            var result = false;

            if (typeof object === type) {
                result = true;
            } else if (jType.isObjectType(object, type)) {
                result = true;
            } else if (jType.getClass(object) === type) {
                result = true;
            } else if (object && object.inherit && object.inherit.hasOwnProperty(type)) {
                result = true;
            } else if (type === jType.types.Any) {
                result = true;
            }

            return result;
        };

        return jType;
    });