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

/*global require */

require([ "jquery", "qunit", "Contrail/Factory", "Core/Socket", "test/jCC" ],
    function (jQuery, QUnit, Factory, socket, jCC) {
        "use strict";

        jCC.scenario("Trying code", function () {
            var dataFlow, client, i;

            jCC.
                Given(function () {
                    dataFlow = Factory.flow.accumulated();
                }).
                And(function () {
                    client = socket("ws://localhost:1337", dataFlow);
                }).
                When(function () {
                    client.send("Ping");
                }).
                Then(function () {
                    QUnit.equal(dataFlow.getAccumulation().length, 1);
                }).
                And(function () {
                    QUnit.equal(dataFlow.getAccumulation()[0], "Pong");
                });
        });

    });


/* NODE JS server side - npm install websocket required first
 ------------------------------------------------------------

 var http = require('http');

 var server = http.createServer(function(request, response) {
     // process HTTP request. Since we're writing just WebSockets server
     // we don't have to implement anything.
 });

 // create the server
 var WebSocketServer = require('websocket').server;
 var wsServer = new WebSocketServer({
     httpServer: server
 });

 // WebSocket server
 wsServer.on('request', function(request) {

     console.log((new Date()) + ' Accept ' + connection + '.');
     var connection = request.accept(null, request.origin);
     console.log((new Date()) + ' Start ' + connection + '.');

     // This is the most important callback for us, we'll handle
     // all messages from users here.
     connection.on('message', function(message) {
         console.log("RECV : " + message + "/n");
         connection.send("Pong");
     });

     connection.on('close', function(connection) {
         console.log((new Date()) + ' Finish ' + connection + '.');
         // close user connection
     });
 });

 server.listen(1337);
 */
