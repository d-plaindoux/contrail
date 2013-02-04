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

define(["Core/object/jObj", "Core/flow/jFlow", "Contrail/jContrail", "./flow/CoordinatorUpStreamDataFlow" ],
    function (jObj, jFlow, jContrail, coordinatorFlow) {
        "use strict";

        function CoordinatorComponent(coordinator) {
            jObj.bless(this, jContrail.core.destinationWithSingleSource());

            this.coordinator = coordinator;
            this.upStreamDataFlow = jFlow.filtered(this.isPacketActorRequest, coordinatorFlow(coordinator, this));
        }

        CoordinatorComponent.init = jObj.constructor([ jObj.types.Named("Coordinator") ],
            function (coordinator) {
                return new CoordinatorComponent(coordinator);
            });

        CoordinatorComponent.prototype.isPacketActorRequest = jObj.method([jObj.types.Any], jObj.types.Boolean,
            function (packet) {
                return jObj.ofType(packet, jObj.types.Named("Packet"))
                    && jObj.ofType(packet.getData(), jObj.types.ObjectOf({identifier:jObj.types.String, request:jObj.types.Named("Request")}));
            });

        return CoordinatorComponent.init;
    });
