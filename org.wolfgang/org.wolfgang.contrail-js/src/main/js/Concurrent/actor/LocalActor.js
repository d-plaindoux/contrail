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

/*global define, setInterval*/

define([ "Core/object/jObj" ],
    function (jObj, actor) {
        "use strict";

        function LocalActor(actor, model) {
            jObj.bless(this, actor);

            this.model = model;
        }

        LocalActor.init = jObj.constructor([ jObj.types.Named("Actor"), jObj.types.Object ],
            function (actor, model) {
                return new LocalActor(actor, model);
            });

        LocalActor.prototype.invoke = jObj.procedure([ jObj.types.Named("Request"), jObj.types.Nullable(jObj.types.Named("Response"))],
            function (request, response) {
                try {
                    var result, method = this.model[request.getName()];
                    jObj.checkType(method, jObj.types.Function);

                    result = method.call(this.model, request.getParameters());

                    if (response !== undefined) {
                        response.success(result);
                    }
                } catch (error) {
                    if (response !== undefined) {
                        response.failure(error);
                    }
                }
            });

        LocalActor.prototype.bindToObject = jObj.procedure([jObj.types.Object],
            function (model) {
                jObj.throwError(jObj.exception("L.actor.already.bind.to.object"));
            });

        LocalActor.prototype.bindToRemote = jObj.procedure([jObj.types.String],
            function (location) {
                jObj.throwError(jObj.exception("L.actor.already.bind.to.object"));
            });

        return LocalActor.init;
    });
