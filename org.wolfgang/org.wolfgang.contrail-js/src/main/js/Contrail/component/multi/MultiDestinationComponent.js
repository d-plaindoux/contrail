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

define([ "require", "Core/jObj", "./flow/MultiUpStreamDataFlow" ],
    function (require, jObj, upStreamDataFlow) {
        "use strict";

        function MultiDestinationComponent() {
            var Factory = require("Component/Factory");
            jObj.bless(this, Factory.core.source(), Factory.core.destinationWithSingleSource());
            this.destinationLink = [];
            this.upStreamDataFlow = upStreamDataFlow(this);
        }

        /**
         * Construction initialisation
         */
        MultiDestinationComponent.init = jObj.constructor([],
            function () {
                return new MultiDestinationComponent();
            });

        MultiDestinationComponent.prototype.acceptDestination = jObj.method([jObj.types.String], jObj.types.Boolean,
            function (componentId) {
                var result = true;

                this.destinationLink.forEach(function (link) {
                    result = result && link.getDestination().getComponentId() !== componentId;
                });

                return result;
            });

        MultiDestinationComponent.prototype.connectDestination = jObj.method([jObj.types.Named("DestinationLink")], jObj.types.Named("ComponentLink"),
            function (destinationLink) {
                this.destinationLink = this.destinationLink.concat(destinationLink);
                return require("Link/Factory").components(this, destinationLink.getDestination());
            });

        MultiDestinationComponent.prototype.getDestinations = jObj.method([], jObj.types.Array,
            function () {
                return this.destinationLink.map(function (link) {
                    return link.getDestination();
                });
            });

        MultiDestinationComponent.prototype.getUpStreamDataFlow = jObj.method([], jObj.types.Named("DataFlow"),
            function () {
                return this.upStreamDataFlow;
            });

        MultiDestinationComponent.prototype.closeUpStream = jObj.procedure([],
            function () {
                this.upStreamDataFlow.handleClose();
                this.destinationLink.forEach(function (link) {
                    link.dispose();
                });
                this.destinationLink = [];
            });

        return MultiDestinationComponent.init;
    });