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

define("Core/ServerSocket", [ "External/SocketFactory", "require", "Core/jObj"],
    function (WebSocket, require, jObj) {
        "use strict";

        function ServerSocket(port, builder) {
            jObj.bless(this);
            this.builder = builder;
            this.server = WebSocket.server(port, {accept:this.accept});
        }

        ServerSocket.init = jObj.constructor([ jObj.types.Number, jObj.types.Named("DataFlowBuilder") ],
            function (port, builder) {
                return new ServerSocket(port, builder);
            });

        ServerSocket.prototype.accept = jObj.method([ jObj.types.Object ], jObj.types.ObjectOf({onclose:jObj.types.Function, onmessage:jObj.types.Function}),
            function (client) {
                var dataFlow = this.builder.create(client);

                return {
                    onmessage:dataFlow.handleData,
                    onclose:dataFlow.handleClose()
                };
            });

        ServerSocket.prototype.close = jObj.procedure([], function () {
            this.server.close();
        });

        return ServerSocket.init;
    });
