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

require([ "Contrail/jContrail", "Core/object/jObj", "Core/test/jCC" ],
    function (Factory, jObj, jCC) {
        "use strict";

        jCC.scenario("Check Component generation", function () {
            var component1, component2;

            jCC.
                Given(function () {
                    component1 = Factory.component.terminal(Factory.flow.core());
                }).
                And(function () {
                    component2 = Factory.component.terminal(Factory.flow.core());
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.notEqual(component1.getComponentId(), component2.getComponentId(), "Two fresh components must be different");
                });
        });

        jCC.scenario("Check Component up stream mechanism", function () {
            var component, dataFlow;

            jCC.
                Given(function () {
                    dataFlow = Factory.flow.core(function (data) {
                        this.content = jObj.value(this.content, "") + data;
                    });
                }).
                And(function () {
                    component = Factory.component.terminal(dataFlow);
                }).
                When(function () {
                    component.getUpStreamDataFlow().handleData("Hello, World!");
                }).
                Then(function () {
                    jCC.equal(dataFlow.content, "Hello, World!", "Checking data stream content which must be 'Hello, World!'");
                });
        });
    });