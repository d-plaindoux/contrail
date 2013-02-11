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

/*global require, setTimeout */

require([
    "Core/object/jObj", "Core/test/jCC", "Concurrent/jConcurrent", "../common/StoredResponse", "Network/jNetwork", "Core/flow/jFlow", "Contrail/jContrail"
],
    function (jObj, jCC, jConcurrent, storedResponse, jNetwork, jFlow, jContrail) {
        "use strict";

        // ---------------------------------------------------------

        function A() {
            jObj.bless(this);
            this.a = "a";
        }

        A.prototype.setA = function (na) {
            this.a = na;
        };

        A.prototype.getA = function () {
            return this.a;
        };

        A.prototype.error = function () {
            throw "A.m()";
        };

        // ---------------------------------------------------------

        jCC.scenario("Check locally routed actor message passing", function () {
            var router, terminal, coordinator, object, packet;

            jCC.
                Given(function () {
                    router = jNetwork.component(jNetwork.table.create(), "a");
                    router.getTable().addRoute("b", "ws://localhost/b");
                }).
                And(function () {
                    coordinator = jConcurrent.actor.coordinator();
                    coordinator.start();
                }).
                And(function () {
                    object = new A();
                }).
                And(function () {
                    coordinator.actor("A").bindToObject(object);
                }).
                And(function () {
                    terminal = jConcurrent.component(coordinator);
                }).
                And(function () {
                    jContrail.component.compose([ router, terminal ]);
                }).
                And(function () {
                    packet = jNetwork.packet("b", "a", jConcurrent.event.request("setA", [ "Hello, World!" ]).toActor("A"), "ws://localhost/a");
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(object.a, "a");
                }).
                When(function () {
                    router.getUpStreamDataFlow().handleData(packet);
                }).
                ThenAfter(500, function () {
                    jCC.equal(object.a, "Hello, World!");
                    coordinator.stop();
                });
        });

        jCC.scenario("Checking remotely routed actor message passing", function () {
            var table, routerA, initialA, routerB, initialB, terminalB, packet, dataFlowRouter, coordinator, object;

            jCC.
                Given(function () {
                    table = jNetwork.table.create();
                    table.addRoute("a", "ws://localhost/a");
                    table.addRoute("b", "ws://localhost/b");
                }).
                And(function () {
                    routerA = jNetwork.component(table, "a");
                }).
                And(function () {
                    routerB = jNetwork.component(table, "b");
                }).
                And(function () {
                    dataFlowRouter = jContrail.flow.core();
                    dataFlowRouter.handleData = function (data) {
                        if (data.getEndPoint() === table.getRoute("a")) {
                            initialA.getUpStreamDataFlow().handleData(data);
                        } else if (data.getEndPoint() === table.getRoute("b")) {
                            initialB.getUpStreamDataFlow().handleData(data);
                        }
                    };
                }).
                And(function () {
                    initialA = jContrail.component.initial(dataFlowRouter);
                }).
                And(function () {
                    initialB = jContrail.component.initial(dataFlowRouter);
                }).
                And(function () {
                    coordinator = jConcurrent.actor.coordinator();
                    coordinator.start();
                }).
                And(function () {
                    object = new A();
                }).
                And(function () {
                    coordinator.actor("A").bindToObject(object);
                }).
                And(function () {
                    terminalB = jConcurrent.component(coordinator);
                }).
                And(function () {
                    jContrail.component.compose([ initialA, routerA ]);
                }).
                And(function () {
                    jContrail.component.compose([ initialB, routerB, terminalB ]);
                }).
                And(function () {
                    packet = jNetwork.packet("a", "b", jConcurrent.event.request("setA", [ "Hello, World!" ]).toActor("A"));
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(object.a, "a");
                }).
                When(function () {
                    routerA.getDownStreamDataFlow().handleData(packet);
                }).
                ThenAfter(500, function () {
                    jCC.equal(object.a, "Hello, World!");
                    coordinator.stop();
                });
        });

        jCC.scenario("Checking remotely routed actor message passing using remote actor", function () {
            var table, coordinatorA, initialA, coordinatorB, initialB, dataFlowRouter, response;

            jCC.
                Given(function () {
                    table = jNetwork.table.create();
                    table.addRoute("a", "ws://localhost/a");
                    table.addRoute("b", "ws://localhost/b");
                }).
                And(function () {
                    dataFlowRouter = jContrail.flow.core();
                    dataFlowRouter.handleData = function (data) {
                        if (data.getEndPoint() === table.getRoute("a")) {
                            initialA.getUpStreamDataFlow().handleData(data);
                        } else if (data.getEndPoint() === table.getRoute("b")) {
                            initialB.getUpStreamDataFlow().handleData(data);
                        }
                    };
                }).
                And(function () {
                    initialA = jContrail.component.initial(dataFlowRouter);
                }).
                And(function () {
                    initialB = jContrail.component.initial(dataFlowRouter);
                }).
                And(function () {
                    coordinatorA = jConcurrent.actor.coordinator();
                    coordinatorA.start();
                }).
                And(function () {
                    coordinatorA.actor("A").bindToRemote("b");
                }).
                And(function () {
                    coordinatorB = jConcurrent.actor.coordinator();
                    coordinatorB.start();
                }).
                And(function () {
                    coordinatorB.actor("A").bindToObject(new A());
                }).
                And(function () {
                    jContrail.component.compose([ initialA, jNetwork.component(table, "a"), jConcurrent.component(coordinatorA) ]);
                }).
                And(function () {
                    jContrail.component.compose([ initialB, jNetwork.component(table, "b"), jConcurrent.component(coordinatorB) ]);
                }).
                And(function () {
                    response = storedResponse();
                }).
                When(function () {
                    coordinatorA.send("A", jConcurrent.event.request("getA", []), response);
                }).
                ThenAfter(500, function () {
                    jCC.equal(response.value(), "a");
                    coordinatorA.stop();
                    coordinatorB.stop();
                });
        });

        jCC.scenario("Checking sequential remotely routed actor message passing using remote actor", function () {
            var table, coordinatorA, initialA, coordinatorB, initialB, dataFlowRouter, response1, response2;

            jCC.
                Given(function () {
                    table = jNetwork.table.create();
                    table.addRoute("a", "ws://localhost/a");
                    table.addRoute("b", "ws://localhost/b");
                }).
                And(function () {
                    dataFlowRouter = jContrail.flow.core();
                    dataFlowRouter.handleData = function (data) {
                        if (data.getEndPoint() === table.getRoute("a")) {
                            initialA.getUpStreamDataFlow().handleData(data);
                        } else if (data.getEndPoint() === table.getRoute("b")) {
                            initialB.getUpStreamDataFlow().handleData(data);
                        }
                    };
                }).
                And(function () {
                    initialA = jContrail.component.initial(dataFlowRouter);
                }).
                And(function () {
                    initialB = jContrail.component.initial(dataFlowRouter);
                }).
                And(function () {
                    coordinatorA = jConcurrent.actor.coordinator();
                    coordinatorA.start();
                }).
                And(function () {
                    coordinatorA.actor("A").bindToRemote("b");
                }).
                And(function () {
                    coordinatorB = jConcurrent.actor.coordinator();
                    coordinatorB.start();
                }).
                And(function () {
                    coordinatorB.actor("A").bindToObject(new A());
                }).
                And(function () {
                    jContrail.component.compose([ initialA, jNetwork.component(table, "a"), jConcurrent.component(coordinatorA) ]);
                }).
                And(function () {
                    jContrail.component.compose([ initialB, jNetwork.component(table, "b"), jConcurrent.component(coordinatorB) ]);
                }).
                And(function () {
                    response1 = storedResponse();
                }).
                And(function () {
                    response2 = storedResponse();
                }).
                When(function () {
                    coordinatorA.send("A", jConcurrent.event.request("getA", []), response1);
                }).
                And(function () {
                    coordinatorA.send("A", jConcurrent.event.request("setA", [ "Hello, World!" ]));
                }).
                And(function () {
                    coordinatorA.send("A", jConcurrent.event.request("getA", []), response2);
                }).
                ThenAfter(500, function () {
                    jCC.equal(response1.value(), "a");
                    jCC.equal(response2.value(), "Hello, World!");
                    coordinatorA.stop();
                    coordinatorB.stop();
                });
        });
    });

