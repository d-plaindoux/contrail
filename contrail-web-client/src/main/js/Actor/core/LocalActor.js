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

define([ "Core/object/jObj", "./BoundActor" ],
    function (jObj, bindActor) {
        "use strict";

        function LocalActor(actor, model) {
            jObj.bless(this, actor, bindActor());

            this.model = model;
        }

        LocalActor.init = jObj.constructor([ jObj.types.Named("Actor"), jObj.types.Object ],
            function (actor, model) {
                return new LocalActor(actor, model);
            });

        LocalActor.prototype.askNow = jObj.procedure([ jObj.types.Named("Request"), jObj.types.Nullable(jObj.types.Named("Response"))],
            function (request, response) {
                try {
                    var result, method = this.model[request.getName()];

                    if (method) {
                        jObj.checkType(method, jObj.types.Function);
                        result = method.apply(this.model, request.getParameters());

                        if (response) {
                            response.success(result);
                        }
                    } else if (this.model.receiveRequest) {
                        this.model.receiveRequest(request, response);
                    } else {
                        jObj.throwError(jObj.exception("L.service?not.found"));
                    }
                } catch (error) {
                    if (response) {
                        response.failure(error);
                    } else {
                        jObj.throwError(error);
                    }
                }
            });

        return LocalActor.init;
    });
