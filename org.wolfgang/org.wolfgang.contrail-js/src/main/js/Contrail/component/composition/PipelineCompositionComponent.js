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

define(["require", "Core/object/jObj", "./SourceCompositionComponent", "./DestinationCompositionComponent" ],
    function (require, jObj, source, destination) {
        "use strict";

        var PipelineCompositionComponent = function (components) {
            jObj.bless(this, source(components), destination(components));
            this.components = components;
        };

        PipelineCompositionComponent.init = jObj.constructor([ jObj.types.ArrayOf(jObj.types.Named("Component")) ],
            function (components) {
                return new PipelineCompositionComponent(components);
            });

        return PipelineCompositionComponent.init;
    });
