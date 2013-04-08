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

define(["require", "Core/object/jObj", "../core/SourceComponent" ],
    function (require, jObj, source) {
        "use strict";

        var SourceCompositionComponent = function (components) {
            jObj.bless(this, source());
            this.initialComponent = components[0];
            this.terminalComponent = components[components.length - 1];
        };

        SourceCompositionComponent.init = jObj.constructor([ jObj.types.ArrayOf(jObj.types.Named("Component")) ],
            function (components) {
                return new SourceCompositionComponent(components);
            });

        SourceCompositionComponent.prototype.acceptDestination = jObj.method([jObj.types.String], jObj.types.Boolean,
            function (componentId) {
                return this.terminalComponent.acceptDestination(componentId);
            });

        SourceCompositionComponent.prototype.connectDestination = jObj.method([jObj.types.Named("DestinationLink")], jObj.types.Named("ComponentLink"),
            function (destinationLink) {
                return this.terminalComponent.connectDestination(destinationLink);
            });

        SourceCompositionComponent.prototype.getDestination = jObj.method([], jObj.types.Named("DestinationComponent"),
            function () {
                return this.terminalComponent.getDestination();
            });

        SourceCompositionComponent.prototype.getDownStreamDataFlow = jObj.method([], jObj.types.Named("DataFlow"),
            function(){
                return this.terminalComponent.getDownStreamDataFlow();
            });

        SourceCompositionComponent.prototype.getUpStreamDataFlow = jObj.method([], jObj.types.Named("DataFlow"),
            function(){
                return this.initialComponent.getUpStreamDataFlow();
            });

        SourceCompositionComponent.prototype.closeUpStream = jObj.procedure([],
            function () {
                this.getDestination().closeUpStream();
            });

        return SourceCompositionComponent.init;
    });
