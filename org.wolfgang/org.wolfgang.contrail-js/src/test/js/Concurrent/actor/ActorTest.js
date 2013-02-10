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

require([ "Core/object/jObj", "Core/test/jCC", "Concurrent/actor/jActor", "Concurrent/event/jEvent", "../common/StoredResponse" ],
    function (jObj, jCC, jActor, jEvent, storedResponse) {
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

        jCC.scenario("Check actor creation", function () {
            var manager, actor;

            jCC.
                Given(function () {
                    manager = jActor.coordinator();
                }).
                And(function () {
                    actor = manager.actor("test.a").bindToObject(new A());
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(jObj.ofType(actor, jObj.types.Named("Actor")), true, "Checking a:A instance of Actor");
                });
        });

        jCC.scenario("Check actor direct invocation", function () {
            var manager, actor, response;

            jCC.
                Given(function () {
                    manager = jActor.coordinator();
                }).
                And(function () {
                    actor = manager.actor("test.a").bindToObject(new A());
                }).
                And(function () {
                    response = storedResponse();
                }).
                When(function () {
                    actor.invoke(jEvent.request("n", []), response);
                }).
                Then(function () {
                    jCC.equal(response.value(), "A.n()", "Checking response type");
                });
        });

        jCC.scenario("Check actor indirect successful invocation simulated", function () {
            var manager, actor, response;

            jCC.
                Given(function () {
                    manager = jActor.coordinator();
                }).
                And(function () {
                    actor = manager.actor("test.a").bindToObject(new A());
                }).
                And(function () {
                    response = storedResponse();
                }).
                When(function () {
                    actor.send(jEvent.request("n", []), response);
                }).
                Then(function () {
                    jCC.equal(actor.jobs.length, 1, "A new job has been created");
                }).
                When(function () {
                    manager.actorRunner();
                }).
                Then(function () {
                    jCC.equal(manager.jobs.length, 1, "Job has been push in executable state");
                }).
                And(function () {
                    jCC.equal(actor.jobs.length, 0, "no new job");
                }).
                When(function () {
                    manager.jobRunner();
                }).
                Then(function () {
                    jCC.equal(manager.jobs.length, 0, "Job has been executed");
                }).
                And(function () {
                    jCC.equal(response.value(), "A.n()", "Checking response type");
                });
        });

        jCC.scenario("Check actor indirect failed invocation simulated", function () {
            var manager, actor, response;

            jCC.
                Given(function () {
                    manager = jActor.coordinator();
                }).
                And(function () {
                    actor = manager.actor("test.a").bindToObject(new A());
                }).
                And(function () {
                    response = storedResponse();
                }).
                When(function () {
                    actor.send(jEvent.request("m", []), response);
                }).
                Then(function () {
                    jCC.equal(actor.jobs.length, 1, "A new job has been created");
                }).
                When(function () {
                    manager.actorRunner();
                }).
                Then(function () {
                    jCC.equal(manager.jobs.length, 1, "Job has been push in executable state");
                }).
                And(function () {
                    jCC.equal(actor.jobs.length, 0, "no new job");
                }).
                When(function () {
                    manager.jobRunner();
                }).
                Then(function () {
                    jCC.equal(manager.jobs.length, 0, "Job has been executed");
                }).
                When(function () {
                    response.value();
                }).
                ThenError(function (e) {
                    jCC.equal(e, "A.m()", "Job has been executed and an exception has been raised");
                    manager.stop();
                });
        });

        jCC.scenario("Check actor indirect successful message sent", function () {
            var manager, actor, response;

            jCC.
                Given(function () {
                    manager = jActor.coordinator();
                }).
                And(function () {
                    manager.start();
                }).
                And(function () {
                    actor = manager.actor("test.a").bindToObject(new A());
                }).
                And(function () {
                    response = storedResponse();
                }).
                When(function () {
                    actor.send(jEvent.request("n", []), response);
                }).
                ThenAfter(500, function () {
                    jCC.equal(response.value(), "A.n()", "Checking response type");
                    manager.stop();
                });
        });

        jCC.scenario("Check actor indirect using manager successful message sent", function () {
            var manager, response;

            jCC.
                Given(function () {
                    manager = jActor.coordinator();
                }).
                And(function () {
                    manager.start();
                }).
                And(function () {
                    manager.actor("test.a").bindToObject(new A());
                }).
                And(function () {
                    response = storedResponse();
                }).
                When(function () {
                    manager.send("test.a", jEvent.request("n", []), response);
                }).
                ThenAfter(500, function () {
                    jCC.equal(response.value(), "A.n()", "Checking response type");
                    manager.stop();
                });
        });
        jCC.scenario("Check actor indirect failed message sent", function () {
            var manager, actor, response;

            jCC.
                Given(function () {
                    manager = jActor.coordinator();
                }).
                And(function () {
                    manager.start();
                }).
                And(function () {
                    actor = manager.actor("test.a").bindToObject(new A());
                }).
                And(function () {
                    response = storedResponse();
                }).
                When(function () {
                    actor.send(jEvent.request("m", []), response);
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

        jCC.scenario("Check actor indirect using manager failed message sent", function () {
            var manager, response;

            jCC.
                Given(function () {
                    manager = jActor.coordinator();
                }).
                And(function () {
                    manager.start();
                }).
                And(function () {
                    manager.actor("test.a").bindToObject(new A());
                }).
                And(function () {
                    response = storedResponse();
                }).
                When(function () {
                    manager.send("test.a", jEvent.request("m", []), response);
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
            var manager, response;

            jCC.
                Given(function () {
                    manager = jActor.coordinator();
                }).
                And(function () {
                    manager.start();
                }).
                And(function () {
                    response = storedResponse();
                }).
                When(function () {
                    manager.send("test.a", jEvent.request("n", []), response);
                }).
                ThenAfter(500, function () {
                    try {
                        response.value();
                        jCC.equal(true, false, "expecting an exception");
                    } catch (e) {
                        jCC.equal(e.message, "L.actor.not.found", "Actor does not exist");
                    }
                    manager.stop();
                });
        });
    });
