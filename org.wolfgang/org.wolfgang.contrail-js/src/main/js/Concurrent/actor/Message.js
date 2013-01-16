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

/*global define:true, require, module*/

if (typeof define !== "function") {
    var define = require("amdefine")(module);
}

define([ "Core/object/jObj" ],
    function (jObj) {
        "use strict";

        function Message(actorId, data) {
            jObj.bless(this);
            this.actorId = actorId;
            this.data = data;
        }

        Message.init = jObj.constructor([jObj.types.String, jObj.types.Or(jObj.types.Named("Request"), jObj.types.Named("Response"))],
            function (actorId, data) {
                return new Message(actorId, data);
            });

        Message.prototype.getActorId = jObj.method([], jObj.types.String,
            function () {
                return this.actorId;
            });

        Message.prototype.getData = jObj.method([], jObj.types.Or(jObj.types.Named("Request"), jObj.types.Named("Response")),
            function () {
                return this.data;
            });

        return Message.init;
    });