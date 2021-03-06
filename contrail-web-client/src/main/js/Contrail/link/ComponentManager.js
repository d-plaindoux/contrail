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

define([ "require", "Core/object/jObj" ],
    function (require, jObj) {
        "use strict";

        return jObj.procedure([jObj.types.Named("SourceComponent"), jObj.types.Named("DestinationComponent")],
            function (source, destination) {
                if (!source.acceptDestination(destination.getComponentId())) {
                    jObj.throwError(jObj.exception("L.source.cannot.accept.destination"));
                } else if (!destination.acceptSource(source.getComponentId())) {
                    jObj.throwError(jObj.exception("L.destination.cannot.accept.source"));
                } else {
                    var Factory = require("Contrail/jContrail");
                    source.connectDestination(Factory.link.destination(destination));
                    destination.connectSource(Factory.link.source(source));
                }
            });
    });