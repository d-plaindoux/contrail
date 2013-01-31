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

define([ "Core/object/jObj" ],
    function (jObj) {
        "use strict";

        function Packet(sourceId, destinationId, data, endpoint) {
            jObj.bless(this);

            this.sourceId = sourceId;
            this.destinationId = destinationId;
            this.data = data;
            this.endPoint = endpoint;
        }

        Packet.init = jObj.constructor([ jObj.types.String, jObj.types.String, jObj.types.Any , jObj.types.Nullable(jObj.types.String) ],
            function (sourceId, destinationId, data, endPoint) {
                return new Packet(sourceId, destinationId, data, endPoint);
            });

        Packet.prototype.getSourceId = jObj.method([], jObj.types.String,
            function () {
                return this.sourceId;
            });

        Packet.prototype.getDestinationId = jObj.method([], jObj.types.String,
            function () {
                return this.destinationId;
            });

        Packet.prototype.getData = jObj.method([], jObj.types.Any,
            function () {
                return this.data;
            });

        Packet.prototype.getEndPoint = jObj.method([], jObj.types.String,
            function () {
                return this.endPoint || jObj.throwError(jObj.exception("L.packet.endpoint.not.defined"));
            });

        Packet.prototype.sendTo = jObj.method([ jObj.types.String ], jObj.types.Named("Packet"),
            function (endPoint) {
                return Packet.init(this.sourceId, this.destinationId, this.data, endPoint);
            });

        return Packet.init;
    });
