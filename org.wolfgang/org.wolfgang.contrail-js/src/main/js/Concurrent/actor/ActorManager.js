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

define([ "../../Core/object/jObj", "./Actor" ],
    function (jObj, actor) {
        "use strict";

        var ActorManager = function () {
            jObj.bless(this);

            this.actors = [];
            this.jobs = [];

            setInterval(this.jobRunner, 100);
            setInterval(this.actorRunner, 100);
        };

        ActorManager.init = jObj.constructor([], function () {
            return new ActorManager();
        });

        /*
         * Privates method
         */

        ActorManager.prototype.jobRunner = function () {
            if (this.jobs.length < 1) {
                return;
            }

            return this.jobs.shift()();
        };

        ActorManager.prototype.actorRunner = function () {
            var actor;

            if (this.actors.length === 0) {
                return;
            }

            actor = this.actors.shift();

            while (actor.jobs.length) {
                this.jobs.push(actor.jobs.shift());
            }
        };

        /*
         * Actor manager (un)registration features
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
         * Actor factory
         */

        ActorManager.prototype.actor = jObj.method([], jObj.types.Named("Actor"),
            function () {
                return actor(this);
            });

        return ActorManager.init;
    });
