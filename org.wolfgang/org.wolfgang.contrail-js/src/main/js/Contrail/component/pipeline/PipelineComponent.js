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

        function PipelineComponent() {
            var Factory = require("Component/Factory");
            jObj.bless(this, Factory.core.source(), Factory.core.destination());
        }

        /**
         * Construction initialisation
         */
        PipelineComponent.init = jObj.constructor([], function () {
            return new PipelineComponent();
        });

        /**
         * Provides the embedded upstream source component (internal use only)
         *
         * @return the current up stream source component
         */
        PipelineComponent.prototype.getSourceComponentLink = function () {
            return this.sourceLink;
        };

        /**
         * Provides the embedded upstream source component (internal use only)
         *
         * @return the current up stream source component
         */
        PipelineComponent.prototype.getDestinationComponentLink = function () {
            return this.destinationLink;
        };

        return PipelineComponent.init;
    });