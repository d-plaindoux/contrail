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

define([ "Core/jObj", "./DisposableLink"],
    function (jObj, disposable) {
        "use strict";

        function ComponentLink(source, destination) {
            jObj.bless(this, disposable());

            this.source = source;
            this.destination = destination;
        }

        ComponentLink.init = jObj.constructor([jObj.types.Named("SourceComponent"), jObj.types.Named("DestinationComponent")],
            function (source, destination) {
                return new ComponentLink(source, destination);
            });

        ComponentLink.prototype.dispose = jObj.procedure([],
            function () {
                // TODO
            });

        return ComponentLink.init;
    });