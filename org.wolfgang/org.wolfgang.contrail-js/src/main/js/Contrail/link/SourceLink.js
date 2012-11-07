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

define([ "require", "Core/jObj" ],
    function (require, jObj) {
        "use strict";

        function SourceLink(source, linkManager) {
            jObj.bless(this, require("Contrail/Factory").link.basic(linkManager));

            this.source = source;
        }

        SourceLink.init = jObj.constructor([jObj.types.Named("SourceComponent"), jObj.types.Named("ComponentLinkManager")], function (source, linkManager) {
            return new SourceLink(source, linkManager);
        });

        SourceLink.prototype.getSource = jObj.method([], jObj.types.Named("SourceComponent"), function () {
            return this.source;
        });

        return SourceLink.init;
    });