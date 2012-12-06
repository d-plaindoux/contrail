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

define("Core/Socket", ["External/WebSocket", "require", "Core/jObj"],
    function (WebSocket, require, jObj) {
        "use strict";

        function Socket(host, dataFlow) {
            jObj.bless(this);

            this.socket = new WebSocket(host);

            this.socket.onopen = function () {
                // Review -- TODO
            };

            this.socket.onerror = function (evt) {
                // Review -- TODO
            };

            this.socket.onclose = function () {
                dataFlow.handleClose();
            };

            this.socket.onmessage = function (data) {
                dataFlow.handleData(data);
            };
        }

        Socket.init = jObj.constructor([ jObj.types.String, jObj.types.Named("DataFlow") ],
            function (host, dataFlow) {
                return new Socket(host, dataFlow);
            });

        Socket.prototype.send = jObj.procedure([ jObj.types.Any],
            function (data) {
                this.socket.send(data);
            });

        Socket.prototype.close = jObj.procedure([],
            function () {
                this.socket.close();
            });

        return Socket.init;
    });
