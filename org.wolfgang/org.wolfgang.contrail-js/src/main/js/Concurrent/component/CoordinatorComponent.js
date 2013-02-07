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

define(["Core/object/jObj", "Core/flow/jFlow", "Contrail/component/jComponent", "Core/utils/jUUID", "./flow/CoordinatorUpStreamDataFlow" ],
    function (jObj, jFlow, jComponent, jUUID, coordinatorFlow) {
        "use strict";

        function CoordinatorComponent(coordinator) {
            jObj.bless(this, jComponent.core.destinationWithSingleSource());

            this.upStreamDataFlow = jFlow.filtered(coordinatorFlow(coordinator, this), this.actorRequestFilter);
            this.coordinator = coordinator;
            this.responses = {};
        }

        CoordinatorComponent.init = jObj.constructor([ jObj.types.Named("Coordinator") ],
            function (coordinator) {
                return new CoordinatorComponent(coordinator);
            });

        CoordinatorComponent.prototype.createResponseHook = jObj.method([jObj.types.Named("Response")], jObj.types.String,
            function (response) {
                var identifier = jUUID.generate();
                this.responses[identifier] = response;
                return identifier;
            });

        CoordinatorComponent.prototype.retrieveResponseHook = jObj.method([jObj.types.String], jObj.types.Named("Response"),
            function (identifier) {
                var response = this.responses[identifier];
                delete this.responses[identifier];
                return response;
            });

        CoordinatorComponent.prototype.isAnActorRequest = jObj.method([jObj.types.Any], jObj.types.Boolean,
            function (data) {
                return jObj.ofType(data, jObj.types.ObjectOf({identifier:jObj.types.String, request:jObj.types.Named("Request"), response:jObj.types.Nullable(jObj.types.String)}));
            });

        CoordinatorComponent.prototype.isAnActorResponse = jObj.method([jObj.types.Any], jObj.types.Boolean,
            function (data) {
                return jObj.ofType(data, jObj.types.ObjectOf({identifier:jObj.types.String, type:jObj.types.Number, value:jObj.types.Any}));
            });

        CoordinatorComponent.prototype.actorRequestFilter = jObj.method([jObj.types.Any], jObj.types.Named("Packet"),
            function (packet) {
                var result;

                if (!jObj.ofType(packet, jObj.types.Named("Packet"))) {
                    result = null;
                } else if (this.isAnActorRequest(packet.getData())) {
                    result = packet;
                } else if (this.isAnActorResponse(packet.getData())) {
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
