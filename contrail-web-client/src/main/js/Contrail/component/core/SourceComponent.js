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

        function SourceComponent() {
            jObj.bless(this, require("Contrail/component/jComponent").core.component());
        }

        SourceComponent.init = jObj.constructor([],
            function () {
                return new SourceComponent();
            });

        /**
         * Abstract methods
         */

        SourceComponent.prototype.acceptDestination = jObj.method([jObj.types.String], jObj.types.Boolean);

        SourceComponent.prototype.connectDestination = jObj.method([jObj.types.Named("DestinationLink")], jObj.types.Named("ComponentLink"));

        SourceComponent.prototype.getDownStreamDataFlow = jObj.method([], jObj.types.Named("DataFlow"));

        return SourceComponent.init;
    });
