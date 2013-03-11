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

/*global define, setInterval, clearInterval*/

define([ "Core/object/jObj", "./Actor" ],
    function (jObj, actor) {
        "use strict";

        function Coordinator() {
            jObj.bless(this);

            this.universe = {};
            this.activeActors = [];
            this.pendingJobs = [];

            this.interval = 100 /*ms*/;

            this.jobRunnerInterval = undefined;
            this.actorRunnerInterval = undefined;
        }

        Coordinator.init = jObj.constructor([], function () {
            return new Coordinator();
        });

        /*
         * Remote actor handler
         */

        Coordinator.prototype.setRemoteActorHandler = jObj.procedure([jObj.types.Named("RemoteActorHandler")],
            function (remoteActorHandler) {
                this.remoteActorHandler = remoteActorHandler;
            });

        Coordinator.prototype.remote = jObj.method([ jObj.types.String ], jObj.types.Object,
            function (location) {
                var self = this;

                return {
                    actor:jObj.method([ jObj.types.String ], jObj.types.Object,
                        function (identifier) {
                            return {
                                send:jObj.procedure([jObj.types.Named("Request"), jObj.types.Nullable(jObj.types.Named("Response"))],
                                    function (request, response) {
                                        self.getRemoteActorHandler().handle(location, identifier, request, response);
                                    })
                            };
                        })
                };
            });

        Coordinator.prototype.getRemoteActorHandler = jObj.method([], jObj.types.Named("RemoteActorHandler"),
            function () {
                return this.remoteActorHandler;
            });

        /*
         * Coordinator management
         */

        Coordinator.prototype.start = jObj.method([], jObj.types.Named("Coordinator"),
            function () {
                var self = this;

                if (this.jobRunnerInterval === undefined) {
                    this.jobRunnerInterval = setInterval(function () {
                        self.jobRunner();
                    }, this.interval);
                }
                if (this.actorRunnerInterval === undefined) {
                    this.actorRunnerInterval = setInterval(function () {
                        self.actorRunner();
                    }, this.interval);
                }

                return this;
            });

        Coordinator.prototype.stop = jObj.procedure([],
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

        Coordinator.prototype.jobRunner = function () {
            if (this.pendingJobs.length !== 0) {
                this.pendingJobs.shift()();
            }
        };

        Coordinator.prototype.actorRunner = function () {
            var self = this;
            this.activeActors.forEach(function (actor) {
                if (actor.pendingJobs.length !== 0) {
                    self.pendingJobs.push(actor.pendingJobs.shift());
                }
            });
        };

        /*
         * Actor (un)registration features
         */

        Coordinator.prototype.activateActor = jObj.procedure([jObj.types.Named("Actor")],
            function (actor) {
                this.activeActors.push(actor);
            });

        Coordinator.prototype.deactivateActor = jObj.procedure([jObj.types.String],
            function (identifier) {
                this.activeActors = this.activeActors.filter(function (actor) {
                    return actor.identifier !== identifier;
                });
            });

        /*
         * Actor creation and deletion
         */

        Coordinator.prototype.actor = jObj.method([jObj.types.String], jObj.types.Named("Actor"),
            function (identifier) {
                var anActor = actor(this, identifier);
                this.universe[anActor.getIdentifier()] = anActor;
                return anActor;
            });

        Coordinator.prototype.registerActor = jObj.procedure([jObj.types.Named("Actor")],
            function (anActor) {
                this.universe[anActor.getIdentifier()] = anActor;
                this.activateActor(anActor);
            });

        Coordinator.prototype.disposeActor = jObj.procedure([jObj.types.String],
            function (id) {
                this.deactivateActor(id);
                delete this.universe[id];
            });

        /*
         * Send and broadcast mechanisms
         */

        Coordinator.prototype.send = jObj.procedure([jObj.types.String, jObj.types.Named("Request"), jObj.types.Nullable(jObj.types.Named("Response"))],
            function (identifier, request, response) {
                if (this.universe.hasOwnProperty(identifier)) {
                    this.universe[identifier].send(request, response);
                } else {
                    if (response) {
                        response.failure(jObj.exception("L.actor.not.found"));
                    }
                }
            });

        Coordinator.prototype.invoke = jObj.procedure([jObj.types.String, jObj.types.Named("Request"), jObj.types.Nullable(jObj.types.Named("Response"))],
            function (identifier, request, response) {
                if (this.universe.hasOwnProperty(identifier)) {
                    this.universe[identifier].invoke(request, response);
                } else {
                    if (response) {
                        response.failure(jObj.exception("L.actor.not.found"));
                    }
                }
            });

        Coordinator.prototype.broadcast = jObj.procedure([jObj.types.Named("Request")],
            function (request) {
                var identifier;
                for (identifier in this.universe) {
                    if (this.universe.hasOwnProperty(identifier)) {
                        this.universe[identifier].send(request);
                    }
                }
            });

        return Coordinator.init;
    });
