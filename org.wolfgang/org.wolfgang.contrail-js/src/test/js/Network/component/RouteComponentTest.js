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

require([ "Core/test/jCC", "Core/object/jObj", "Network/jNetwork", "Contrail/jContrail", "Core/flow/jFlow" ],
    function (jCC, jObj, jNetwork, jContrail, jFlow) {
        "use strict";

        jCC.scenario("Checking route component with packet in destination", function () {
            var table, router, terminal, buffered, packet;

            jCC.
                Given(function () {
                    table = jNetwork.table.create();
                }).
                And(function () {
                    table.addRoute("b", "ws://localhost/b");
                }).
                And(function () {
                    router = jNetwork.component(table, "a");
                }).
                And(function () {
                    buffered = jFlow.buffered();
                }).
                And(function () {
                    terminal = jContrail.component.terminal(buffered);
                }).
                And(function () {
                    jContrail.component.compose([ router, terminal ]);
                }).
                And(function () {
                    packet = jNetwork.packet("b", "a", "Hello, World!", "ws://localhost/a");
                }).
                When(function () {
                    terminal.getDownStreamDataFlow().handleData(packet);
                }).
                Then(function () {
                    jCC.equal(buffered.getBuffered().length, 1, "A data must be buffered");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(buffered.getBuffered()[0], jObj.types.Named("Packet")), true, "A Packet must be buffered");
                }).
                And(function () {
                    jCC.equal(buffered.getBuffered()[0].getData(), "Hello, World!", "Data is 'Hello, World!'");
                }).
                And(function () {
                    jCC.equal(buffered.getBuffered()[0].getEndPoint(), "ws://localhost/a", "End point is 'ws://localhost/a'");
                });
        });


        jCC.scenario("Checking route component already routed in destination", function () {
            var table, router, terminal, buffered, packet;

            jCC.
                Given(function () {
                    table = jNetwork.table.create();
                }).
                And(function () {
                    table.addRoute("b", "ws://localhost/b");
                }).
                And(function () {
                    router = jNetwork.component(table, "a");
                }).
                And(function () {
                    buffered = jFlow.buffered();
                }).
                And(function () {
                    terminal = jContrail.component.terminal(buffered);
                }).
                And(function () {
                    jContrail.component.compose([ router, terminal ]);
                }).
                And(function () {
                    packet = jNetwork.packet("b", "a", "Hello, World!", "ws://localhost/a");
                }).
                When(function () {
                    router.getUpStreamDataFlow().handleData(packet);
                }).
                Then(function () {
                    jCC.equal(buffered.getBuffered().length, 1, "A data must be buffered");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(buffered.getBuffered()[0], jObj.types.Named("Packet")), true, "A Packet must be buffered");
                }).
                And(function () {
                    jCC.equal(buffered.getBuffered()[0].getData(), "Hello, World!", "Data is 'Hello, World!'");
                }).
                And(function () {
                    jCC.equal(buffered.getBuffered()[0].getEndPoint(), "ws://localhost/a", "End point is 'ws://localhost/a'");
                });
        });

        jCC.scenario("Checking route component with packet in transit", function () {
            var table, router, initial, buffered, packet;

            jCC.
                Given(function () {
                    table = jNetwork.table.create();
                }).
                And(function () {
                    table.addRoute("b", "ws://localhost/b");
                }).
                And(function () {
                    router = jNetwork.component(table, "a");
                }).
                And(function () {
                    buffered = jFlow.buffered();
                }).
                And(function () {
                    initial = jContrail.component.initial(buffered);
                }).
                And(function () {
                    jContrail.component.compose([ initial, router ]);
                }).
                And(function () {
                    packet = jNetwork.packet("b", "b", "Hello, World!", "ws://localhost/a");
                }).
                When(function () {
                    initial.getUpStreamDataFlow().handleData(packet);
                }).
                Then(function () {
                    jCC.equal(buffered.getBuffered().length, 1, "A data must be buffered");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(buffered.getBuffered()[0], jObj.types.Named("Packet")), true, "A Packet must be buffered");
                }).
                And(function () {
                    jCC.equal(buffered.getBuffered()[0].getData(), "Hello, World!", "Data is 'Hello, World!'");
                }).
                And(function () {
                    jCC.equal(buffered.getBuffered()[0].getEndPoint(), "ws://localhost/b", "End point is 'ws://localhost/b'");
                });
        });

        jCC.scenario("Checking route component with packet sent to a component", function () {
            var table, router, initial, buffered, packet;

            jCC.
                Given(function () {
                    table = jNetwork.table.create();
                }).
                And(function () {
                    table.addRoute("b", "ws://localhost/b");
                }).
                And(function () {
                    router = jNetwork.component(table, "a");
                }).
                And(function () {
                    buffered = jFlow.buffered();
                }).
                And(function () {
                    initial = jContrail.component.initial(buffered);
                }).
                And(function () {
                    jContrail.component.compose([ initial, router ]);
                }).
                And(function () {
                    packet = jNetwork.packet("b", "b", "Hello, World!");
                }).
                When(function () {
                    router.getDownStreamDataFlow().handleData(packet);
                }).
                Then(function () {
                    jCC.equal(buffered.getBuffered().length, 1, "A data must be buffered");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(buffered.getBuffered()[0], jObj.types.Named("Packet")), true, "A Packet must be buffered");
                }).
                And(function () {
                    jCC.equal(buffered.getBuffered()[0].getData(), "Hello, World!", "Data is 'Hello, World!'");
                }).
                And(function () {
                    jCC.equal(buffered.getBuffered()[0].getEndPoint(), "ws://localhost/b", "End point is 'ws://localhost/b'");
                });
        });

        jCC.scenario("Checking route component with packet sent to an intermediate component", function () {
            var table, router, initial, buffered, packet;

            jCC.
                Given(function () {
                    table = jNetwork.table.create();
                }).
                And(function () {
                    table.addRoute("b", "ws://localhost/b");
                }).
                And(function () {
                    router = jNetwork.component(table, "a");
                }).
                And(function () {
                    buffered = jFlow.buffered();
                }).
                And(function () {
                    initial = jContrail.component.initial(buffered);
                }).
                And(function () {
                    jContrail.component.compose([ initial, router ]);
                }).
                And(function () {
                    packet = jNetwork.packet("b", "b", "Hello, World!");
                }).
                When(function () {
                    router.getUpStreamDataFlow().handleData(packet);
                }).
                Then(function () {
                    jCC.equal(buffered.getBuffered().length, 1, "A data must be buffered");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(buffered.getBuffered()[0], jObj.types.Named("Packet")), true, "A Packet must be buffered");
                }).
                And(function () {
                    jCC.equal(buffered.getBuffered()[0].getData(), "Hello, World!", "Data is 'Hello, World!'");
                }).
                And(function () {
                    jCC.equal(buffered.getBuffered()[0].getEndPoint(), "ws://localhost/b", "End point is 'ws://localhost/b'");
                });
        });

        jCC.scenario("Checking route with indirect call and two collaborative component ecosystems", function () {
            var table, routerA, initialA, routerB, initialB, terminalB, packet, dataFlowRouter;

            jCC.
                Given(function () {
                    table = jNetwork.table.create();
                }).
                And(function () {
                    table.addRoute("a", "ws://localhost/a");
                }).
                And(function () {
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
                    terminalB = jContrail.component.terminal(jFlow.buffered());
                }).
                And(function () {
                    jContrail.component.compose([ initialA, routerA ]);
                }).
                And(function () {
                    jContrail.component.compose([ initialB, routerB, terminalB ]);
                }).
                And(function () {
                    packet = jNetwork.packet("a", "b", "Hello, World!");
                }).
                When(function () {
                    routerA.getDownStreamDataFlow().handleData(packet);
                }).
                Then(function () {
                    jCC.equal(terminalB.getUpStreamDataFlow().getBuffered().length, 1, "A data must be buffered");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(terminalB.getUpStreamDataFlow().getBuffered()[0], jObj.types.Named("Packet")), true, "A Packet must be buffered");
                }).
                And(function () {
                    jCC.equal(terminalB.getUpStreamDataFlow().getBuffered()[0].getData(), "Hello, World!", "Data is 'Hello, World!'");
                }).
                And(function () {
                    jCC.equal(terminalB.getUpStreamDataFlow().getBuffered()[0].getEndPoint(), "ws://localhost/b", "End point is 'ws://localhost/b'");
                });
        });
    });