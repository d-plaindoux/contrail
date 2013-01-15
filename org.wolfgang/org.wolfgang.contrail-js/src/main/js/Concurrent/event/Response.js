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

        var status = {
            UNSET:0x0,
            SUCCESS:0x1,
            FAILURE:0x2
        };

        function Response() {
            jObj.bless(this);

            this.status = status.UNSET;
            this.result = undefined;
        }

        Response.init = jObj.constructor([],
            function () {
                return new Response();
            });

        Response.prototype.success = jObj.procedure([ jObj.types.Any ],
            function (value) {
                this.status = status.SUCCESS;
                this.result = value;
            });

        Response.prototype.failure = jObj.procedure([ jObj.types.Any ],
            function (error) {
                this.status = status.FAILURE;
                this.result = error;
            });

        Response.prototype.value = jObj.method([], jObj.types.Any,
            function () {
                switch (this.status) {
                    case status.UNSET:
                        throw jObj.exception("L.response.not.yet.setup");
                    case status.FAILURE:
                        throw this.result;
                    case status.SUCCESS:
                        return this.result; // Value can be null ! So How can be this handled by the current system
                }
            });

        return Response.init;
    });
