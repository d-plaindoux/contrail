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

        function ActorHandler(destinationId, coordinatorComponent) {
            jObj.bless(this);

            this.destinationId = destinationId;
            this.coordinatorComponent = coordinatorComponent;
        }

        ActorHandler.init = jObj.constructor([jObj.types.String, jObj.types.Named("CoordinatorComponent")],
            function (destinationId, coordinatorComponent) {
                return new ActorHandler(destinationId, coordinatorComponent);
            });

        ActorHandler.prototype.actorHandler = jObj.method([jObj.types.ObjectOf({identifier:jObj.types.String, request:jObj.types.Named("Request"), response:jObj.types.Nullable(jObj.types.Named("Response"))})],
            function (data) {
                if (data.response) {
                    data.response = this.coordinatorComponent.createResponseHook(data.response);
                }

                this.coordinatorComponent.getDownStreamDataFlow().handleData(jNetwork.packet(this.destinationId, data));
            });

        return ActorHandler.init;
    });
