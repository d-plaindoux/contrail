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

require([ "Core/object/jObj", "Core/test/jCC", "Actor/jActor", "../common/StoredResponse", "Network/jNetwork", "Core/flow/jFlow", "Contrail/jContrail" ],
    function (jObj, jCC, jActor, storedResponse, jNetwork, jFlow, jContrail) {
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
                    router = jNetwork.component.domain("a");
                }).
                And(function () {
                    coordinator = jActor.coordinator();
                    coordinator.start();
                }).
                And(function () {
                    object = new A();
                }).
                And(function () {
                    coordinator.actor("A").bindToObject(object);
                }).
                And(function () {
                    terminal = jActor.component(coordinator);
                }).
                And(function () {
                    jContrail.component.compose([ router, terminal ]);
                }).
                And(function () {
                    packet = jNetwork.packet("b", "a", jActor.event.request("setA", [ "Hello, World!" ]).toActor("A"));
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
            var routerA, initialA, routerB, initialB, terminalB, packet, dataFlowRouter, coordinator, object;

            jCC.
                Given(function () {
                    routerA = jNetwork.component.domain("a");
                }).
                And(function () {
                    routerB = jNetwork.component.domain("b");
                }).
                And(function () {
                    dataFlowRouter = jContrail.flow.core();
                    dataFlowRouter.handleData = function (data) {
                        if (data.getDestinationId() === "a") {
                            initialA.getUpStreamDataFlow().handleData(data);
                        } else if (data.getDestinationId() === "b") {
                            initialB.getUpStreamDataFlow().handleData(data);
                        }
                    };
                }).
                And(function () {
                    initialA = jContrail.component.initial(dataFlowRouter);
                    initialB = jContrail.component.initial(dataFlowRouter);
                }).
                And(function () {
                    coordinator = jActor.coordinator();
                    coordinator.start();
                }).
                And(function () {
                    object = new A();
                }).
                And(function () {
                    coordinator.actor("A").bindToObject(object);
                }).
                And(function () {
                    terminalB = jActor.component(coordinator);
                }).
                And(function () {
                    jContrail.component.compose([ initialA, routerA ]);
                    jContrail.component.compose([ initialB, routerB, terminalB ]);
                }).
                And(function () {
                    packet = jNetwork.packet("a", "b", jActor.event.request("setA", [ "Hello, World!" ]).toActor("A"));
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
            var coordinatorA, initialA, coordinatorB, initialB, response1, response2, jSonifiers;

            jCC.
                Given(function () {
                    initialA = jContrail.component.initial(jContrail.flow.core(function (data) {
                        initialB.getUpStreamDataFlow().handleData(data);
                    }));
                    initialB = jContrail.component.initial(jContrail.flow.core(function (data) {
                        initialA.getUpStreamDataFlow().handleData(data);
                    }));
                }).
                And(function () {
                    coordinatorA = jActor.coordinator();
                    coordinatorA.start();
                    coordinatorB = jActor.coordinator();
                    coordinatorB.start();
                }).
                And(function () {
                    coordinatorA.actor("A").bindToRemote("A", "b");
                    coordinatorB.actor("A").bindToObject(new A());
                }).
                And(function () {
                    jSonifiers = [ jNetwork.packet, jActor.event.request ];
                }).
                And(function () {
                    jContrail.component.compose([
                        initialA,
                        jContrail.component.transducer(jContrail.codec.payload.encoder(), jContrail.codec.payload.decoder()),
                        jContrail.component.transducer(jContrail.codec.serialize.encoder(), jContrail.codec.serialize.decoder()),
                        jContrail.component.transducer(jContrail.codec.json.encoder(), jContrail.codec.json.decoder()),
                        jContrail.component.transducer(jContrail.codec.object.encoder(jSonifiers), jContrail.codec.object.decoder(jSonifiers)),
                        jNetwork.component.domain("a"),
                        jActor.component(coordinatorA) ]);
                }).
                And(function () {
                    jContrail.component.compose([
                        initialB,
                        jContrail.component.transducer(jContrail.codec.payload.encoder(), jContrail.codec.payload.decoder()),
                        jContrail.component.transducer(jContrail.codec.serialize.encoder(), jContrail.codec.serialize.decoder()),
                        jContrail.component.transducer(jContrail.codec.json.encoder(), jContrail.codec.json.decoder()),
                        jContrail.component.transducer(jContrail.codec.object.encoder(jSonifiers), jContrail.codec.object.decoder(jSonifiers)),
                        jNetwork.component.domain("b"),
                        jActor.component(coordinatorB) ]);
                }).
                And(function () {
                    response1 = storedResponse();
                    response2 = storedResponse();
                }).
                When(function () {
                    coordinatorA.send("A", jActor.event.request("getA", []), response1);
                    coordinatorA.send("A", jActor.event.request("setA", [ "Hello, World!" ]));
                    coordinatorA.send("A", jActor.event.request("getA", []), response2);
                }).
                ThenAfter(500, function () {
                    jCC.equal(response1.value(), "a");
                    jCC.equal(response2.value(), "Hello, World!");
                    coordinatorA.stop();
                    coordinatorB.stop();
                });
        });

        jCC.scenario("Checking remote actor message passing", function () {
            var coordinatorA, initialA, coordinatorB, initialB, response1, response2, jSonifiers;

            jCC.
                Given(function () {
                    initialA = jContrail.component.initial(jContrail.flow.core(function (data) {
                        initialB.getUpStreamDataFlow().handleData(data);
                    }));
                    initialB = jContrail.component.initial(jContrail.flow.core(function (data) {
                        initialA.getUpStreamDataFlow().handleData(data);
                    }));
                }).
                And(function () {
                    coordinatorA = jActor.coordinator();
                    coordinatorA.start();
                    coordinatorB = jActor.coordinator();
                    coordinatorB.start();
                }).
                And(function () {
                    coordinatorB.actor("A").bindToObject(new A());
                }).
                And(function () {
                    jSonifiers = [ jNetwork.packet, jActor.event.request ];
                }).
                And(function () {
                    jContrail.component.compose([
                        initialA,
                        jContrail.component.transducer(jContrail.codec.payload.encoder(), jContrail.codec.payload.decoder()),
                        jContrail.component.transducer(jContrail.codec.serialize.encoder(), jContrail.codec.serialize.decoder()),
                        jContrail.component.transducer(jContrail.codec.json.encoder(), jContrail.codec.json.decoder()),
                        jContrail.component.transducer(jContrail.codec.object.encoder(jSonifiers), jContrail.codec.object.decoder(jSonifiers)),
                        jNetwork.component.domain("a"),
                        jActor.component(coordinatorA) ]);
                }).
                And(function () {
                    jContrail.component.compose([
                        initialB,
                        jContrail.component.transducer(jContrail.codec.payload.encoder(), jContrail.codec.payload.decoder()),
                        jContrail.component.transducer(jContrail.codec.serialize.encoder(), jContrail.codec.serialize.decoder()),
                        jContrail.component.transducer(jContrail.codec.json.encoder(), jContrail.codec.json.decoder()),
                        jContrail.component.transducer(jContrail.codec.object.encoder(jSonifiers), jContrail.codec.object.decoder(jSonifiers)),
                        jNetwork.component.domain("b"),
                        jActor.component(coordinatorB) ]);
                }).
                And(function () {
                    response1 = storedResponse();
                    response2 = storedResponse();
                }).
                When(function () {
                    coordinatorA.domain("b").actor("A").send(jActor.event.request("getA", []), response1);
                    coordinatorA.domain("b").actor("A").send(jActor.event.request("setA", [ "Hello, World!" ]));
                    coordinatorA.domain("b").actor("A").send(jActor.event.request("getA", []), response2);
                }).
                ThenAfter(500, function () {
                    jCC.equal(response1.value(), "a");
                    jCC.equal(response2.value(), "Hello, World!");
                    coordinatorA.stop();
                    coordinatorB.stop();
                });
        });

        jCC.scenario("Checking remotely routed actor message passing using remote actor and native serialization without json", function () {
            var coordinatorA, initialA, coordinatorB, initialB, response1, response2, jSonifiers;

            jCC.
                Given(function () {
                    initialA = jContrail.component.initial(jContrail.flow.core(function (data) {
                        initialB.getUpStreamDataFlow().handleData(data);
                    }));
                    initialB = jContrail.component.initial(jContrail.flow.core(function (data) {
                        initialA.getUpStreamDataFlow().handleData(data);
                    }));
                }).
                And(function () {
                    coordinatorA = jActor.coordinator();
                    coordinatorA.start();
                    coordinatorB = jActor.coordinator();
                    coordinatorB.start();
                }).
                And(function () {
                    coordinatorA.actor("A").bindToRemote("A", "b");
                    coordinatorB.actor("A").bindToObject(new A());
                }).
                And(function () {
                    jSonifiers = [ jNetwork.packet, jActor.event.request ];
                }).
                And(function () {
                    jContrail.component.compose([
                        initialA,
                        jContrail.component.transducer(jContrail.codec.payload.encoder(), jContrail.codec.payload.decoder()),
                        jContrail.component.transducer(jContrail.codec.serialize.encoder(), jContrail.codec.serialize.decoder()),
                        jContrail.component.transducer(jContrail.codec.object.encoder(jSonifiers), jContrail.codec.object.decoder(jSonifiers)),
                        jNetwork.component.domain("a"),
                        jActor.component(coordinatorA) ]);
                }).
                And(function () {
                    jContrail.component.compose([
                        initialB,
                        jContrail.component.transducer(jContrail.codec.payload.encoder(), jContrail.codec.payload.decoder()),
                        jContrail.component.transducer(jContrail.codec.serialize.encoder(), jContrail.codec.serialize.decoder()),
                        jContrail.component.transducer(jContrail.codec.object.encoder(jSonifiers), jContrail.codec.object.decoder(jSonifiers)),
                        jNetwork.component.domain("b"),
                        jActor.component(coordinatorB) ]);
                }).
                And(function () {
                    response1 = storedResponse();
                    response2 = storedResponse();
                }).
                When(function () {
                    coordinatorA.send("A", jActor.event.request("getA", []), response1);
                    coordinatorA.send("A", jActor.event.request("setA", [ "Hello, World!" ]));
                    coordinatorA.send("A", jActor.event.request("getA", []), response2);
                }).
                ThenAfter(500, function () {
                    jCC.equal(response1.value(), "a");
                    jCC.equal(response2.value(), "Hello, World!");
                    coordinatorA.stop();
                    coordinatorB.stop();
                });
        });

        jCC.scenario("Checking sequential remotely routed actor message passing using remote actor", function () {
            var coordinatorA, initialA, coordinatorB, initialB, dataFlowRouter, response1, response2;

            jCC.
                Given(function () {
                    dataFlowRouter = jContrail.flow.core();
                    dataFlowRouter.handleData = function (data) {
                        if (data.getDestinationId() === "a") {
                            initialA.getUpStreamDataFlow().handleData(data);
                        } else if (data.getDestinationId() === "b") {
                            initialB.getUpStreamDataFlow().handleData(data);
                        }
                    };
                }).
                And(function () {
                    initialA = jContrail.component.initial(dataFlowRouter);
                    initialB = jContrail.component.initial(dataFlowRouter);
                }).
                And(function () {
                    coordinatorA = jActor.coordinator();
                    coordinatorA.start();
                    coordinatorB = jActor.coordinator();
                    coordinatorB.start();
                }).
                And(function () {
                    coordinatorA.actor("A.dist").bindToRemote("A", "b");
                    coordinatorB.actor("A").bindToObject(new A());
                }).
                And(function () {
                    jContrail.component.compose([ initialA, jNetwork.component.domain("a"), jActor.component(coordinatorA) ]);
                    jContrail.component.compose([ initialB, jNetwork.component.domain("b"), jActor.component(coordinatorB) ]);
                }).
                And(function () {
                    response1 = storedResponse();
                    response2 = storedResponse();
                }).
                When(function () {
                    coordinatorA.send("A.dist", jActor.event.request("getA", []), response1);
                    coordinatorA.send("A.dist", jActor.event.request("setA", [ "Hello, World!" ]));
                    coordinatorA.send("A.dist", jActor.event.request("getA", []), response2);
                }).
                ThenAfter(500, function () {
                    jCC.equal(response1.value(), "a");
                    jCC.equal(response2.value(), "Hello, World!");
                    coordinatorA.stop();
                    coordinatorB.stop();
                });
        });
    });

