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

require([ "Contrail/jContrail", "Core/object/jObj", "test/jCC"],
    function (Factory, jObj, jCC) {
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
                When(jCC.Nothing).
                Then(function () {
                    jCC.notEqual(c1.getComponentId(), c2.getComponentId(), "Two fresh components must be different");
                });
        });

        jCC.scenario("Check Component type to be a Component", function () {
            var c1;

            jCC.
                Given(function () {
                    c1 = Factory.component.multi.destinations();
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(jObj.ofType(c1, jObj.types.Named("Component")), true, "Checking c1 instance of Component");
                });
        });

        jCC.scenario("Check Component type to be a MultiDestinationComponent", function () {
            var c1;

            jCC.
                Given(function () {
                    c1 = Factory.component.multi.destinations();
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(jObj.ofType(c1, jObj.types.Named("MultiDestinationComponent")), true, "Checking c1 instance of MultiDestinationComponent");
                });
        });

        jCC.scenario("Test component with a single destination", function () {
            var c1, t1;

            jCC.
                Given(function () {
                    c1 = Factory.component.multi.destinations();
                }).
                And(function () {
                    t1 = Factory.component.terminal(Factory.flow.buffered());
                }).
                When(function () {
                    Factory.link.connect(c1, t1);
                }).
                And(function () {
                    c1.getUpStreamDataFlow().handleData("Hello, World!");
                }).
                Then(function () {
                    jCC.equal(t1.getUpStreamDataFlow().getCumulated().length, 1, "Checking accumulated number of data");
                }).
                And(function () {
                    jCC.equal(t1.getUpStreamDataFlow().getCumulated()[0], "Hello, World!", "Checking accumulated data");
                });
        });

        jCC.scenario("Test component with two destinations", function () {
            var c1, t1, t2;

            jCC.
                Given(function () {
                    c1 = Factory.component.multi.destinations();
                }).
                And(function () {
                    t1 = Factory.component.terminal(Factory.flow.buffered());
                }).
                And(function () {
                    t2 = Factory.component.terminal(Factory.flow.buffered());
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
                    jCC.equal(t1.getUpStreamDataFlow().getCumulated().length, 1, "Checking accumulated number of data");
                }).
                And(function () {
                    jCC.equal(t1.getUpStreamDataFlow().getCumulated()[0], "Hello, World!", "Checking accumulated data");
                }).
                And(function () {
                    jCC.equal(t2.getUpStreamDataFlow().getCumulated().length, 1, "Checking accumulated number of data");
                }).
                And(function () {
                    jCC.equal(t2.getUpStreamDataFlow().getCumulated()[0], "Hello, World!", "Checking accumulated data");
                });
        });

        jCC.scenario("Test component with two destinations and filtered data flows", function () {
            var c1, t1, t2, d1, d2;

            jCC.
                Given(function () {
                    c1 = Factory.component.multi.destinations();
                }).
                And(function () {
                    d1 = Factory.flow.buffered();
                }).
                And(function () {
                    d2 = Factory.flow.buffered();
                }).
                And(function () {
                    t1 = Factory.component.terminal(Factory.flow.filtered(d1, function (data) {
                        return data;
                    }));
                }).
                And(function () {
                    t2 = Factory.component.terminal(Factory.flow.filtered(d2, function (data) {
                        return undefined;
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
                    jCC.equal(d1.getCumulated().length, 1, "Checking accumulated number of data");
                }).
                And(function () {
                    jCC.equal(d1.getCumulated()[0], "Hello, World!", "Checking accumulated data");
                }).
                And(function () {
                    jCC.equal(d2.getCumulated().length, 0, "Checking accumulated number of data");
                });
        });

        jCC.scenario("Test component with two destinations and routing filtered data flows", function () {
            var c1, t1, t2, d1, d2;

            jCC.
                Given(function () {
                    c1 = Factory.component.multi.destinations();
                }).
                And(function () {
                    d1 = Factory.flow.buffered();
                }).
                And(function () {
                    d2 = Factory.flow.buffered();
                }).
                And(function () {
                    t1 = Factory.component.terminal(Factory.flow.filtered(d1, function (data) {
                        return data && data.to === "T1" && data.what;
                    }));
                }).
                And(function () {
                    t2 = Factory.component.terminal(Factory.flow.filtered(d2, function (data) {
                        return data && data.to === "T2" && data.what;
                    }));
                }).
                When(function () {
                    Factory.link.connect(c1, t1);
                }).
                And(function () {
                    Factory.link.connect(c1, t2);
                }).
                And(function () {
                    c1.getUpStreamDataFlow().handleData({to:"T1", what:"Hello"});
                }).
                And(function () {
                    c1.getUpStreamDataFlow().handleData({to:"T2", what:"World!"});
                }).
                Then(function () {
                    jCC.equal(d1.getCumulated().length, 1, "Checking accumulated number of data");
                }).
                And(function () {
                    jCC.equal(d1.getCumulated()[0], "Hello", "Checking accumulated data");
                }).
                And(function () {
                    jCC.equal(d2.getCumulated().length, 1, "Checking accumulated number of data");
                }).
                And(function () {
                    jCC.equal(d2.getCumulated()[0], "World!", "Checking accumulated data");
                });
        });
    });