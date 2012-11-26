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

        var CompositionFactory = {}, linkAll, sourceType, destinationType;

        // Static and private definitions

        sourceType = jObj.types.Named("SourceComponent");
        destinationType = jObj.types.Named("DestinationComponent");

        /**
         * Private method dedicated to composition creation
         *
         * @param linkManager
         * @param components
         * @return {*}
         */
        linkAll = jObj.method([jObj.types.Named("ComponentLinkManager"), jObj.types.Array ], jObj.types.Array,
            function (linkManager, components) {
                var index, current = components[0];

                for (index = 1; index < components.length; index += 1) {
                    linkManager.link(current, components[index]);
                    current = components[index];
                }

                return components;
            });

        CompositionFactory.compose = jObj.method([jObj.types.Named("ComponentLinkManager"), jObj.types.Array ], jObj.types.Named("Component"),
            function (linkManager, components) {
                var result, first, last;

                if (components.length === 0) {

                    throw jObj.exception("L.no.components.available");

                } else if (components.length === 1) {

                    result = components[0];

                } else {
                    first = components[0];
                    last = components[components.length - 1];

                    if (jObj.ofTypes(first, [ sourceType, destinationType]) &&
                        jObj.ofTypes(last, [ sourceType, destinationType ])) {

                        result = pipelineComposition(linkManager, linkAll(linkManager, components));

                    } else if (jObj.ofType(first, sourceType) &&
                        jObj.ofTypes(last, [ sourceType, destinationType ])) {

                        result = sourceComposition(linkManager, linkAll(linkManager, components));

                    } else if (jObj.ofTypes(first, [ sourceType, destinationType ]) &&
                        jObj.ofType(last, destinationType)) {

                        result = destinationComposition(linkManager, linkAll(linkManager, components));

                    } else if (jObj.ofType(first, sourceType) &&
                        jObj.ofType(last, jObj.types.Named("DestinationComponent"))) {

                        result = componentComposition(linkManager, linkAll(linkManager, components));

                    } else {
                        throw jObj.exception("L.not.compatible.components");
                    }
                }

                return result;
            });

        return CompositionFactory;

    })
;