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

require([ "qunit", "Core/jObj", "Contrail/Factory" , "test/jCC"],
    function (QUnit, jObj, Factory, jCC) {
        "use strict";

        jCC.scenario("Create composition with 0 components", function () {
            var linkManager, compose;

            jCC.
                Given(function () {
                    linkManager = Factory.link.manager();
                }).
                When(function () {
                    compose = Factory.component.compose(linkManager, [ ]);
                }).
                ThenError(function (exception) {
                    QUnit.equal(true, true, "Expected Exception " + exception + " catched");
                });
        });

        jCC.scenario("Create composition with 1 components", function () {
            var component, linkManager, compose;

            jCC.
                Given(function () {
                    component = Factory.component.core.source();
                }).
                And(function () {
                    linkManager = Factory.link.manager();
                }).
                When(function () {
                    compose = Factory.component.compose(linkManager, [ component ]);
                }).
                Then(function () {
                    QUnit.equal(compose, component, " Composition One component => the component itself");
                });
        });


        jCC.scenario("Create composition with 2 components - Source | Destination", function () {
            var component1, component2, linkManager, compose;

            jCC.
                Given(function () {
                    component1 = Factory.component.core.source();
                }).
                And(function () {
                    component2 = Factory.component.core.destination();
                }).
                And(function () {
                    linkManager = Factory.link.manager();
                }).
                When(function () {
                    compose = Factory.component.compose(linkManager, [ component1, component2 ]);
                }).
                Then(function () {
                    QUnit.equal(jObj.ofType(compose, jObj.types.Named("Component")), true, " Composition with source | destination => a component");
                }).
                And(function () {
                    QUnit.equal(jObj.ofType(compose, jObj.types.Named("SourceComponent")), false, " Composition with source | destination not a source component");
                }).
                And(function () {
                    QUnit.equal(jObj.ofType(compose, jObj.types.Named("DestinationComponent")), false, " Composition with source | destination not a destination component");
                });
        });
    });