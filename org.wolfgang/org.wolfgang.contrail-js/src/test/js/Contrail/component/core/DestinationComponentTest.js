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

require([ "Contrail/Factory", "Core/jObj", "qunit", "test/jCC" ],
    function (Factory, jObj, QUnit, jCC) {
        "use strict";

        /**
         * Test generation
         */
        jCC.scenario("Check Component generation", function () {
            var c1, c2;

            jCC.
                Given(function () {
                    c1 = Factory.component.core.destination();
                }).
                And(function () {
                    c2 = Factory.component.core.destination();
                }).
                WhenNothing.
                Then(function () {
                    QUnit.notEqual(c1.getComponentId(), c2.getComponentId(), "Two fresh components must be different");
                });
        });

        /**
         * Test source
         */
        jCC.scenario("Check Component source linkage acceptation", function () {
            var c1, c2;

            jCC.
                Given(function () {
                    c1 = Factory.component.core.destinationWithSingleSource();
                }).
                And(function () {
                    c2 = Factory.component.core.sourceWithSingleDestination();
                }).
                WhenNothing.
                Then(function () {
                    QUnit.equal(c1.acceptSource(c2.getComponentId()), true, "Source must be unbound");
                });
        });

        /**
         * Test source
         */
        jCC.scenario("Check Component source linkage", function () {
            var c1, c2, lm;

            jCC.
                Given(function () {
                    c1 = Factory.component.core.destinationWithSingleSource();
                }).
                And(function () {
                    c2 = Factory.component.core.sourceWithSingleDestination();
                }).
                And(function () {
                    lm = Factory.link;
                }).
                WhenNothing.
                Then(function () {
                    QUnit.equal(c1.acceptSource(c2.getComponentId()), true, "Source must be unbound");
                }).
                When(function () {
                    lm.connect(c2, c1);
                }).
                Then(function () {
                    QUnit.equal(c1.acceptSource(c2.getComponentId()), false, "Source must be setup");
                });

        });

        /**
         * Test type
         */
        jCC.scenario("Check Component type to be a DestinationComponent", function () {
            var c1;

            jCC.
                Given(function () {
                    c1 = Factory.component.core.destinationWithSingleSource();
                }).
                WhenNothing.
                Then(function () {
                    QUnit.equal(jObj.ofType(c1, jObj.types.Named("DestinationComponent")), true, "Checking c1 instance of DestinationComponent");
                });
        });

        jCC.scenario("Check Component type to be a Component", function () {
            var c1;

            jCC.
                Given(function () {
                    c1 = Factory.component.core.destination();
                }).
                WhenNothing.
                Then(function () {
                    QUnit.equal(jObj.ofType(c1, jObj.types.Named("Component")), true, "Checking c1 instance of Component");
                });
        });
    });