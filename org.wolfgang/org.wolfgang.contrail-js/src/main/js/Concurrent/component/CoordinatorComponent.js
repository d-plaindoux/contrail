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

define(["Core/object/jObj", "Core/flow/jFlow", "Contrail/component/jComponent", "./flow/CoordinatorUpStreamDataFlow" ],
    function (jObj, jFlow, jComponent, coordinatorFlow) {
        "use strict";

        function CoordinatorComponent(coordinator) {
            jObj.bless(this, jComponent.core.destinationWithSingleSource());

            this.upStreamDataFlow = jFlow.filtered(coordinatorFlow(coordinator, this), this.packetActorRequestFilter);
            this.coordinator = coordinator;
        }

        CoordinatorComponent.init = jObj.constructor([ jObj.types.Named("Coordinator") ],
            function (coordinator) {
                return new CoordinatorComponent(coordinator);
            });

        CoordinatorComponent.prototype.packetActorRequestFilter = jObj.method([jObj.types.Any], jObj.types.Named("Packet"),
            function (packet) {
                var result;

                if (jObj.ofType(packet, jObj.types.Named("Packet"))
                    && jObj.ofType(packet.getData(), jObj.types.ObjectOf({identifier:jObj.types.String, request:jObj.types.Named("Request")}))) {
                    result = packet;
                } else {
                    result = null;
                }

                return result;
            });


        CoordinatorComponent.prototype.getUpStreamDataFlow = jObj.method([], jObj.types.Named("DataFlow"),
            function () {
                return this.upStreamDataFlow;
            });

        return CoordinatorComponent.init;
    });
