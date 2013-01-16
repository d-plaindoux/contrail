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

/*global define:true, require, module, setInterval, clearInterval*/

if (typeof define !== "function") {
    var define = require("amdefine")(module);
}

define([ "Core/object/jObj", "./Actor" ],
    function (jObj, actor) {
        "use strict";

        function ActorManager() {
            jObj.bless(this);

            this.universe = {};
            this.actors = [];
            this.jobs = [];

            this.interval = 100;
            this.jobRunnerInterval = undefined;
            this.actorRunnerInterval = undefined;
        }

        ActorManager.init = jObj.constructor([], function () {
            return new ActorManager();
        });

        ActorManager.prototype.start = jObj.procedure([],
            function () {
                var self = this;

                if (this.jobRunnerInterval === undefined) {
                    this.jobRunnerInterval = setInterval(function () {
                            self.jobRunner();
                        }, this.interval
                    );
                }
                if (this.actorRunnerInterval === undefined) {
                    this.actorRunnerInterval = setInterval(function () {
                        self.actorRunner();
                    }, this.interval);
                }
            });

        ActorManager.prototype.stop = jObj.procedure([],
            function () {
                if (this.jobRunnerInterval !== undefined) {
                    clearInterval(this.jobRunnerInterval);
                    this.jobRunnerInterval = undefined;
                }
                if (this.actorRunnerInterval !== undefined) {
                    clearInterval(this.actorRunnerInterval);
                    this.actorRunnerInterval = undefined;
                }
            });

        /*
         * Privates method
         */

        ActorManager.prototype.jobRunner = function () {
            if (this.jobs.length !== 0) {
                this.jobs.shift()();
            }
        };

        ActorManager.prototype.actorRunner = function () {
            var self = this;
            this.actors.forEach(function (actor) {
                if (actor.jobs.length !== 0) {
                    self.jobs.push(actor.jobs.shift());
                }
            });
        };

        /*
         * Actor (un)registration features
         */

        ActorManager.prototype.register = jObj.procedure([jObj.types.Named("Actor")],
            function (actor) {
                this.actors.push(actor);
            });

        ActorManager.prototype.unregister = jObj.procedure([jObj.types.Named("Actor")],
            function (actor) {
                this.actors = this.actors.filter(function (a) {
                    return a.actorId !== actor.actorId;
                });
            });

        /*
         * Actor creation and deletion
         */

        ActorManager.prototype.actor = jObj.method([jObj.types.String, jObj.types.Object], jObj.types.Named("Actor"),
            function (identifier, model) {
                var freshActor = actor(this, identifier, model);
                this.universe[identifier] = freshActor;
                return freshActor;
            });

        ActorManager.prototype.findActorById = jObj.method([ jObj.types.String ], jObj.types.Named("Actor"),
            function (id) {
                return this.universe[id] || jObj.raise(jObj.exception("L.actor.not.found")); // O(log(n))
            });

        ActorManager.prototype.disposeActor = jObj.procedure([jObj.types.String],
            function (id) {
                delete this.universe[id];
            });

        return ActorManager.init;
    });
