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

require([ "Core/object/jObj", "Core/test/jCC", "Actor/jActor", "../common/StoredResponse" ],
    function (jObj, jCC, jActor, storedResponse) {
        "use strict";

        // ---------------------------------------------------------

        function A() {
            jObj.bless(this);
            this.a = "a";
        }

        A.prototype.coordinator = undefined;

        A.prototype.n = function () {
            return "A.n()";
        };

        A.prototype.m = function () {
            throw "A.m()";
        };

        // ---------------------------------------------------------

        jCC.scenario("Check actor creation", function () {
            var coordinator, actor;

            jCC.
                Given(function () {
                    coordinator = jActor.coordinator();
                }).
                And(function () {
                    actor = coordinator.actor("A").bindToObject(new A());
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(jObj.ofType(actor, jObj.types.Named("Actor")), true, "Checking a:A instance of Actor");
                });
        });

        jCC.scenario("Check actor creation not bind", function () {
            var coordinator, actor;

            jCC.
                Given(function () {
                    coordinator = jActor.coordinator();
                }).
                And(function () {
                    actor = coordinator.actor("A").bindToObject(new A());
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(actor.isBound(), false, "Checking a:A unbound instance of Actor");
                });
        });

        jCC.scenario("Check actor creation define entry in the coordinator", function () {
            var coordinator, actor;

            jCC.
                Given(function () {
                    coordinator = jActor.coordinator();
                }).
                And(function () {
                    actor = coordinator.actor("A").bindToObject(new A());
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(coordinator.hasActor("A"), true, "Checking a:A is defined in the coordinator");
                });
        });

        jCC.scenario("Bound actor has a coordinator property defined", function () {
            var coordinator, actor, model;

            jCC.
                Given(function () {
                    coordinator = jActor.coordinator();
                }).
                And(function () {
                    model = new A();
                }).
                When(function () {
                    actor = coordinator.actor("A").bindToObject(model);
                }).
                Then(function () {
                    jCC.equal(model.coordinator, coordinator, "Checking coordinator aliasing");
                }).
                And(function () {
                    jCC.equal(model.actorId, "A", "Checking actor identifier aliasing");
                });
        });

        jCC.scenario("Check actor direct invocation", function () {
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
                    actor.invoke(jActor.event.request("n", []), response);
                }).
                Then(function () {
                    jCC.equal(response.value(), "A.n()", "Checking response type");
                });
        });
    });

