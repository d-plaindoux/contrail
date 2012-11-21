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

define([ "Core/jObj", "./PipelineCompositionComponent", "./SourceCompositionComponent", "./DestinationCompositionComponent", "./CompositionComponent" ],
    function (jObj, pipelineComposition, sourceComposition, destinationComposition, componentComposition) {
        "use strict";

        var CompositionFactory = {};

        CompositionFactory.compose = jObj.method([jObj.types.Named("ComponentLinkMananger"), jObj.types.Array ], jObj.types.Named("Component"),
            function (linkManager, components) {
                var result;

                if (components.length === 0) {

                    throw jObj.exception("L.no.components.available");

                } else if (components.length === 1) {

                    result = components[0];

                } else if (jObj.isATypes(components[0], [ jObj.types.Named("SourceComponent"), jObj.types.Named("DestinationComponent")]) &&
                    jObj.isATypes(components[components.length - 1], [ jObj.types.Named("SourceComponent"), jObj.types.Named("DestinationComponent") ])) {

                    result = pipelineComposition(linkManager, components);

                } else if (jObj.isATypes(components[0], [ jObj.types.Named("SourceComponent") ]) &&
                    jObj.isATypes(components[components.length - 1], [ jObj.types.Named("SourceComponent"), jObj.types.Named("DestinationComponent") ])) {

                    result = sourceComposition(linkManager, components);

                } else if (jObj.isATypes(components[0], [ jObj.types.Named("SourceComponent"), jObj.types.Named("DestinationComponent") ]) &&
                    jObj.isATypes(components[components.length - 1], [ jObj.types.Named("DestinationComponent")])) {

                    result = destinationComposition(linkManager, components);

                } else if (jObj.isATypes(components[0], [ jObj.types.Named("SourceComponent") ]) &&
                    jObj.isATypes(components[components.length - 1], [ jObj.types.Named("DestinationComponent") ])) {

                    result = componentComposition(linkManager, components);

                } else {
                    throw jObj.exception("L.not.compatible.components");
                }

                return result;
            });

        return CompositionFactory;

    });