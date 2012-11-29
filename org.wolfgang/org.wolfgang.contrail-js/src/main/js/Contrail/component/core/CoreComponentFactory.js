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

define([ "./Component", "./SourceComponent", "./SourceComponentWithSingleDestination", "./DestinationComponent", "./DestinationComponentWithSingleSource" ],
    function (component, sourceComponent, sourceComponentWithSingleDestination, destinationComponent, destinationComponentWithSingleSource) {
        "use strict";

        var Factory = {};

        Factory.component = function () {
            return component();
        };

        Factory.source = function () {
            return sourceComponent();
        };

        Factory.destination = function () {
            return destinationComponent();
        };

        Factory.sourceWithSingleDestination = function () {
            return sourceComponentWithSingleDestination();
        };

        Factory.destinationWithSingleSource = function () {
            return destinationComponentWithSingleSource();
        };

        return Factory;

    });