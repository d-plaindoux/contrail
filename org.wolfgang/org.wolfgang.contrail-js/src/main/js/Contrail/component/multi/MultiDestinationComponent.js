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

        function MultiDestinationComponent() {
            var Factory = require("Component/Factory");
            jObj.bless(this, Factory.core.source(), Factory.core.destinationWithSingleSource());
            this.destinationLink = [];
            this.upStreamDataFlow = undefined; // TODO
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
                return this.destinationLink[componentId] === null;
            });

        MultiDestinationComponent.prototype.connectDestination = jObj.method([jObj.types.Named("DestinationLink")], jObj.types.Named("ComponentLink"),
            function (destinationLink) {
                this.destinationLink[destinationLink.getDestination().getComponentId()] = destinationLink;
                return require("Contrail/Factory").link.components(destinationLink.getDestination(), this);
            });

        MultiDestinationComponent.prototype.getUpStreamDataFlow = jObj.method([], jObj.types.Named("DataFlow"),
            function () {
                return this.upStreamDataFlow;
            });

        MultiDestinationComponent.prototype.closeUpStream = jObj.procedure([],
            function () {
                // nothing for the moment -- TODO
            });

        return MultiDestinationComponent.init;
    });