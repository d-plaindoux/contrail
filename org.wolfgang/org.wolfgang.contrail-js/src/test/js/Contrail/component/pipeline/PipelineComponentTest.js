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

/*global require */

require([ "Contrail/Factory", "Core/jObj", "qunit" ],
    function (Factory, jObj, QUnit) {
        "use strict";

        QUnit.test("Check Component generation", function () {
            var c1 = Factory.component.pipeline.component(),
                c2 = Factory.component.pipeline.component();

            QUnit.notEqual(c1.getComponentId(), c2.getComponentId(), "Two fresh components must be different");
        });

        QUnit.test("Check Component type #1", function () {
            var c1 = Factory.component.pipeline.component();

            QUnit.equal(jObj.instanceOf(c1, "PipelineComponent"), true, "Checking c1 instance of PipelineComponent");
        });

        QUnit.test("Check Component type #2", function () {
            var c1 = Factory.component.pipeline.component();

            QUnit.equal(jObj.instanceOf(c1, "SourceComponent"), true, "Checking c1 instance of SourceComponent");
        });

        QUnit.test("Check Component type #3", function () {
            var c1 = Factory.component.pipeline.component();

            QUnit.equal(jObj.instanceOf(c1, "DestinationComponent"), true, "Checking c1 instance of DestinationComponent");
        });

        QUnit.test("Check Component type #4", function () {
            var c1 = Factory.component.pipeline.component();

            QUnit.equal(jObj.instanceOf(c1, "Component"), true, "Checking c1 instance of Component");
        });
    });