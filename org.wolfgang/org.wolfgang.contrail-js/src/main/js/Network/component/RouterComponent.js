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

define([ "Core/object/jObj", "Contrail/jContrail", "./flow/router/RouterComponentDownStreamDataFlow" ],
    function (jObj, jContrail, routerDownStream) {
        "use strict";

        function RouterComponent(table) {
            jObj.bless(this, jContrail.component.multi.sources());

            this.table = table;
            this.downStreamDataFlow = routerDownStream(this);

            this.activeRoutes = [];
        }

        RouterComponent.init = jObj.constructor([ jObj.types.Named("RouteTable") ],
            function (table) {
                return new RouterComponent(table);
            });

        RouterComponent.prototype.getDownStreamDataFlow = jObj.method([], jObj.types.Named("DataFlow"),
            function () {
                return this.downStreamDataFlow;
            });

        RouterComponent.prototype.getRouteTable = jObj.method([], jObj.types.Named("RouteTable"),
            function () {
                return this.table;
            });

        RouterComponent.prototype.getActiveRoutes = jObj.method([], jObj.types.ArrayOf("InterfaceComponent"),
            function () {
                return this.activeRoutes;
            });

        return RouterComponent.init;
    });
