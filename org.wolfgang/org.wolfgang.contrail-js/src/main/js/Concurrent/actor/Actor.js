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

define([ "Core/object/jObj" ],
    function (jObj) {
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

        Actor.prototype.send = jObj.procedure([ jObj.types.Named("Request"), jObj.types.Nullable(jObj.types.Named("Response"))],
            function (request, response) {
                var self = this;
                this.jobs.push(function () {
                    self.invoke(request, response);
                });
            });

        Actor.prototype.invoke = jObj.procedure([ jObj.types.Named("Request"), jObj.types.Nullable(jObj.types.Named("Response"))],
            function (request, response) {
                try {
                    var returnValue = this[request.getName()](request.getParameters());
                    if (response !== undefined) {
                        response.success(returnValue);
                    }
                } catch (error) {
                    if (response !== undefined) {
                        response.failure(error);
                    }
                }
            });

        /*
         * Management corner ...
         */

        Actor.prototype.activate = jObj.procedure([],
            function () {
                this.manager.register(this);
            });

        Actor.prototype.suspend = jObj.procedure([],
            function () {
                this.manager.unregister(this);
            });

        Actor.prototype.dispose = jObj.procedure([],
            function () {
                this.manager.disposeActor(this.getActorId());
            });

        return Actor.init;
    });
