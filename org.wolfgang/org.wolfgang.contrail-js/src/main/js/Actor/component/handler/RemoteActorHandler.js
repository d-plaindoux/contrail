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

define(["Core/object/jObj", "Network/jNetwork" ],
    function (jObj, jNetwork) {
        "use strict";

        function RemoteActorHandler(coordinatorComponent) {
            jObj.bless(this);

            this.coordinatorComponent = coordinatorComponent;
        }

        RemoteActorHandler.init = jObj.constructor([jObj.types.Named("CoordinatorComponent")],
            function (coordinatorComponent) {
                return new RemoteActorHandler(coordinatorComponent);
            });

        RemoteActorHandler.prototype.handle = jObj.procedure([jObj.types.String, jObj.types.String, jObj.types.Named("Request"), jObj.types.Nullable(jObj.types.Named("Response"))],
            function (location, identifier, request, response) {
                var packet, responseId;

                if (response) {
                    responseId = this.coordinatorComponent.createResponseId(response);
                } else {
                    responseId = undefined;
                }

                packet = jNetwork.packet(null, location, request.toActor(identifier, responseId));

                this.coordinatorComponent.getDownStreamDataFlow().handleData(packet);
            });

        return RemoteActorHandler.init;
    });
