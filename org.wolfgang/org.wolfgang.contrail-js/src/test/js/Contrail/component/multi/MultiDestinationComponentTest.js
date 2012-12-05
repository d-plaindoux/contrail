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

require([ "Contrail/Factory", "qunit", "Core/jObj", "test/jCC"],
    function (Factory, QUnit, jObj, jCC) {
        "use strict";

        jCC.scenario("Check Component generation", function () {
            var c1, c2;

            jCC.
                Given(function () {
                    c1 = Factory.component.multi.destinations();
                }).
                And(function () {
                    c2 = Factory.component.multi.destinations();
                }).
                WhenNothing.
                Then(function () {
                    QUnit.notEqual(c1.getComponentId(), c2.getComponentId(), "Two fresh components must be different");
                });
        });

        jCC.scenario("Check Component type to be a Component", function () {
            var c1;

            jCC.
                Given(function () {
                    c1 = Factory.component.multi.destinations();
                }).
                WhenNothing.
                Then(function () {
                    QUnit.equal(jObj.ofType(c1, jObj.types.Named("Component")), true, "Checking c1 instance of Component");
                });
        });

        jCC.scenario("Check Component type to be a MultiDestinationComponent", function () {
            var c1;

            jCC.
                Given(function () {
                    c1 = Factory.component.multi.destinations();
                }).
                WhenNothing.
                Then(function () {
                    QUnit.equal(jObj.ofType(c1, jObj.types.Named("MultiDestinationComponent")), true, "Checking c1 instance of MultiDestinationComponent");
                });
        });

        jCC.scenario("Test component with a single destination", function () {
            var c1, t1;

            jCC.
                Given(function () {
                    c1 = Factory.component.multi.destinations();
                }).
                And(function () {
                    t1 = Factory.component.terminal(Factory.flow.accumulated());
                }).
                When(function () {
                    Factory.link.connect(c1, t1);
                }).
                And(function () {
                    c1.getUpStreamDataFlow().handleData("Hello, World!");
                }).
                Then(function () {
                    QUnit.equal(t1.getUpStreamDataFlow().getAccumulation().length, 1, "Checking accumulated number of data");
                }).
                And(function () {
                    QUnit.equal(t1.getUpStreamDataFlow().getAccumulation()[0], "Hello, World!", "Checking accumulated data");
                });
        });

        jCC.scenario("Test component with two destinations", function () {
            var c1, t1, t2;

            jCC.
                Given(function () {
                    c1 = Factory.component.multi.destinations();
                }).
                And(function () {
                    t1 = Factory.component.terminal(Factory.flow.accumulated());
                }).
                And(function () {
                    t2 = Factory.component.terminal(Factory.flow.accumulated());
                }).
                When(function () {
                    Factory.link.connect(c1, t1);
                }).
                And(function () {
                    Factory.link.connect(c1, t2);
                }).
                And(function () {
                    c1.getUpStreamDataFlow().handleData("Hello, World!");
                }).
                Then(function () {
                    QUnit.equal(t1.getUpStreamDataFlow().getAccumulation().length, 1, "Checking accumulated number of data");
                }).
                And(function () {
                    QUnit.equal(t1.getUpStreamDataFlow().getAccumulation()[0], "Hello, World!", "Checking accumulated data");
                }).
                And(function () {
                    QUnit.equal(t2.getUpStreamDataFlow().getAccumulation().length, 1, "Checking accumulated number of data");
                }).
                And(function () {
                    QUnit.equal(t2.getUpStreamDataFlow().getAccumulation()[0], "Hello, World!", "Checking accumulated data");
                });
        });

        jCC.scenario("Test component with two destinations and guarded data flows", function () {
            var c1, t1, t2, d1, d2;

            jCC.
                Given(function () {
                    c1 = Factory.component.multi.destinations();
                }).
                And(function () {
                    d1 = Factory.flow.accumulated();
                }).
                And(function () {
                    d2 = Factory.flow.accumulated();
                }).
                And(function () {
                    t1 = Factory.component.terminal(Factory.flow.guarded(d1, function (data) {
                        return true;
                    }));
                }).
                And(function () {
                    t2 = Factory.component.terminal(Factory.flow.guarded(d2, function (data) {
                        return false;
                    }));
                }).
                When(function () {
                    Factory.link.connect(c1, t1);
                }).
                And(function () {
                    Factory.link.connect(c1, t2);
                }).
                And(function () {
                    c1.getUpStreamDataFlow().handleData("Hello, World!");
                }).
                Then(function () {
                    QUnit.equal(d1.getAccumulation().length, 1, "Checking accumulated number of data");
                }).
                And(function () {
                    QUnit.equal(d1.getAccumulation()[0], "Hello, World!", "Checking accumulated data");
                }).
                And(function () {
                    QUnit.equal(d2.getAccumulation().length, 0, "Checking accumulated number of data");
                });
        });
    });