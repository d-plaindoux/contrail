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

define("Core/jObj", [ "jquery", "./jModel", "./jType", "./jTransObj" ],
    function (jQuery, jModel, jType, jTransObj) {
        "use strict";

        var jObj = jQuery.extend({}, jModel, jType, jTransObj);

        /**
         * Method called whether an object must be extended and blessed as an instance
         * of the extended class model.
         */
        jObj.bless = function (/*arguments*/) {
            var i, key, parameters = arguments;

            if (arguments.length > 0) {
                // Extension and supers
                for (i = 1; i < parameters.length; i += 1) {
                    jQuery.extend(parameters[0], parameters[i]);
                }

                // Inheritance
                parameters[0].inherit = {};

                for (i = 1; i < parameters.length; i += 1) {
                    if (parameters[i] && parameters[i].inherit) {
                        for (key in parameters[i].inherit) {
                            if (parameters[i].inherit.hasOwnProperty(key)) {
                                parameters[0].inherit[key] = true;
                            }
                        }
                        parameters[0].inherit[jType.getClass(parameters[i])] = true;
                    }
                }
            }
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

        return jObj;

    });
