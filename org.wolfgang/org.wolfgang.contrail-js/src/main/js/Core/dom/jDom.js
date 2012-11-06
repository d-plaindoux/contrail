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

define("Core/jDom", [ ],
    function () {
        "use strict";

        function PObject(tag, attributes, content) {
            this.qname = tag;
            this.attributes = attributes;
            this.content = content;
        }

        PObject.prototype.toString = function () {
            var key, result;

            result = "<" + this.qname;

            for (key in this.attributes) {
                result += " " + key + "='" + this.attributes[key] + "'";
            }

            if (this.content) {
                result += ">" + this.content.toString() + "</" + this.qname + ">";
            } else {
                result += "/>";
            }

            return result;

        };

        PObject.prototype.build = PObject.prototype.toString;

        // Public package definition

        return function (tag, attributes, content) {
            return new PObject(tag, attributes, content).build();
        };
    });
