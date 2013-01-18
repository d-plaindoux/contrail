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

/*global define:true, require, module, setInterval*/

if (typeof define !== "function") {
    var define = require("amdefine")(module);
}

define([ "Core/object/jObj", "Concurrent/event/jEvent" ],
    function (jObj, jEvent) {
        "use strict";

        function Actor(manager, identifier, model) {
            jObj.bless(this, model);

            this.actorId = identifier;
            this.manager = manager;
            this.jobs = [];
        }

        Actor.init = jObj.constructor([ jObj.types.Named("ActorManager"), jObj.types.String, jObj.types.Object ],
            function (manager, identifier, model) {
                return new Actor(manager, identifier, model);
            });

        Actor.prototype.getActorId = jObj.method([], jObj.types.String,
            function () {
                return this.actorId;
            });

        Actor.prototype.send = jObj.method([ jObj.types.Named("Request"), jObj.types.Nullable(jObj.types.Named("Response"))], jObj.types.Named("Response"),
            function (request, response) {
                var self = this, realResponse;

                if (response === undefined) {
                    realResponse = jEvent.storedResponse();
                } else {
                    realResponse = response;
                }

                this.jobs.push(function () {
                    self.invoke(request, realResponse);
                });

                return realResponse;
            });

        Actor.prototype.invoke = jObj.procedure([ jObj.types.Named("Request"), jObj.types.Named("Response")],
            function (request, response) {
                try {
                    var method = this[request.getName()];
                    jObj.checkType(method, jObj.types.Function);
                    response.success(method(request.getParameters()));
                } catch (error) {
                    response.failure(error);
                }
            });

        /*
         * Management corner ...
         */

        Actor.prototype.activate = jObj.procedure([],
            function () {
                this.manager.registerActor(this);
            });

        Actor.prototype.suspend = jObj.procedure([],
            function () {
                this.manager.unregisterActor(this);
            });

        Actor.prototype.dispose = jObj.procedure([],
            function () {
                this.manager.disposeActor(this.getActorId());
            });

        return Actor.init;
    });
