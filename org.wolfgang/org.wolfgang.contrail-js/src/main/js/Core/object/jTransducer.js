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

define([ "./jType" ],
    function (jType) {
        "use strict";

        var jTransducer = {};

        /**
         * Return an object transformation
         *
         * @param object The object
         * @param driver The transformation driver
         * @return the transformation
         */
        jTransducer.transform = function (object, driver) {
            var result, key, content;

            if (typeof object === "object") {
                content = driver.enterObject(jType.getClass(object));

                for (key in object) {
                    if (object.hasOwnProperty(key)) {
                        content = driver.visitAttribute(content, key, jTransducer.transform(object[key], driver));
                    }
                }

                result = driver.exitObject(content);
            } else {
                result = driver.visitNative(object);
            }

            return result;
        };

        /**
         * Return the string representation
         *
         * @param object The object
         * @return the type
         */
        jTransducer.toString = function (object) {
            var driverToString = {
                enterObject:function (name) {
                    return "{";
                },
                visitAttribute:function (content, key, value) {
                    return content + " " + key + ":" + value + ";\n";
                },
                exitObject:function (content) {
                    return content + " }\n";
                },
                visitNative:function (value) {
                    return value;
                }
            };

            return jTransducer.transform(object, driverToString);
        };

        /**
         * Return the type of the parameter
         *
         * @param object The object
         * @return the type
         */
        jTransducer.toType = function (object) {
            var driverToType = {
                enterObject:function (name) {
                    return {};
                },
                visitAttribute:function (content, key, value) {
                    content[key] = value;
                    return content;
                },
                exitObject:function (content) {
                    return content;
                },
                visitNative:function (value) {
                    return typeof value;
                }
            };

            return jTransducer.transform(object, driverToType);
        };

        return jTransducer;

    });
