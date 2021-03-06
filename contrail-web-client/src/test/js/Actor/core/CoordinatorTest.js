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

/*global require, setTimeout*/

require([ "Core/object/jObj", "Core/test/jCC", "Core/client/jLoader", "Actor/jActor", "../common/StoredResponse" ],
    function (jObj, jCC, jLoader, jActor, storedResponse) {
        "use strict";

        // ---------------------------------------------------------

        function A() {
            jObj.bless(this);
            this.a = "a";
        }

        A.prototype.n = function () {
            return "A.n()";
        };

        A.prototype.m = function () {
            throw "A.m()";
        };

        // ---------------------------------------------------------

        jCC.scenario("Check actor indirect successful invocation simulated", function () {
            var coordinator, actor, response;

            jCC.
                Given(function () {
                    coordinator = jActor.coordinator();
                }).
                And(function () {
                    actor = coordinator.actor("A").bindToObject(new A());
                }).
                And(function () {
                    response = storedResponse();
                }).
                When(function () {
                    actor.ask(jActor.event.request("n", []), response);
                }).
                Then(function () {
                    jCC.equal(actor.pendingJobs.length, 1, "A new job has been created");
                }).
                When(function () {
                    coordinator.actorRunner();
                }).
                Then(function () {
                    jCC.equal(coordinator.pendingJobs.length, 1, "Job has been push in executable state");
                }).
                And(function () {
                    jCC.equal(actor.pendingJobs.length, 0, "no new job");
                }).
                When(function () {
                    coordinator.jobRunner();
                }).
                Then(function () {
                    jCC.equal(coordinator.pendingJobs.length, 0, "Job has been executed");
                }).
                And(function () {
                    jCC.equal(response.value(), "A.n()", "Checking response type");
                });
        });

        jCC.scenario("Check actor indirect failed invocation simulated", function () {
            var coordinator, actor, response;

            jCC.
                Given(function () {
                    coordinator = jActor.coordinator();
                }).
                And(function () {
                    actor = coordinator.actor("A").bindToObject(new A());
                }).
                And(function () {
                    response = storedResponse();
                }).
                When(function () {
                    actor.ask(jActor.event.request("m", []), response);
                }).
                Then(function () {
                    jCC.equal(actor.pendingJobs.length, 1, "A new job has been created");
                }).
                When(function () {
                    coordinator.actorRunner();
                }).
                Then(function () {
                    jCC.equal(coordinator.pendingJobs.length, 1, "Job has been push in executable state");
                }).
                And(function () {
                    jCC.equal(actor.pendingJobs.length, 0, "no new job");
                }).
                When(function () {
                    coordinator.jobRunner();
                }).
                Then(function () {
                    jCC.equal(coordinator.pendingJobs.length, 0, "Job has been executed");
                }).
                When(function () {
                    response.value();
                }).
                ThenError(function (e) {
                    jCC.equal(e, "A.m()", "Job has been executed and an exception has been raised");
                    coordinator.stop();
                });
        });

        jCC.scenario("Check actor indirect successful message sent", function () {
            var coordinator, actor, response;

            jCC.
                Given(function () {
                    coordinator = jActor.coordinator();
                    coordinator.start();
                }).
                And(function () {
                    actor = coordinator.actor("A").bindToObject(new A());
                }).
                And(function () {
                    response = storedResponse();
                }).
                When(function () {
                    actor.ask(jActor.event.request("n", []), response);
                }).
                ThenAfter(1000, function () {
                    jCC.equal(response.value(), "A.n()", "Checking response type");
                    coordinator.stop();
                });
        });

        jCC.scenario("Check actor indirect using manager successful message sent", function () {
            var coordinator, response;

            jCC.
                Given(function () {
                    coordinator = jActor.coordinator();
                    coordinator.start();
                }).
                And(function () {
                    coordinator.actor("A").bindToObject(new A());
                }).
                And(function () {
                    response = storedResponse();
                }).
                When(function () {
                    coordinator.ask("A", jActor.event.request("n", []), response);
                }).
                ThenAfter(500, function () {
                    jCC.equal(response.value(), "A.n()", "Checking response type");
                    coordinator.stop();
                });
        });

        jCC.scenario("Check actor indirect using manager successful message sent to first un-started coordinator", function () {
            var coordinator, response;

            jCC.
                Given(function () {
                    coordinator = jActor.coordinator();
                }).
                And(function () {
                    coordinator.actor("A").bindToObject(new A());
                }).
                And(function () {
                    response = storedResponse();
                }).
                When(function () {
                    coordinator.ask("A", jActor.event.request("n", []), response);
                }).
                And(function () {
                    coordinator.start();
                }).
                ThenAfter(500, function () {
                    jCC.equal(response.value(), "A.n()", "Checking response type");
                    coordinator.stop();
                });
        });

        jCC.scenario("Check actor indirect using manager successful message sent to first unbound coordinator", function () {
            var coordinator, response, actor;

            jCC.
                Given(function () {
                    coordinator = jActor.coordinator();
                    coordinator.start();
                }).
                And(function () {
                    actor = coordinator.actor("A");
                }).
                And(function () {
                    response = storedResponse();
                }).
                When(function () {
                    coordinator.ask("A", jActor.event.request("n", []), response);
                }).
                And(function () {
                    actor.bindToObject(new A());
                }).
                ThenAfter(500, function () {
                    jCC.equal(response.value(), "A.n()", "Checking response type");
                    coordinator.stop();
                });
        });

        jCC.scenario("Check actor indirect failed message sent", function () {
            var coordinator, actor, response;

            jCC.
                Given(function () {
                    coordinator = jActor.coordinator();
                    coordinator.start();
                }).
                And(function () {
                    actor = coordinator.actor("A").bindToObject(new A());
                }).
                And(function () {
                    response = storedResponse();
                }).
                When(function () {
                    actor.ask(jActor.event.request("m", []), response);
                }).
                ThenAfter(500, function () {
                    try {
                        response.value();
                        jCC.equal(true, false, "expecting an exception");
                    } catch (e) {
                        jCC.equal(e, "A.m()", "Job has been executed and an exception has been raised");
                    }
                    coordinator.stop();
                });
        });

        jCC.scenario("Check actor indirect using manager failed message sent", function () {
            var manager, response;

            jCC.
                Given(function () {
                    manager = jActor.coordinator();
                    manager.start();
                }).
                And(function () {
                    manager.actor("A").bindToObject(new A());
                }).
                And(function () {
                    response = storedResponse();
                }).
                When(function () {
                    manager.ask("A", jActor.event.request("m", []), response);
                }).
                ThenAfter(500, function () {
                    try {
                        response.value();
                        jCC.equal(true, false, "expecting an exception");
                    } catch (e) {
                        jCC.equal(e, "A.m()", "Job has been executed and an exception has been raised");
                    }
                    manager.stop();
                });
        });

        jCC.scenario("Check undefined actor indirect failed message sent", function () {
            var coordinator, response;

            jCC.
                Given(function () {
                    coordinator = jActor.coordinator();
                    coordinator.start();
                }).
                And(function () {
                    response = storedResponse();
                }).
                When(function () {
                    coordinator.ask("A", jActor.event.request("n", []), response);
                }).
                ThenAfter(500, function () {
                    try {
                        response.value();
                        jCC.equal(true, false, "expecting an exception");
                    } catch (e) {
                        jCC.equal(e.message, "L.actor.not.found", "Actor does not exist");
                    }
                    coordinator.stop();
                });
        });

        jCC.scenario("Check sourced module actor indirect using manager", function () {
            var coordinator, response;

            jCC.
                Given(function () {
                    coordinator = jActor.coordinator();
                    coordinator.start();
                }).
                And(function () {
                    coordinator.actor("AtomicString").bindToSource("./AtomicString.js").onLoad(function () {
                        return require("Atomic.String")("Hello!");
                    });
                }).
                And(function () {
                    response = storedResponse();
                }).
                When(function () {
                    coordinator.ask("AtomicString", jActor.event.request("getValue", []), response);
                }).
                ThenAfter(500, function () {
                    jCC.equal(response.value(), "Hello!", "Actor must respond 'Hello!'");
                    coordinator.stop();
                });
        });

        jCC.scenario("Check sourced simple actor indirect using manager", function () {
            var coordinator, response;

            jCC.
                Given(function () {
                    coordinator = jActor.coordinator();
                    coordinator.start();
                }).
                And(function () {
                    coordinator.actor("SimpleString").bindToSource("./SimpleString.js").onLoad(function () {
                        return SimpleObject;
                    });
                }).
                And(function () {
                    response = storedResponse();
                }).
                When(function () {
                    coordinator.ask("SimpleString", jActor.event.request("getName", []), response);
                }).
                ThenAfter(500, function () {
                    jCC.equal(response.value(), "A simple name", "Actor must respond 'A simple name'");
                    coordinator.stop();
                });
        });

        jCC.scenario("Check sourced module actor indirect using manager failed message sent [New formulation]", function () {
            var coordinator, response;

            jCC.
                Given(function () {
                    coordinator = jActor.coordinator();
                    coordinator.start();
                }).
                And(function () {
                    response = storedResponse();
                }).
                When(function () {
                    jLoader().
                        source("./AtomicString.js").
                        onLoad(function () {
                            coordinator.actor("AtomicString").bindToModule("Atomic.String", [ "Hello!" ]);
                            coordinator.ask("AtomicString", jActor.event.request("getValue", []), response);
                        });
                }).
                ThenAfter(500, function () {
                    jCC.equal(response.value(), "Hello!", "Actor must respond 'Hello!'");
                    coordinator.stop();
                });
        });
    });

