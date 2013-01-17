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

define([ "External/jSocketLib", "require", "Core/object/jObj", "Contrail/jContrail"],
    function (SocketLib, require, jObj, Contrail) {
        "use strict";

        function ServerSocket(port, handler) {
            jObj.bless(this);

            this.handler = handler;
            this.server = SocketLib.server(port, {accept:this.accept});
        }

        ServerSocket.init = jObj.constructor([ jObj.types.Number, jObj.types.Named("DataFlowHandler") ],
            function (port, handler) {
                return new ServerSocket(port, handler);
            });

        ServerSocket.prototype.accept = jObj.method([ jObj.types.ObjectOf({send:jObj.types.Function}) ],
            jObj.types.ObjectOf({onclose:jObj.types.Function, onmessage:jObj.types.Function}),
            function (client) {
                var dataFlowIn, dataFlowOut;

                dataFlowOut = Contrail.flow.core();
                dataFlowOut.handleData = jObj.procedure([ jObj.types.Any ],
                    function (data) {
                        client.send(data);
                    });

                dataFlowIn = this.handler.handle(dataFlowOut);

                return {
                    onmessage:dataFlowIn.handleData,
                    onclose:dataFlowIn.handleClose
                };
            });

        ServerSocket.prototype.close = jObj.procedure([], function () {
            this.server.close();
        });

        return ServerSocket.init;
    });
