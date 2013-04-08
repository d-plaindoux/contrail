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


define([ "Core/object/jObj", "Contrail/jContrail" ],
    function (jObj, jContrail) {
        "use strict";

        var RouterDownStreamComponentDataFlow = function (component) {
            jObj.bless(this, jContrail.flow.core());

            this.component = component;
        };

        RouterDownStreamComponentDataFlow.init = jObj.constructor([ jObj.types.Named("RouterComponent") ],
            function (router) {
                return new RouterDownStreamComponentDataFlow(router);
            });

        RouterDownStreamComponentDataFlow.prototype.handleData = jObj.procedure([jObj.types.Named("Packet")],
            function (packet) {
                var builder = null, endPoint = null, activeRoute = null;

                if (this.component.getRouteTable().hasEntry(packet.getDestinationId())) {
                    builder = this.component.getRouteTable().getEntry(packet.getDestinationId());
                    endPoint = builder.getEndPoint();
                }

                activeRoute = this.component.getActiveRoute(packet.getDestinationId(), endPoint);

                if (!activeRoute) {
                    if (builder) {
                        activeRoute = this.component.addActiveRoute(builder.activate(), endPoint);
                    } else {
                        jObj.throwError(jObj.exception("L.no.route.to.destination"));
                    }
                }

                activeRoute.getDownStreamDataFlow().handleData(packet);
            });

        RouterDownStreamComponentDataFlow.prototype.handleClose = jObj.procedure([],
            function () {
                // TODO
            });

        return RouterDownStreamComponentDataFlow.init;
    });
