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

define("Core/utils/jUUID", [  ],
    function () {
        "use strict";

        var jUUID = {};

        jUUID.generate = function () {
            var S4 = function () {
                return Math.floor(Math.random() * 0x10000).toString(16);
            };

            return (
                S4() + S4() + "-" +
                    S4() + "-" +
                    S4() + "-" +
                    S4() + "-" +
                    S4() + S4() + S4()
                );
        };

        jUUID.array = function (len) {
            var array = [], i;

            for (i = 0; i < len; i += 1) {
                array.push(undefined);
            }

            return array;
        };

        return jUUID;
    });
