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

define([ ],
    function () {
        "use strict";

        var jSonifier = {};

        jSonifier.jSonifable = function (callBack) {
            return { nameAndType:function (name, type) {
                return { withKeys:function () {
                    var keys = Array.prototype.slice.call(arguments);
                    callBack.jSonifable = {
                        name:name,
                        type:type,
                        toObject:function (structure, toObject) {
                            var parameters = [];

                            keys.forEach(function (key) {
                                if (structure[key]) {
                                    parameters.push(toObject(structure[key]));
                                } else {
                                    parameters.push(structure[key]);
                                }
                            });

                            return callBack.apply(this, parameters);
                        },
                        toStructure:function (object, toStructure) {
                            var structure = {};

                            keys.forEach(function (key) {
                                if (object[key]) {
                                    structure[key] = toStructure(object[key]);
                                }
                            });

                            return structure;
                        }
                    };

                    return callBack;
                }};
            }};
        };

        return jSonifier;
    });
