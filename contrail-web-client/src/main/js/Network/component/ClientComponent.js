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

define([ "Core/object/jObj", "Contrail/jContrail", "./flow/client/ClientComponentDownStreamDataFlow", "./flow/client/ClientComponentUpStreamDataFlow" ],
    function (jObj, jContrail, clientDownStream, clientUpStream) {
        "use strict";

        function ClientComponent(endPoint) {
            jObj.bless(this, jContrail.component.pipeline());

            this.endPoint = endPoint;
            this.upStreamDataFlow = clientUpStream(this);
            this.downStreamDataFlow = clientDownStream(this);

            this.destinations = [];
            this.sources = [];
        }

        ClientComponent.init = jObj.constructor([ jObj.types.Nullable(jObj.types.String) ],
            function (identifier) {
                return new ClientComponent(identifier);
            });

        ClientComponent.prototype.getEndPoint = jObj.method([], jObj.types.Nullable(jObj.types.String),
            function () {
                return this.endPoint;
            });

        ClientComponent.prototype.addDestinationId = jObj.procedure([ jObj.types.String ],
            function (identifier) {
                if (!this.acceptDestinationId(identifier)) {
                    this.destinations.push(identifier);
                }
            });

        ClientComponent.prototype.acceptDestinationId = jObj.method([ jObj.types.String ], jObj.types.Boolean,
            function (identifier) {
                return this.destinations.some(function (id) {
                    return identifier === id;
                });
            });

        ClientComponent.prototype.addSourceId = jObj.procedure([ jObj.types.String ],
            function (identifier) {
                if (!this.acceptSourceId(identifier)) {
                    this.sources.push(identifier);
                }
            });

        ClientComponent.prototype.acceptSourceId = jObj.method([ jObj.types.String ], jObj.types.Boolean,
            function (identifier) {
                return this.sources.some(function (id) {
                    return identifier === id;
                });
            });

        ClientComponent.prototype.getUpStreamDataFlow = jObj.method([], jObj.types.Named("DataFlow"),
            function () {
                return this.upStreamDataFlow;
            });

        ClientComponent.prototype.getDownStreamDataFlow = jObj.method([], jObj.types.Named("DataFlow"),
            function () {
                return this.downStreamDataFlow;
            });

        return ClientComponent.init;
    });
