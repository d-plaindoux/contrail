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

define(["Core/object/jObj", "Concurrent/event/jEvent", "Network/jNetwork"],
    function (jObj, jEvent, jNetwork) {
        "use strict";

        function ResponseHandler(component, location, identifier) {
            jObj.bless(this, jEvent.response(this.success, this.failure));
            this.component = component;
            this.location = location;
            this.identifier = identifier;
        }

        function sendPacket(self, data) {
            var packet = jNetwork.packet(null, self.location, data, null);
            self.component.getDownStreamDataFlow().handleData(packet);
        }

        ResponseHandler.init = jObj.constructor([jObj.types.Named("CoordinatorComponent"), jObj.types.String, jObj.types.String],
            function (component, location, identifier) {
                return new ResponseHandler(component, location, identifier);
            });

        ResponseHandler.prototype.success = jObj.procedure([jObj.types.Any],
            function (value) {
                sendPacket(this, { identifier:this.identifier, type:0x01, value:value });
            });

        ResponseHandler.prototype.failure = jObj.procedure([jObj.types.Any],
            function (error) {
                sendPacket(this, { identifier:this.identifier, type:0x02, value:error });
            });

        return ResponseHandler.init;
    });
