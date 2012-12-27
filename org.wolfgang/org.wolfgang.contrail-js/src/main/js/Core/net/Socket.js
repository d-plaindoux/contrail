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

define(["External/SocketFactory", "require", "Core/object/jObj"],
    function (WebSocket, require, jObj) {
        "use strict";

        function Socket(endpoint, dataFlow) {

            jObj.bless(this);

            this.client = WebSocket.client(endpoint, {
                onopen:jObj.procedure([],
                    function () {
                        // Nothing
                    }),

                onerror:jObj.procedure([ jObj.types.Object ],
                    function (error) {
                        dataFlow.handleClose();
                    }),

                onclose:jObj.procedure([],
                    function () {
                        dataFlow.handleClose();
                    }),

                onmessage:jObj.procedure([ jObj.types.ObjectOf({data:jObj.types.Any}) ],
                    function (message) {
                        if (message.data) {
                            dataFlow.handleData(message.data);
                        }
                    })
            });
        }

        Socket.init = jObj.constructor([ jObj.types.String, jObj.types.Named("DataFlow") ],
            function (endPoint, dataFlow) {
                return new Socket(endPoint, dataFlow);
            });

        Socket.prototype.ensureOpen = jObj.procedure([ ],
            function () {
                if (!this.client.isOpen()) {
                    if (this.client.isClosed()) {
                        throw jObj.exception("L.web.socket.closed");
                    } else {
                        throw jObj.exception("L.web.socket.not.established");
                    }
                }
            });

        Socket.prototype.isOpen = jObj.method([], jObj.types.Boolean,
            function () {
                return this.client.isOpen();
            });

        Socket.prototype.send = jObj.procedure([ jObj.types.String ],
            function (message) {
                this.ensureOpen();
                this.client.send(message);
            });

        Socket.prototype.close = jObj.procedure([],
            function () {
                this.client.close();
            });

        return Socket.init;

    });
