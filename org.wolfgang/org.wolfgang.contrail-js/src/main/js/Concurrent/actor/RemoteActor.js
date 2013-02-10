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

/*global define, setInterval*/

define([ "Core/object/jObj", "Network/jNetwork", "./Actor" ],
    function (jObj, jNetwork, actor) {
        "use strict";

        function RemoteActor(coordinator, location, identifier) {
            jObj.bless(this, actor(coordinator, identifier));

            this.location = location;
        }

        RemoteActor.init = jObj.constructor([ jObj.types.Named("Coordinator"), jObj.types.String, jObj.types.String ],
            function (coordinator, location, identifier) {
                return new RemoteActor(coordinator, location, identifier);
            });

        RemoteActor.prototype.invoke = jObj.procedure([ jObj.types.Named("Request"), jObj.types.Nullable(jObj.types.Named("Response"))],
            function (request, response) {
                this.coordinator.getRemoteActorHandler().handle(this.location, this.identifier, request, response);
            });

        return RemoteActor.init;
    });
