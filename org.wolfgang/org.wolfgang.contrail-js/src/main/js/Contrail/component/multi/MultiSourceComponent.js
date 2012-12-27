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

/*global define:true, require, module*/

if (typeof define !== "function") {
    var define = require("amdefine")(module);
}

define([ "require", "Core/object/jObj", "./flow/MultiDownStreamDataFlow" ],
    function (require, jObj, downStreamDataFlow) {
        "use strict";

        function MultiSourceComponent() {
            var Factory = require("Contrail/component/jComponent");
            jObj.bless(this, Factory.core.sourceWithSingleDestination(), Factory.core.destination());
            this.sourceLink = [];
            this.downStreamDataFlow = downStreamDataFlow(this);
        }

        MultiSourceComponent.init = jObj.constructor([],
            function () {
                return new MultiSourceComponent();
            });

        MultiSourceComponent.prototype.acceptSource = jObj.method([jObj.types.String], jObj.types.Boolean,
            function (componentId) {
                var result = true;

                this.sourceLink.forEach(function (link) {
                    result = result && link.getSource().getComponentId() !== componentId;
                });

                return result;
            });

        MultiSourceComponent.prototype.connectSource = jObj.method([jObj.types.Named("SourceLink")], jObj.types.Named("ComponentLink"),
            function (sourceLink) {
                this.sourceLink = this.sourceLink.concat(sourceLink);
                return require("Contrail/link/jLink").components(sourceLink.getSource(), this);
            });

        MultiSourceComponent.prototype.getSources = jObj.method([], jObj.types.ArrayOf(jObj.types.Named("SourceComponent")),
            function () {
                return this.sourceLink.map(function (link) {
                    return link.getSource();
                });
            });

        MultiSourceComponent.prototype.getDownStreamDataFlow = jObj.method([], jObj.types.Named("DataFlow"),
            function () {
                return this.downStreamDataFlow;
            });

        MultiSourceComponent.prototype.closeDownStream = jObj.procedure([],
            function () {
                var key;
                this.downStreamDataFlow.handleClose();
                this.sourceLink.forEach(function (link) {
                    link.dispose();
                });
                this.sourceLink = [];
            });

        return MultiSourceComponent.init;
    });