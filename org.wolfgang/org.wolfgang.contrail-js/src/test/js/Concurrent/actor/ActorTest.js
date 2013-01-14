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

require([ "Core/object/jObj", "qunit", "test/jCC", "Concurrent/actor/jActor" ],
    function (jObj, QUnit, jCC, jActor) {
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
            return "A.m()";
        };

        // ---------------------------------------------------------

        jCC.scenario("Check actor creation", function () {
            var manager, actor;

            jCC.
                Given(function () {
                    manager = jActor.manager();
                }).
                And(function () {
                    actor = manager.actor("test.a", new A());
                }).
                WhenNothing.
                Then(function () {
                    QUnit.equal(jObj.ofType(actor, jObj.types.Named("A")), true, "Checking a:A instance of A");
                }).
                And(function () {
                    QUnit.equal(jObj.ofType(actor, jObj.types.Named("Actor")), true, "Checking a:A instance of Actor");
                });
        });

    });

