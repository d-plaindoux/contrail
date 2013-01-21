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

require([ "Core/object/jObj", "Core/utils/jTransducer", "Core/test/jCC" ],
    function (jObj, jTransducer, jCC) {
        "use strict";

        // ---------------------------------------------------------

        function A(m) {
            jObj.bless(this);
            this.id = m;
            this.a = "a";
        }

        A.prototype.n = function () {
            return "A.n() for " + this.id;
        };

        A.prototype.m = function () {
            return "A.m() for " + this.id;
        };

        // ---------------------------------------------------------

        function B() {
            jObj.bless(this, new A("b"));
            this.b = { hello:"World!"};
        }

        B.prototype.m = function () {
            return "B.m() for " + this.id;
        };

        // ---------------------------------------------------------

        function C() {
            jObj.bless(this, new A("c"), new B("c"));
            this.c = 2013;
        }

        C.prototype.m = function () {
            return "C.m() for " + this.id;
        };

        // ---------------------------------------------------------

        function D() {
            jObj.bless(this, new C("d"));
        }

        D.prototype.m = function () {
            return "D.m() for " + this.id;
        };

        // ---------------------------------------------------------

        jCC.scenario("Check Subtype a:A <? A", function () {
            var a;

            jCC.
                Given(function () {
                    a = new A();
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(jObj.ofType(a, "A"), true, "Checking a:A instance of A");
                });
        });

        jCC.scenario("Check Subtype b:B <? B", function () {
            var b;

            jCC.
                Given(function () {
                    b = new B();
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(jObj.ofType(b, "B"), true, "Checking b:B instance of B");
                });
        });

        jCC.scenario("Check Subtype b:B <? A", function () {
            var b;

            jCC.
                Given(function () {
                    b = new B();
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(jObj.ofType(b, "A"), true, "Checking b:B extends A instance of A");
                });
        });

        jCC.scenario("CheckType(<string>)", function () {
            var b;

            jCC.
                Given(function () {
                    b = "b";
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(jObj.ofType(b, jObj.types.String), true, "Checking Type(<string>)");
                });
        });

        jCC.scenario("CheckType(<number>)", function () {
            var b;

            jCC.
                Given(function () {
                    b = 123;
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(jObj.ofType(b, jObj.types.Number), true, "Checking Type(<number>)");
                });
        });

        jCC.scenario("CheckType(<boolean>)", function () {
            var b;

            jCC.
                Given(function () {
                    b = true;
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(jObj.ofType(b, jObj.types.Boolean), true, "Checking Type(<boolean>)");
                });
        });

        jCC.scenario("CheckType(<undefined>)", function () {
            var b; // = undefined;

            jCC.
                Given(jCC.Nothing).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(jObj.ofType(b, jObj.types.Undefined), true, "Checking Type(<undefined>)");
                });
        });

        jCC.scenario("CheckType(<object>)", function () {
            var b;

            jCC.
                Given(function () {
                    b = {};
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(jObj.ofType(b, jObj.types.Object), true, "Checking Type(<object>)");
                });
        });

        jCC.scenario("CheckType(<array>)", function () {
            var b;

            jCC.
                Given(function () {
                    b = [];
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(jObj.ofType(b, jObj.types.Array), true, "Checking Type(<array>)");
                });
        });

        jCC.scenario("CheckType(<object>)", function () {
            var b, tb;

            jCC.
                Given(function () {
                    b = new B();
                }).
                When(function () {
                    tb = jTransducer.toType(b);
                }).
                Then(function () {
                    jCC.equal(jObj.ofType(tb.a, jObj.types.String), true, "Checking Type(<string>)");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(tb.b, jObj.types.Object), true, "Checking getType(<object>)");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(tb.b.hello, jObj.types.String), true, "Checking getType(<string>)");
                });
        });

        jCC.scenario("Method polymorphism without override", function () {
            var b, r;

            jCC.
                Given(function () {
                    b = new B();
                }).
                When(function () {
                    r = b.n();
                }).
                Then(function () {
                    jCC.equal(r, "A.n() for b", "Method polymorphism without override is A.n()");
                });
        });

        jCC.scenario("Method polymorphism with override", function () {
            var b, r;

            jCC.
                Given(function () {
                    b = new B();
                }).
                When(function () {
                    r = b.m();
                }).
                Then(function () {
                    jCC.equal(r, "B.m() for b", "Method polymorphism with override is B.m()");
                });
        });

        jCC.scenario("Method polymorphism with override and calling extension A", function () {
            var b, r;

            jCC.
                Given(function () {
                    b = new B();
                }).
                When(function () {
                    r = b.extension.A.m();
                }).
                Then(function () {
                    jCC.equal(r, "A.m() for b", "Super method polymorphism with override is A.m()");
                });
        });

        jCC.scenario("Method polymorphism with override and calling superclass which is A", function () {
            var b, r;

            jCC.
                Given(function () {
                    b = new B();
                }).
                When(function () {
                    r = b.superclass.m();
                }).
                Then(function () {
                    jCC.equal(r, "A.m() for b", "Super method polymorphism with override is A.m()");
                });
        });

        jCC.scenario("Method polymorphism with override and calling superclass which is A and extending B", function () {
            var c, r;

            jCC.
                Given(function () {
                    c = new C();
                }).
                When(function () {
                    r = c.superclass.m();
                }).
                Then(function () {
                    jCC.equal(r, "A.m() for c", "Super method polymorphism with override is A.m()");
                });
        });

        jCC.scenario("Checking type of C inheriting A and extending B", function () {
            var c, r;

            jCC.
                Given(function () {
                    c = new C();
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(jObj.ofType(c, jObj.types.Named("A")), true, "c is a A");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(c, jObj.types.Named("B")), true, "c is also B");
                });
        });

        jCC.scenario("Checking type of D inheriting C", function () {
            var d, r;

            jCC.
                Given(function () {
                    d = new D();
                }).
                When(jCC.Nothing).
                Then(function () {
                    jCC.equal(jObj.ofType(d, jObj.types.Named("C")), true, "d is a C");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(d, jObj.types.Named("A")), true, "d is also A");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(d, jObj.types.Named("B")), true, "d is also B");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(d.superclass, jObj.types.Named("C")), true, "d.superclass is C");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(d.extension.C, jObj.types.Named("C")), true, "d.extension.C is C");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(d.superclass.superclass, jObj.types.Named("A")), true, "d.superclass.superclass is A");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(d.superclass.extension.A, jObj.types.Named("A")), true, "d.superclass.extension.A is also A");
                }).
                And(function () {
                    jCC.equal(jObj.ofType(d.superclass.extension.B, jObj.types.Named("B")), true, "d.superclass.extension.B is also B");
                });
        });
    });


