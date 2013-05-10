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

define([
    "Core/object/jObj", "Core/flow/jFlow", "Core/utils/jUtils", "Contrail/component/jComponent",
    "./flow/CoordinatorUpStreamDataFlow", "./flow/CoordinatorDownStreamDataFlow",
    "./flow/ActorInteractionFilter", "./handler/RemoteActorHandler"
],
    function (jObj, jFlow, jUUID, jComponent, coordinatorUpStreamFlow, coordinatorDownStreamFlow, actorInteractionFilter, remoteActorHandler) {
        "use strict";

        function CoordinatorComponent(coordinator, domainId) {
            jObj.bless(this, jComponent.core.destinationWithSingleSource());

            this.domainId = domainId;
            this.upStreamDataFlow = jFlow.filtered(coordinatorUpStreamFlow(coordinator, this), actorInteractionFilter.isAnActorInteraction);
            this.downStreamDataFlow = coordinatorDownStreamFlow(this);
            this.responses = {};

            coordinator.setRemoteActorHandler(remoteActorHandler(this));
        }

        CoordinatorComponent.init = jObj.constructor([ jObj.types.Named("Coordinator"), jObj.types.String ],
            function (coordinator, domainId) {
                return new CoordinatorComponent(coordinator, domainId);
            });

        CoordinatorComponent.prototype.getDomainId = jObj.method([], jObj.types.String,
            function() {
                return this.domainId;
            });

        CoordinatorComponent.prototype.createResponseId = jObj.method([jObj.types.Named("Response")], jObj.types.String,
            function (response) {
                var identifier = jUUID.uuid();
                this.responses[identifier] = response;
                return identifier;
            });

        CoordinatorComponent.prototype.retrieveResponseById = jObj.method([jObj.types.String], jObj.types.Named("Response"),
            function (identifier) {
                var response = this.responses[identifier];
                delete this.responses[identifier];
                return response;
            });

        CoordinatorComponent.prototype.getUpStreamDataFlow = jObj.method([], jObj.types.Named("DataFlow"),
            function () {
                return this.upStreamDataFlow;
            });

        CoordinatorComponent.prototype.getUpStreamDataFlow = jObj.method([], jObj.types.Named("DataFlow"),
            function () {
                return this.upStreamDataFlow;
            });

        CoordinatorComponent.prototype.createRemoteActor = jObj.procedure([jObj.types.String, jObj.types.String],
            function (id, name) {
                this.coordinator.localActor(name, remoteActorHandler(id, this));
            });

        return CoordinatorComponent.init;
    });
