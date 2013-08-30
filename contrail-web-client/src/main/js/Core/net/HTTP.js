
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

define(["External/jSocketLib", "require", "Core/object/jObj"],
    function (SocketLib, require, jObj) {
        "use strict";

        function HTTP(endpoint) {
            jObj.bless(this);

            this.endPoint = endpoint;
            this.client = null;
            this.pendingMessages = null;
        }

        HTTP.init = jObj.constructor([ jObj.types.String ],
            function (endPoint) {
                return new HTTP(endPoint);
            });

        HTTP.prototype.connect = jObj.procedure([ jObj.types.Named("DataFlow"), jObj.types.Nullable(jObj.types.Function) ],
            function (dataFlow, callback) {
                this.pendingMessages = [];

                if (this.client) {
                    jObj.throwError(jObj.exception("L.web.socket.already.opened"));
                } else {
                    var self = this;

                    this.client = SocketLib.client(this.endPoint, {
                        onopen:function () {
                            if (callback) {
                                callback();
                            }

                            self.pendingMessages.forEach(function (message) {
                                self.client.send(message);
                            });

                            self.pendingMessages = null;
                        },
                        onerror:function (error) {
                            dataFlow.handleClose();
                        },
                        onclose:function () {
                            dataFlow.handleClose();
                        },
                        onmessage:function (message) {
                            if (message.data) {
                                dataFlow.handleData(message.data);
                            }
                        }
                    });
                }
            });

        HTTP.prototype.isOpen = jObj.method([], jObj.types.Boolean,
            function () {
                return this.client && this.client.isOpen();
            });

        HTTP.prototype.isClosed = jObj.method([], jObj.types.Boolean,
            function () {
                return this.client && this.client.isClosed();
            });

        HTTP.prototype.send = jObj.procedure([ jObj.types.String ],
            function (message) {
                if (this.isOpen()) {
                    this.client.send(message);
                } else if (this.pendingMessages) {
                    this.pendingMessages.push(message);
                } else if (this.isClosed()) {
                    jObj.throwError(jObj.exception("L.web.socket.closed"));
                } else {
                    jObj.throwError(jObj.exception("L.web.socket.not.established"));
                }
            });

        HTTP.prototype.close = jObj.procedure([],
            function () {
                if (this.client) {
                    this.client.close();
                    this.pendingMessages = null;
                }
            });

        return HTTP.init;

    });
