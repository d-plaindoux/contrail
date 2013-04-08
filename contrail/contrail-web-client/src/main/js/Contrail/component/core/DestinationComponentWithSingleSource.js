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

define([ "require", "Core/object/jObj" ],
    function (require, jObj) {
        "use strict";

        function DestinationComponentWithSingleSource() {
            jObj.bless(this, require("Contrail/component/jComponent").core.destination());

            this.sourceLink = null;
        }

        DestinationComponentWithSingleSource.init = jObj.constructor([],
            function () {
                return new DestinationComponentWithSingleSource();
            });

        DestinationComponentWithSingleSource.prototype.acceptSource = jObj.method([jObj.types.String], jObj.types.Boolean,
            function (componentId) {
                return this.sourceLink === null;
            });

        DestinationComponentWithSingleSource.prototype.connectSource = jObj.method([jObj.types.Named("SourceLink")], jObj.types.Named("ComponentLink"),
            function (sourceLink) {
                this.sourceLink = sourceLink;
                return require("Contrail/jContrail").link.components(sourceLink.getSource(), this);
            });

        DestinationComponentWithSingleSource.prototype.getSource = jObj.method([], jObj.types.Named("SourceComponent"),
            function () {
                var result;

                if (this.sourceLink !== null) {
                    result = this.sourceLink.getSource();
                } else {
                    jObj.throwError(jObj.exception("L.source.not.connected"));
                }

                return result;
            });

        DestinationComponentWithSingleSource.prototype.closeDownStream = jObj.procedure([],
            function () {
                this.getSource().closeDownStream();
                this.sourceLink = null;
            });

        DestinationComponentWithSingleSource.prototype.getDownStreamDataFlow = jObj.method([], jObj.types.Named("DataFlow"),
            function () {
                return this.getSource().getDownStreamDataFlow();
            });

        return DestinationComponentWithSingleSource.init;
    });