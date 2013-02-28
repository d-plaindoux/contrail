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

        var ClientComponentUpStreamDataFlow = function (component) {
            jObj.bless(this, jContrail.flow.core());

            this.component = component;
        };

        ClientComponentUpStreamDataFlow.init = jObj.constructor([ jObj.types.Named("ClientComponent") ],
            function (component) {
                return new ClientComponentUpStreamDataFlow(component);
            });

        ClientComponentUpStreamDataFlow.prototype.handleData = jObj.procedure([jObj.types.Named("Packet")],
            function (packet) {
                this.component.addDestinationId(packet.getSourceId());
                this.component.getDestination().getUpStreamDataFlow().handleData(packet);
            });

        ClientComponentUpStreamDataFlow.prototype.handleClose = jObj.procedure([],
            function () {
                this.component.getDestination().getUpStreamDataFlow().handleClose();
            });

        return ClientComponentUpStreamDataFlow.init;
    });
