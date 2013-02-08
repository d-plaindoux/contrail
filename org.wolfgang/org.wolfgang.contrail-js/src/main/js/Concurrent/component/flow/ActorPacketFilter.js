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

define(["Core/object/jObj" ],
    function (jObj) {
        "use strict";

        var ActorPacketFilter = {};

        ActorPacketFilter.isAnActorRequest = jObj.method([jObj.types.Any], jObj.types.Boolean,
            function (data) {
                return jObj.ofType(data, jObj.types.ObjectOf({identifier:jObj.types.String, request:jObj.types.Named("Request"), response:jObj.types.Nullable(jObj.types.String)}));
            });

        ActorPacketFilter.isAnActorResponse = jObj.method([jObj.types.Any], jObj.types.Boolean,
            function (data) {
                return jObj.ofType(data, jObj.types.ObjectOf({identifier:jObj.types.String, type:jObj.types.Number, value:jObj.types.Any}));
            });

        ActorPacketFilter.actorRequestFilter = jObj.method([jObj.types.Any], jObj.types.Named("Packet"),
            function (packet) {
                var result;

                if (!jObj.ofType(packet, jObj.types.Named("Packet"))) {
                    result = null;
                } else if (ActorPacketFilter.isAnActorRequest(packet.getData())) {
                    result = packet;
                } else if (ActorPacketFilter.isAnActorResponse(packet.getData())) {
                    result = packet;
                } else {
                    result = null;
                }

                return result;
            });


        return ActorPacketFilter;
    });
