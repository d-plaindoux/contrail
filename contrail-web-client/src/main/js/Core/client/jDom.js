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

define("Core/client/jDom", [ "Core/object/jObj" ],
    function (jObj) {
        "use strict";

        var jDom = {};

        // Atomic values

        jDom.text = jObj.method([ jObj.types.String ], jObj.types.String,
            function (text) {
                return text;
            });

        jDom.empty = jObj.method([], jObj.types.String,
            function () {
                return jDom.text("");
            });

        // Tags

        jDom.tag = jObj.method([ jObj.types.String, jObj.types.Object, jObj.types.Nullable(jObj.types.Any) ], jObj.types.String,
            function (qname, attributes, content) {
                var key, result;

                result = "<" + qname;

                for (key in attributes) {
                    if (attributes.hasOwnProperty(key)) {
                        result += " " + key + "='" + attributes[key] + "'";
                    }
                }

                if (content) {
                    result += ">" + content.toString() + "</" + qname + ">";
                } else {
                    result += ">" + "</" + qname + ">";
                }

                return result;
            });

        jDom.div = jObj.method([ jObj.types.Object, jObj.types.Nullable(jObj.types.Any) ], jObj.types.String,
            function (attributes, content) {
                return jDom.tag("div", attributes, content);
            });

        return jDom;
    });
