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

define([ "Core/object/jObj" ],
    function (jObj) {
        "use strict";

        function RouteTable() {
            jObj.bless(this);

            this.routes = {};
        }

        RouteTable.init = jObj.constructor([ ],
            function () {
                return new RouteTable();
            });

        RouteTable.prototype.addEntry = jObj.procedure([ jObj.types.String, jObj.types.Named("ClientBuilder") ],
            function (name, builder) {
                if (this.hasEntry(name)) {
                    jObj.throwError(jObj.exception("L.route.entry.already.defined"));
                } else {
                    this.routes[name] = builder;
                }
            });

        RouteTable.prototype.hasEntry = jObj.method([ jObj.types.String ], jObj.types.Boolean,
            function (name) {
                return this.routes.hasOwnProperty(name);
            });

        RouteTable.prototype.getEntry = jObj.method([ jObj.types.String ], jObj.types.Named("ClientBuilder"),
            function (name) {
                return this.routes[name] || jObj.throwError(jObj.exception("L.route.entry.not.defined"));
            });

        return RouteTable.init;
    });
