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

/*global define:true, require, module*/

if (typeof define !== "function") {
    var define = require("amdefine")(module);
}

define([ "Core/object/jObj", "Contrail/jContrail", "./flow/RouterComponentUpStreamDataFlow", "./flow/RouterComponentDownStreamDataFlow" ],
    function (jObj, jContrail, routerUpStreamFlow, routerDownStreamFlow) {
        "use strict";

        function RouterComponent(route, identifier) {
            jObj.bless(this, jContrail.component.pipeline());

            this.destinationId = identifier;
            this.route = route;
            this.upStreamDataFlow = routerUpStreamFlow(this);
            this.downStreamDataFlow = routerDownStreamFlow(this);
        }

        RouterComponent.init = jObj.constructor([ jObj.types.Named("RouteTable"), jObj.types.String ],
            function (route, identifier) {
                return new RouterComponent(route, identifier);
            });

        RouterComponent.prototype.getIdentifier = jObj.method([ ], jObj.types.String,
            function () {
                return this.destinationId;
            });

        RouterComponent.prototype.getTable = jObj.method([ ], jObj.types.Named("RouteTable"),
            function () {
                return this.route;
            });

        RouterComponent.prototype.getUpStreamDataFlow = jObj.method([], jObj.types.Named("DataFlow"),
            function () {
                return this.upStreamDataFlow;
            });

        RouterComponent.prototype.getDownStreamDataFlow = jObj.method([], jObj.types.Named("DataFlow"),
            function () {
                return this.downStreamDataFlow;
            });

        return RouterComponent.init;
    });
