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

define([ "Core/utils/jUUID", "Core/object/jObj" ],
    function (jUUID, jObj) {
        "use strict";

        var Actor = function (manager) {
            jObj.bless(this);

            this.actorId = jUUID.generate();
            this.manager = manager;
            this.jobs = [];
        };

        Actor.init = jObj.constructor([ jObj.types.Named("ActorManager") ],
            function (manager) {
                return new Actor(manager);
            });

        Actor.prototype.send = jObj.procedure([ jObj.types.String, jObj.types.Array, jObj.types.Nullable(jObj.types.Named("Promise"))],
            function (name, parameters, promise) {
                var job = function () {
                    var returnValue;

                    try {
                        returnValue = this[name](parameters);
                        if (promise !== undefined) {
                            promise.success(returnValue);
                        }
                    } catch (e) {
                        if (promise !== undefined) {
                            promise.failure(e);
                        }
                    }
                };

                this.jobs.push(job);
            });

        Actor.prototype.start = function () {
            this.manager.register(this);
        };

        Actor.prototype.stop = function () {
            this.manager.unregister(this);
        };

        return Actor.init;
    });
