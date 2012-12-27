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

require([ "Contrail/jContrail", "Core/object/jObj", "qunit", "test/jCC" ],
    function (Factory, jObj, QUnit, jCC) {
        "use strict";

        QUnit.test("Check Component generation", function () {
            var c1, c2;

            jCC.
                Given(function () {
                    c1 = Factory.component.core.pipeline();
                }).
                And(function () {
                    c2 = Factory.component.core.pipeline();
                }).
                WhenNothing.
                Then(function () {
                    QUnit.notEqual(c1.getComponentId(), c2.getComponentId(), "Two fresh components must be different");
                });
        });

        QUnit.test("Check Component type to be a PipelineComponent", function () {
            var c1;

            jCC.
                Given(function () {
                    c1 = Factory.component.core.pipeline();
                }).
                WhenNothing.
                Then(function () {
                    QUnit.equal(jObj.ofType(c1, jObj.types.Named("PipelineComponent")), true, "Checking c1 instance of PipelineComponent");
                });
        });

        QUnit.test("Check Component type to be a SourceComponent", function () {
            var c1;

            jCC.
                Given(function () {
                    c1 = Factory.component.core.pipeline();
                }).
                WhenNothing.
                Then(function () {
                    QUnit.equal(jObj.ofType(c1, jObj.types.Named("SourceComponent")), true, "Checking c1 instance of SourceComponent");
                });
        });

        QUnit.test("Check Component type to be a DestinationComponent", function () {
            var c1;

            jCC.
                Given(function () {
                    c1 = Factory.component.core.pipeline();
                }).
                WhenNothing.
                Then(function () {
                    QUnit.equal(jObj.ofType(c1, jObj.types.Named("DestinationComponent")), true, "Checking c1 instance of DestinationComponent");
                });
        });

        QUnit.test("Check Component type to be a Component", function () {
            var c1;

            jCC.
                Given(function () {
                    c1 = Factory.component.core.pipeline();
                }).
                WhenNothing.
                Then(function () {
                    QUnit.equal(jObj.ofType(c1, jObj.types.Named("Component")), true, "Checking c1 instance of Component");
                });
        });

        QUnit.test("Check Component type to be a SourceComponent and a DestinationComponent", function () {
            var c1;

            jCC.
                Given(function () {
                    c1 = Factory.component.core.pipeline();
                }).
                WhenNothing.
                Then(function () {
                    QUnit.equal(jObj.ofTypes(c1, [ jObj.types.Named("SourceComponent"), jObj.types.Named("DestinationComponent")]), true, "Checking c1 instance of SourceComponent and DestinationComponent");
                });
        });
    });