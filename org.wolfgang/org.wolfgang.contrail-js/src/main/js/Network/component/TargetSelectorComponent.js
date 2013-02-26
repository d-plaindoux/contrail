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

define([ "Core/object/jObj", "Contrail/jContrail", "./flow/TargetSelectorComponentUpStreamDataFlow", "./flow/TargetSelectorComponentDownStreamDataFlow" ],
    function (jObj, jContrail, routerUpStreamFlow, routerDownStreamFlow) {
        "use strict";

        function TargetSelectorComponent(identifier) {
            jObj.bless(this, jContrail.component.pipeline());

            this.destinationId = identifier;
            this.upStreamDataFlow = routerUpStreamFlow(this);
            this.downStreamDataFlow = routerDownStreamFlow(this);
        }

        TargetSelectorComponent.init = jObj.constructor([ jObj.types.String ],
            function (identifier) {
                return new TargetSelectorComponent(identifier);
            });

        TargetSelectorComponent.prototype.getIdentifier = jObj.method([ ], jObj.types.String,
            function () {
                return this.destinationId;
            });

        TargetSelectorComponent.prototype.getUpStreamDataFlow = jObj.method([], jObj.types.Named("DataFlow"),
            function () {
                return this.upStreamDataFlow;
            });

        TargetSelectorComponent.prototype.getDownStreamDataFlow = jObj.method([], jObj.types.Named("DataFlow"),
            function () {
                return this.downStreamDataFlow;
            });

        return TargetSelectorComponent.init;
    });
