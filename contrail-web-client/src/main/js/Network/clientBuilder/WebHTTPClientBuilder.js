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

define([  "require", "Core/object/jObj", "Core/net/jSocket", "Contrail/jContrail", "./StandardClientBuilder" ],
    function (require, jObj, jSocket, jContrail, clientBuilder) {
        "use strict";

        function WebHTTPClientBuilder(endPoint, callback) {
            jObj.bless(this, clientBuilder(endPoint));

            this.callback = callback;
        }

        WebHTTPClientBuilder.init = jObj.constructor([ jObj.types.String, jObj.types.Nullable(jObj.types.Function)  ],
            function (endPoint, callback) {
                return new WebHTTPClientBuilder(endPoint, callback);
            });

        WebHTTPClientBuilder.prototype.activate = jObj.method([], jObj.types.Named("SourceComponent"),
            function () {
                var socket, component, initial;

                socket = jSocket.client(this.endPoint);

                initial = jContrail.component.initial(jContrail.flow.core(function (data) {
                    socket.send(data);
                }));

                component = jContrail.component.compose([ initial, this.getIntermediateComponent() ]);

                socket.connect(component.getUpStreamDataFlow(),this.callback);

                return component;
            });

        return WebHTTPClientBuilder.init;
    });
