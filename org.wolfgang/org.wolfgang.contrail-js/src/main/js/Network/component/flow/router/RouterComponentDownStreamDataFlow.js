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
                var endPoint, activeRoute;

                if (this.component.getRouteTable().hasRoute(packet.getDestinationId())) {
                    endPoint = this.component.getRouteTable().getRoute(packet.getDestinationId());
                }

                this.component.getActiveRoutes().forEach(function (route) {
                    if (!activeRoute) {
                        if (route.acceptDestinationId(packet.getDestinationId())) {
                            activeRoute = route;
                        } else if (endPoint && route.getEndPoint() === endPoint) {
                            activeRoute = route;
                        }
                    }
                });

                if (!activeRoute) {
                    if (endPoint) {
                        activeRoute = endPoint.activate(packet.getDestinationId());
                        jContrail.component.compose(activeRoute, this.component);
                    } else {
                        jObj.throwError(jObj.exception("L.no.route.to.destination"));
                    }
                }

                activeRoute.getDownStreamDataFlow().handleData(packet);
            });

        RouterDownStreamComponentDataFlow.prototype.handleClose = jObj.procedure([],
            function () {
                this.component.getSource().getDownStreamDataFlow().handleClose();
            });

        return RouterDownStreamComponentDataFlow.init;
    });
