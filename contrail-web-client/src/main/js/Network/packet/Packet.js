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

        function Packet(sourceId, destinationId, data) {
            jObj.bless(this);

            this.sourceId = sourceId;
            this.destinationId = destinationId;
            this.data = data;
        }

        Packet.init = jObj.constructor([ jObj.types.Nullable(jObj.types.String), jObj.types.String, jObj.types.Any ],
            function (sourceId, destinationId, data) {
                return new Packet(sourceId, destinationId, data);
            });

        Packet.prototype.setSourceId = jObj.procedure([jObj.types.String],
            function (sourceId) {
                this.sourceId = sourceId;
            });

        Packet.prototype.getSourceId = jObj.method([], jObj.types.Nullable(jObj.types.String),
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

        Packet.prototype.setData = jObj.procedure([jObj.types.Any],
            function (data) {
                this.data = data;
            });

        return jObj.jSonifable(Packet.init).nameAndType("Packet", "Packet").withKeys("sourceId", "destinationId", "data");
    });
