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

define(["Core/object/jObj", "Core/flow/jFlow", "../handler/RemoteResponseHandler", "./ActorInteractionFilter" ],
    function (jObj, jFlow, remoteResponseHandler, actorInteractionFilter) {
        "use strict";

        function CoordinatorUpStreamDataFlow(coordinator, component) {
            jObj.bless(this, jFlow.core());

            this.coordinator = coordinator;
            this.component = component;
        }

        CoordinatorUpStreamDataFlow.init = jObj.constructor([ jObj.types.Named("Coordinator"), jObj.types.Named("CoordinatorComponent")],
            function (actorManager, actorComponent) {
                return new CoordinatorUpStreamDataFlow(actorManager, actorComponent);
            });

        CoordinatorUpStreamDataFlow.prototype.handleData = jObj.procedure([ jObj.types.Named("Packet")],
            function (packet) {
                var response, data = packet.getData();

                // Decode right now

                if (actorInteractionFilter.isAnActorRequest(data)) {
                    if (data.response) {
                        response = remoteResponseHandler(this.component, packet.getSourceId(), data.response);
                    } else {
                        response = undefined;
                    }
                    this.coordinator.send(data.identifier, data.request, response);
                } else if (actorInteractionFilter.isAnActorResponse(data)) {
                    response = this.component.retrieveResponseById(data.identifier);
                    if (data.type === 0x01) {
                        response.success(data.value);
                    } else {
                        response.failure(data.value);
                    }
                }

            });

        return CoordinatorUpStreamDataFlow.init;
    });
