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

define([ "require", "Core/object/jObj", "./DisposableLink"],
    function (require, jObj, disposable) {
        "use strict";

        function DestinationLink(destination) {
            jObj.bless(this, disposable());
            this.destination = destination;
        }

        DestinationLink.init = jObj.constructor([jObj.types.Named("DestinationComponent")],
            function (destination) {
                return new DestinationLink(destination);
            });

        DestinationLink.prototype.getDestination = jObj.method([], jObj.types.Named("DestinationComponent"),
            function () {
                return this.destination;
            });

        DestinationLink.prototype.dispose = jObj.procedure([],
            function () {
                // TODO
            });


        return DestinationLink.init;
    });