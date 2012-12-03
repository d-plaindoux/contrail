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

        function SwitchDownComponent() {
            var Factory = require("Component/Factory");
            jObj.bless(this, Factory.core.sourceWithSingleDestination(), Factory.core.destination());
            this.sourceLink = [];
            this.downStreamDataFlow = undefined; // TODO
        }

        /**
         * Construction initialisation
         */
        SwitchDownComponent.init = jObj.constructor([],
            function () {
                return new SwitchDownComponent();
            });

        SwitchDownComponent.prototype.acceptSource = jObj.method([jObj.types.String], jObj.types.Boolean,
            function (componentId) {
                return this.sourceLink[componentId] === null;
            });

        SwitchDownComponent.prototype.connectSource = jObj.method([jObj.types.Named("SourceLink")], jObj.types.Named("ComponentLink"),
            function (sourceLink) {
                this.sourceLink[sourceLink.getSource().getComponentId()] = sourceLink;
                return require("Contrail/Factory").link.components(sourceLink.getSource(), this);
            });

        SwitchDownComponent.prototype.getDownStreamDataFlow = jObj.method([], jObj.types.Named("DataFlow"),
            function () {
                return this.upStreamDataFlow;
            });

        SwitchDownComponent.prototype.closeDownStream = jObj.procedure([],
            function () {
                // TODO;
            });

        return SwitchDownComponent.init;
    });