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

define([ "require", "Core/jObj" ],
    function (require, jObj) {
        "use strict";

        function SourceComponentWithSingleDestination() {
            jObj.bless(this, require("Component/Factory").core.source());

            this.destinationLink = null;
        }

        SourceComponentWithSingleDestination.init = jObj.constructor([],
            function () {
                return new SourceComponentWithSingleDestination();
            });

        SourceComponentWithSingleDestination.prototype.acceptDestination = jObj.method([jObj.types.String], jObj.types.Boolean,
            function (componentId) {
                return this.destinationLink === null;
            });

        SourceComponentWithSingleDestination.prototype.connectDestination = jObj.method([jObj.types.Named("DestinationLink")], jObj.types.Named("ComponentLink"),
            function (destinationLink) {
                this.destinationLink = destinationLink;
                return require("Contrail/Factory").link.components(this, destinationLink.getDestination());
            });

        SourceComponentWithSingleDestination.prototype.getDestination = jObj.method([], jObj.types.Named("DestinationComponent"),
            function () {
                var result;

                if (this.sourceLink !== null) {
                    result = this.destinationLink.getDestination();
                } else {
                    throw jObj.exception("L.destination.not.connected");
                }

                return result;
            });

        SourceComponentWithSingleDestination.prototype.closeUpStream = jObj.procedure([],
            function () {
                this.getDestination().closeUpStream();
                this.destinationLink = null;
            });

        SourceComponentWithSingleDestination.prototype.getUpStreamDataFlow = jObj.method([], "DataFlow",
            function () {
                return this.getDestination().getUpStreamDataFlow();
            });

        return SourceComponentWithSingleDestination.init;
    });
