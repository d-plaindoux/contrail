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

        function DestinationComponent() {
            jObj.bless(this, require("Component/Factory").core.component());

            this.sourceLink = null;
        }

        DestinationComponent.init = jObj.constructor([],
            function () {
                return new DestinationComponent();
            });

        DestinationComponent.prototype.acceptSource = jObj.method([jObj.types.String], jObj.types.Boolean,
            function (componentId) {
                return this.sourceLink === null;
            });

        DestinationComponent.prototype.connectSource = jObj.method([jObj.types.Named("SourceLink")], jObj.types.Named("ComponentLink"),
            function (sourceLink) {
                this.sourceLink = sourceLink;
                return require("Contrail/Factory").link.components(this.sourceLink.getSource(), this);
            });

        DestinationComponent.prototype.closeDownStream = jObj.procedure([],
            function () {
                if (this.sourceLink !== null) {
                    this.sourceLink.getSource().closeDownStream();
                    this.sourceLink = null;
                } else {
                    throw jObj.exception("L.source.not.connected");
                }
            });

        /**
         * Abstract methods
         */
        DestinationComponent.prototype.getUpStreamDataFlow = jObj.method([], jObj.types.Named("DataFlow"));

        return DestinationComponent.init;
    });