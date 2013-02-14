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

/*global define, require, setInterval*/

define([ "Core/object/jObj", "Core/browser/jLoader", "./LocalActor", "./RemoteActor" ],
    function (jObj, jLoader, localActor, remoteActor) {
        "use strict";

        function Actor(coordinator, identifier) {
            jObj.bless(this);

            this.identifier = identifier;
            this.coordinator = coordinator;
            this.pendingJobs = [];
        }

        Actor.init = jObj.constructor([ jObj.types.Named("Coordinator"), jObj.types.String ],
            function (coordinator, identifier) {
                return new Actor(coordinator, identifier);
            });

        Actor.prototype.getIdentifier = jObj.method([], jObj.types.String,
            function () {
                return this.identifier;
            });

        Actor.prototype.send = jObj.procedure([ jObj.types.Named("Request"), jObj.types.Nullable(jObj.types.Named("Response"))],
            function (request, response) {
                var self = this;
                this.pendingJobs.push(function () {
                    self.coordinator.invoke(self.getIdentifier(), request, response);
                });
            });

        Actor.prototype.invoke = jObj.procedure([ jObj.types.Named("Request"), jObj.types.Nullable(jObj.types.Named("Response"))],
            function (request, response) {
                jObj.throwError(jObj.exception("L.actor.not.yet.bind"));
            });

        /*
         * Management corner ...
         */

        Actor.prototype.activate = jObj.procedure([],
            function () {
                this.coordinator.activateActor(this);
            });

        Actor.prototype.suspend = jObj.procedure([],
            function () {
                this.coordinator.deactivateActor(this);
            });

        Actor.prototype.dispose = jObj.procedure([],
            function () {
                this.coordinator.disposeActor(this.identifier);
            });

        Actor.prototype.bindToSource = jObj.method([jObj.types.String], jObj.types.ObjectOf({withModule:jObj.types.Function, withCallback:jObj.types.Function}),
            function (source) {
                var self = this;

                return {
                    withModule:jObj.procedure([jObj.types.String, jObj.types.Array],
                        function (module, parameters) {
                            jLoader.load(source, function () {
                                    var anActor, callback;

                                    callback = require(module);

                                    if (callback) {
                                        anActor = localActor(self, callback.apply({}, parameters));
                                        self.coordinator.registerActor(anActor);
                                    } else {
                                        jObj.throwError(jObj.exception(("L.actor.to.source.undefined")));
                                    }
                                }
                            );
                        }),
                    withCallback:jObj.procedure([jObj.types.Function],
                        function (callback) {
                            jLoader.load(source, function () {
                                    var anActor;

                                    anActor = localActor(self, callback());
                                    self.coordinator.registerActor(anActor);
                                }
                            );
                        })
                };
            });

        Actor.prototype.bindToObject = jObj.method([jObj.types.Object], jObj.types.Named("Actor"),
            function (model) {
                var anActor = localActor(this, model);
                this.coordinator.registerActor(anActor);
                return anActor;
            });

        Actor.prototype.bindToRemote = jObj.method([jObj.types.String], jObj.types.Named("Actor"),
            function (location) {
                var anActor = remoteActor(this, location);
                this.coordinator.registerActor(anActor);
                return anActor;
            });

        return Actor.init;
    });
