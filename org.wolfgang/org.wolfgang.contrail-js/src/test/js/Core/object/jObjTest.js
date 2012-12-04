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

require([ "Core/jObj", "qunit", "test/jCC" ],
    function (jObj, QUnit, jCC) {
        "use strict";

        /**
         * Test Type Checking
         */
        function A() {
            jObj.bless(this);
            this.a = "a";
        }

        function B() {
            jObj.bless(this, new A());
            this.b = { hello:"World!"};
        }

        jCC.scenario("Check Subtype a:A <? A", function () {
            var a;

            jCC.
                Given(function () {
                    a = new A();
                }).
                WhenNothing.
                Then(function () {
                    QUnit.equal(jObj.ofType(a, "A"), true, "Checking a:A instance of A");
                });
        });

        jCC.scenario("Check Subtype b:B <? B", function () {
            var b;

            jCC.
                Given(function () {
                    b = new B();
                }).
                WhenNothing.
                Then(function () {
                    QUnit.equal(jObj.ofType(b, "B"), true, "Checking b:B instance of B");
                });
        });

        jCC.scenario("Check Subtype b:B <? A", function () {
            var b;

            jCC.
                Given(function () {
                    b = new B();
                }).
                WhenNothing.
                Then(function () {
                    QUnit.equal(jObj.ofType(b, "A"), true, "Checking b:B extends A instance of A");
                });
        });

        jCC.scenario("CheckType(<string>)", function () {
            var b;

            jCC.
                Given(function () {
                    b = "b";
                }).
                WhenNothing.
                Then(function () {
                    QUnit.equal(jObj.ofType(b, jObj.types.String), true, "Checking Type(<string>)");
                });
        });

        jCC.scenario("CheckType(<number>)", function () {
            var b;

            jCC.
                Given(function () {
                    b = 123;
                }).
                WhenNothing.
                Then(function () {
                    QUnit.equal(jObj.ofType(b, jObj.types.Number), true, "Checking Type(<number>)");
                });
        });

        jCC.scenario("CheckType(<boolean>)", function () {
            var b;

            jCC.
                Given(function () {
                    b = true;
                }).
                WhenNothing.
                Then(function () {
                    QUnit.equal(jObj.ofType(b, jObj.types.Boolean), true, "Checking Type(<boolean>)");
                });
        });

        jCC.scenario("CheckType(<undefined>)", function () {
            var b; // = undefined;

            jCC.
                GivenNothing.
                WhenNothing.
                Then(function () {
                    QUnit.equal(jObj.ofType(b, jObj.types.Undefined), true, "Checking Type(<undefined>)");
                });
        });

        jCC.scenario("CheckType(<object>)", function () {
            var b;

            jCC.
                Given(function () {
                    b = {};
                }).
                WhenNothing.
                Then(function () {
                    QUnit.equal(jObj.ofType(b, jObj.types.Object), true, "Checking Type(<object>)");
                });
        });

        jCC.scenario("CheckType(<array>)", function () {
            var b;

            jCC.
                Given(function () {
                    b = [];
                }).
                WhenNothing.
                Then(function () {
                    QUnit.equal(jObj.ofType(b, jObj.types.Array), true, "Checking Type(<array>)");
                });
        });

        jCC.scenario("CheckType(<object>)", function () {
            var b, tb;

            jCC.
                Given(function () {
                    b = new B();
                }).
                When(function () {
                    tb = jObj.toType(b);
                }).
                Then(function () {
                    QUnit.equal(jObj.ofType(tb.a, jObj.types.String), true, "Checking Type(<string>)");
                }).
                And(function () {
                    QUnit.equal(jObj.ofType(tb.b, jObj.types.Object), true, "Checking getType(<object>)");
                }).
                And(function () {
                    QUnit.equal(jObj.ofType(tb.b.hello, jObj.types.String), true, "Checking getType(<string>)");
                });
        });
    });

