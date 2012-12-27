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

require([ "Contrail", "qunit", "Core/object/jObj", "test/jCC"],
    function (Factory, QUnit, jObj, jCC) {
        "use strict";

        jCC.scenario("Check Component generation", function () {
            var c1, c2;

            jCC.
                Given(function () {
                    c1 = Factory.component.multi.sources();
                }).
                And(function () {
                    c2 = Factory.component.multi.sources();
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
                    c1 = Factory.component.multi.sources();
                }).
                WhenNothing.
                Then(function () {
                    QUnit.equal(jObj.ofType(c1, jObj.types.Named("Component")), true, "Checking c1 instance of Component");
                });
        });

        jCC.scenario("Check Component type to be a MultiSourceComponent", function () {
            var c1;

            jCC.
                Given(function () {
                    c1 = Factory.component.multi.sources();
                }).
                WhenNothing.
                Then(function () {
                    QUnit.equal(jObj.ofType(c1, jObj.types.Named("MultiSourceComponent")), true, "Checking c1 instance of MultiSourceComponent");
                });
        });

        jCC.scenario("Test component with a single source", function () {
            var c1, i1;

            jCC.
                Given(function () {
                    c1 = Factory.component.multi.sources();
                }).
                And(function () {
                    i1 = Factory.component.initial(Factory.flow.cumulated());
                }).
                When(function () {
                    Factory.link.connect(i1, c1);
                }).
                And(function () {
                    c1.getDownStreamDataFlow().handleData("Hello, World!");
                }).
                Then(function () {
                    QUnit.equal(i1.getDownStreamDataFlow().getAccumulation().length, 1, "Checking accumulated number of data");
                }).
                And(function () {
                    QUnit.equal(i1.getDownStreamDataFlow().getAccumulation()[0], "Hello, World!", "Checking accumulated data");
                });
        });

        jCC.scenario("Test component with two sources", function () {
            var c1, i1, i2;

            jCC.
                Given(function () {
                    c1 = Factory.component.multi.sources();
                }).
                And(function () {
                    i1 = Factory.component.initial(Factory.flow.cumulated());
                }).
                And(function () {
                    i2 = Factory.component.initial(Factory.flow.cumulated());
                }).
                When(function () {
                    Factory.link.connect(i1, c1);
                }).
                And(function () {
                    Factory.link.connect(i2, c1);
                }).
                And(function () {
                    c1.getDownStreamDataFlow().handleData("Hello, World!");
                }).
                Then(function () {
                    QUnit.equal(i1.getDownStreamDataFlow().getAccumulation().length, 1, "Checking accumulated number of data");
                }).
                And(function () {
                    QUnit.equal(i1.getDownStreamDataFlow().getAccumulation()[0], "Hello, World!", "Checking accumulated data");
                }).
                And(function () {
                    QUnit.equal(i2.getDownStreamDataFlow().getAccumulation().length, 1, "Checking accumulated number of data");
                }).
                And(function () {
                    QUnit.equal(i2.getDownStreamDataFlow().getAccumulation()[0], "Hello, World!", "Checking accumulated data");
                });
        });

        jCC.scenario("Test component with two sources and filtered data flows", function () {
            var c1, i1, i2, d1, d2;

            jCC.
                Given(function () {
                    c1 = Factory.component.multi.sources();
                }).
                And(function () {
                    d1 = Factory.flow.cumulated();
                }).
                And(function () {
                    d2 = Factory.flow.cumulated();
                }).
                And(function () {
                    i1 = Factory.component.initial(Factory.flow.filtered(d1, function (data) {
                        return data;
                    }));
                }).
                And(function () {
                    i2 = Factory.component.initial(Factory.flow.filtered(d2, function (data) {
                        return undefined;
                    }));
                }).
                When(function () {
                    Factory.link.connect(i1, c1);
                }).
                And(function () {
                    Factory.link.connect(i2, c1);
                }).
                And(function () {
                    c1.getDownStreamDataFlow().handleData("Hello, World!");
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

        jCC.scenario("Test component with two sources and routing filtered data flows", function () {
            var c1, i1, i2, d1, d2;

            jCC.
                Given(function () {
                    c1 = Factory.component.multi.sources();
                }).
                And(function () {
                    d1 = Factory.flow.cumulated();
                }).
                And(function () {
                    d2 = Factory.flow.cumulated();
                }).
                And(function () {
                    i1 = Factory.component.initial(Factory.flow.filtered(d1, function (data) {
                        return data && data.to === "T1" && data.what;
                    }));
                }).
                And(function () {
                    i2 = Factory.component.initial(Factory.flow.filtered(d2, function (data) {
                        return data && data.to === "T2" && data.what;
                    }));
                }).
                When(function () {
                    Factory.link.connect(i1, c1);
                }).
                And(function () {
                    Factory.link.connect(i2, c1);
                }).
                And(function () {
                    c1.getDownStreamDataFlow().handleData({to:"T1", what:"Hello"});
                }).
                And(function () {
                    c1.getDownStreamDataFlow().handleData({to:"T2", what:"World!"});
                }).
                Then(function () {
                    QUnit.equal(d1.getAccumulation().length, 1, "Checking accumulated number of data");
                }).
                And(function () {
                    QUnit.equal(d1.getAccumulation()[0], "Hello", "Checking accumulated data");
                }).
                And(function () {
                    QUnit.equal(d2.getAccumulation().length, 1, "Checking accumulated number of data");
                }).
                And(function () {
                    QUnit.equal(d2.getAccumulation()[0], "World!", "Checking accumulated data");
                });
        });
    });