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

define(["require", "Core/object/jObj" ],
    function (require, jObj) {
        "use strict";

        function CompositionComponent(components) {
            jObj.bless(this, require("Contrail/component/jComponent").core.component());
            this.components = components;
        }

        /**
         * Constructor
         *
         * @type {*|void}
         */
        CompositionComponent.init = jObj.constructor([ jObj.types.ArrayOf(jObj.types.Named("Component")) ],
            function (components) {
                return new CompositionComponent(components);
            });

        CompositionComponent.prototype.getSource = jObj.method([], jObj.types.Named("SourceComponent"),
            function () {
                return this.components[this.components.length - 1].getSource();
            });

        CompositionComponent.prototype.closeDownStream = jObj.procedure([],
            function () {
                this.getSource().closeDownStream();
            });

        CompositionComponent.prototype.getDownStreamDataFlow = jObj.method([], "DataFlow",
            function () {
                return this.getSource().getDownStreamDataFlow();
            });

        CompositionComponent.prototype.getDestination = jObj.method([], jObj.types.Named("DestinationComponent"),
            function () {
                return this.components[0].getDestination();
            });

        CompositionComponent.prototype.closeUpStream = jObj.procedure([],
            function () {
                this.getDestination().closeUpStream();
            });

        CompositionComponent.prototype.getUpStreamDataFlow = jObj.method([], "DataFlow",
            function () {
                return this.getDestination().getUpStreamDataFlow();
            });

        return CompositionComponent.init;
    });
