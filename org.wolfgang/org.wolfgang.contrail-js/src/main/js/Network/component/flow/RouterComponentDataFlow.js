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

        var RouterComponentDataFlow = function (router) {
            jObj.bless(this, jContrail.flow.core());

            this.router = router;
        };

        RouterComponentDataFlow.init = jObj.constructor([ jObj.types.Named("RouterComponent") ],
            function (router) {
                return new RouterComponentDataFlow(router);
            });

        RouterComponentDataFlow.prototype.handleData = jObj.procedure([jObj.types.Named("Packet")],
            function (packet) {
                if (this.router.getIdentifier() === packet.getDestinationId()) {
                    this.router.getDestination().getUpStreamDataFlow().handleData(packet);
                } else {
                    var newPacket = packet.sendTo(this.router.getTable().getRoute(packet.getDestinationId()));
                    this.router.getSource().getDownStreamDataFlow().handleData(newPacket);
                }
            });

        return RouterComponentDataFlow.init;
    });