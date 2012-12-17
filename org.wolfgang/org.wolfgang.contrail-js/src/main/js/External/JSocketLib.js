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

/*global define, window*/

define("External/SocketFactory", [ "require", "Core/jObj" ],
    function (require, jObj) {
        "use strict";

        var Factory = {};

        Factory.client = jObj.method([ jObj.types.String, jObj.types.ObjectOf({onclose:jObj.types.Function, onmessage:jObj.types.Function}) ], jObj.types.Object,
            function (endpoint, callbacks) {
                var WebSocket, client;

                WebSocket = window.WebSocket || window.MozWebSocket || jObj.throwError(jObj.exception("L.web.socket.not.defined"));
                client = new WebSocket(endpoint);

                client.onopen = function () {
                    // Nothing for the moment
                };
                client.onerror = function () {
                    // Nothing for the moment
                };

                client.onmessage = callbacks.onmessage;
                client.onclose = callbacks.onclose;

                client.isOpen = function () {
                    return this.readyState === WebSocket.OPEN;
                };

                client.isClosed = function () {
                    return this.readyState === WebSocket.CLOSED || this.readyState === WebSocket.CLOSING;
                };

                return client;
            });

        /* NODE JS server side - "npm install websocket" - required first */
        Factory.server = jObj.method([ jObj.types.Number, jObj.types.ObjectOf({accept:jObj.types.Function}) ], jObj.types.Object,
            function (port, accept) {
                var httpServer, WebSocketServer, client;

                httpServer = require("http").createServer(function (request, response) {
                    // process HTTP request. Since we're writing just WebSockets server
                    // we don't have to implement anything.
                });

                WebSocketServer = require('websocket').server;
                client = new WebSocketServer({httpServer:httpServer});

                client.on('request', function (request) {
                    var connection, callbacks;

                    connection = request.accept(null, request.origin);
                    callbacks = accept(connection);

                    connection.on('message', callbacks.onmessage);
                    connection.on('close', callbacks.onclose);
                });

                httpServer.listen(port);

                return httpServer;
            });

        return Factory;

    });

