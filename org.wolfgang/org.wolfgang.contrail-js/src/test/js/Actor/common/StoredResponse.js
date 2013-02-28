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

define([ "Core/object/jObj", "Actor/jActor" ],
    function (jObj, jActor) {
        "use strict";

        var status = {
            UNSET:0x0,
            SUCCESS:0x1,
            FAILURE:0x2
        };

        function StoredResponse() {
            jObj.bless(this, jActor.event.response(this.success, this.failure));

            this.status = status.UNSET;
            this.result = undefined;
        }

        StoredResponse.init = jObj.constructor([],
            function () {
                return new StoredResponse();
            });

        StoredResponse.prototype.success = jObj.procedure([ jObj.types.Any ],
            function (value) {
                this.status = status.SUCCESS;
                this.result = value;
            });

        StoredResponse.prototype.failure = jObj.procedure([ jObj.types.Any ],
            function (error) {
                this.status = status.FAILURE;
                this.result = error;
            });

        StoredResponse.prototype.value = jObj.method([], jObj.types.Any,
            function () {
                switch (this.status) {
                    case status.UNSET:
                        jObj.throwError(jObj.exception("L.stored.response.not.yet.setup"));
                        break;
                    case status.FAILURE:
                        jObj.throwError(this.result);
                        break;
                    case status.SUCCESS:
                        return this.result;
                }
            });

        return StoredResponse.init;
    });
