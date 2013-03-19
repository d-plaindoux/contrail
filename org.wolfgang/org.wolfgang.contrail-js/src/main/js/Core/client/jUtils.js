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

/*global define, window*/

define("Core/client/jUtils", [ "Core/object/jObj" ],
    function (jObj) {
        "use strict";

        var jUtils = {};

        jUtils.getURLFromLocation = jObj.method([ jObj.types.String, jObj.types.String ], jObj.types.String,
            function (scheme, path) {
                return scheme + "://" + window.location.host + "/" + path;
            });

        jUtils.getURLPathName = jObj.method([ ], jObj.types.String,
            function () {
                return window.location.pathname;
            });

        jUtils.getURLSearch = jObj.method([ ], jObj.types.String,
            function () {
                var search;

                if (window.location.search && window.location.search.length > 0) {
                    search = window.location.search.substring(1);
                } else {
                    search = "";
                }

                return search;
            });

        jUtils.getWebSocketURL = jObj.method([ ], jObj.types.String,
            function () {
                return jUtils.getURLFromLocation("ws", "websocket");
            });

        return jUtils;
    });
