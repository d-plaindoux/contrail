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

define([ "require", "Core/object/jObj", "../core/DestinationComponent" ],
    function (require, jObj, destination) {
        "use strict";

        var DestinationCompositionComponent = function (components) {
            jObj.bless(this, destination());
            this.initialComponent = components[0];
            this.terminalComponent = components[components.length - 1];
        };

        DestinationCompositionComponent.init = jObj.constructor([ jObj.types.ArrayOf(jObj.types.Named("Component")) ],
            function (components) {
                return new DestinationCompositionComponent(components);
            });

        DestinationCompositionComponent.prototype.acceptSource = jObj.method([jObj.types.String], jObj.types.Boolean,
            function (componentId) {
                return this.initialComponent.acceptSource(componentId);
            });

        DestinationCompositionComponent.prototype.connectSource = jObj.method([jObj.types.Named("SourceLink")], jObj.types.Named("ComponentLink"),
            function (sourceLink) {
                return this.initialComponent.connectSource(sourceLink);
            });

        DestinationCompositionComponent.prototype.getSource = jObj.method([], jObj.types.Named("SourceComponent"),
            function () {
                return this.initialComponent.getSource();
            });

        DestinationCompositionComponent.prototype.getUpStreamDataFlow = jObj.method([], jObj.types.Named("DataFlow"),
            function(){
                return this.initialComponent.getUpStreamDataFlow();
            });

        DestinationCompositionComponent.prototype.getDownStreamDataFlow = jObj.method([], jObj.types.Named("DataFlow"),
            function(){
                return this.terminalComponent.getDownStreamDataFlow();
            });

        DestinationCompositionComponent.prototype.closeDownStream = jObj.procedure([],
            function () {
                this.getSource().closeDownStream();
            });

        return DestinationCompositionComponent.init;
    });
