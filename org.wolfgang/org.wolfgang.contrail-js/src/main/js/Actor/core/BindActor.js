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

define([ "Core/object/jObj" ],
    function (jObj) {
        "use strict";

        var BindActor = function () {
            jObj.bless(this);
        };

        BindActor.init = jObj.constructor([ ],
            function () {
                return new BindActor();
            });

        BindActor.prototype.send = jObj.procedure([ jObj.types.Named("Request"), jObj.types.Nullable(jObj.types.Named("Response"))],
            function (request, response) {
                var self = this;
                this.pendingJobs.push(function () {
                    self.coordinator.invoke(self.getIdentifier(), request, response);
                });
            });

        BindActor.prototype.bindToSource = jObj.procedure([jObj.types.String, jObj.types.String, jObj.types.Array],
            function (source, module, parameters) {
                jObj.throwError(jObj.exception("L.actor.already.bind"));
            });

        BindActor.prototype.bindToObject = jObj.procedure([jObj.types.Object],
            function (model) {
                jObj.throwError(jObj.exception("L.actor.already.bind"));
            });

        BindActor.prototype.bindToRemote = jObj.procedure([jObj.types.String],
            function (location) {
                jObj.throwError(jObj.exception("L.actor.already.bind"));
            });

        return BindActor.init;
    });
