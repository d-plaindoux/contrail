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

define([ "require", "Core/jObj" ],
    function (require, jObj) {
        "use strict";

        var DestinationCompositionComponent = function (components) {
            jObj.bless(this, require("Contrail/component").core.destination());
            this.component = components[0];
        };

        DestinationCompositionComponent.init = jObj.constructor([ jObj.types.ArrayOf(jObj.types.Named("Component")) ],
            function (components) {
                return new DestinationCompositionComponent(components);
            });

        DestinationCompositionComponent.prototype.acceptSource = jObj.method([jObj.types.String], jObj.types.Boolean,
            function (componentId) {
                return this.components[this.components.length - 1].acceptSource(componentId);
            });

        DestinationCompositionComponent.prototype.connectSource = jObj.method([jObj.types.Named("SourceLink")], jObj.types.Named("ComponentLink"),
            function (sourceLink) {
                return this.components[this.components.length - 1].connectSource(sourceLink);
            });

        DestinationCompositionComponent.prototype.getSource = jObj.method([], jObj.types.Named("SourceComponent"),
            function () {
                return this.components[this.components.length - 1].getSource();
            });

        DestinationCompositionComponent.prototype.closeDownStream = jObj.procedure([],
            function () {
                this.getSource().closeDownStream();
            });

        return DestinationCompositionComponent.init;
    });
