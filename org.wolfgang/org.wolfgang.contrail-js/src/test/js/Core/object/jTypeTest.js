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

require([ "Core/object/jObj", "qunit", "test/jCC" ],
    function (jObj, QUnit, jCC) {
        "use strict";

        function A() {
            jObj.bless(this);
        }

        jCC.scenario("Check Subtype a:A <? A", function () {
            var a;

            jCC.
                Given(function () {
                    a = new A();
                }).
                When(function () {
                    jObj.checkType(a, "A");
                }).
                Then(function () {
                    QUnit.equal(true, true, " a is an instance of A");
                });
        });

        jCC.scenario("Check Subtype a:A <? B", function () {
            var a;

            jCC.
                Given(function () {
                    a = new A();
                }).
                When(function () {
                    jObj.checkType(a, "B");
                }).
                ThenError(function (e) {
                    QUnit.equal(jObj.ofType(e, jObj.types.Named("RuntimeTypeError")), true, "Checking throws error to be a TypeError");
                });
        });

        jCC.scenario("Check Subtype a:A <? {}", function () {
            var a;

            jCC.
                Given(function () {
                    a = new A();
                }).
                When(function () {
                    jObj.checkType(a, jObj.types.ObjectOf({}));
                }).
                Then(function () {
                    QUnit.equal(true, true, "a is not an instance of {m:Function}");
                });
        });

        jCC.scenario("Check wrong Subtype a:A <? {m:Function}", function () {
            var a;

            jCC.
                Given(function () {
                    a = new A();
                }).
                When(function () {
                    jObj.checkType(a, jObj.types.ObjectOf({m:jObj.types.Function}));
                }).
                ThenError(function (e) {
                    QUnit.equal(jObj.ofType(e, jObj.types.Named("RuntimeTypeError")), true, "Checking throws error to be a TypeError");
                });
        });

        jCC.scenario("Check Subtype a:A <? {m:Function}", function () {
            var a;

            jCC.
                Given(function () {
                    A.prototype.m = function () {
                        // Empty constructor
                    };
                }).
                And(function () {
                    a = new A();
                }).
                When(function () {
                    jObj.checkType(a, jObj.types.ObjectOf({m:jObj.types.Function}));
                }).
                Then(function () {
                    QUnit.equal(true, true, "a is an instance of {m:Function}");
                }).
                And(function () {
                    A.prototype.m = undefined;
                });
        });

        jCC.scenario("Check Subtype a:String <? Nullable(String)", function () {
            var a;

            jCC.
                Given(function () {
                    a = "...";
                }).
                When(function () {
                    jObj.checkType(a, jObj.types.Nullable(jObj.types.String));
                }).
                Then(function () {
                    QUnit.equal(true, true, "a is an instance of Nullable(String)");
                }).
                And(function () {
                    A.prototype.m = undefined;
                });
        });

        jCC.scenario("Check Subtype a:Undefined <? Nullable(String)", function () {
            var a;

            jCC.
                Given(function () {
                    a = undefined;
                }).
                When(function () {
                    jObj.checkType(a, jObj.types.Nullable(jObj.types.String));
                }).
                Then(function () {
                    QUnit.equal(true, true, "a is an instance of Nullable(String)");
                }).
                And(function () {
                    A.prototype.m = undefined;
                });
        });
    });

