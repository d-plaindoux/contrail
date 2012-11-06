/*global require */

require([ "Core/jObj", "qunit" ], function (jObj, QUnit) {
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

    QUnit.test("Check Subtype a:A <? A", function () {
        var a = new A();
        QUnit.equal(jObj.instanceOf(a, "A"), true, "Checking a:A instance of A");
    });

    QUnit.test("Check Subtype b:B <? B", function () {
        var b = new B();
        QUnit.equal(jObj.instanceOf(b, "B"), true, "Checking b:B instance of B");
    });

    QUnit.test("Check Subtype b:B <? A", function () {
        var b = new B();
        QUnit.equal(jObj.instanceOf(b, "A"), true, "Checking b:B extends A instance of A");
    });

    QUnit.test("CheckType(<string>)", function () {
        var b = "b";
        QUnit.equal(jObj.instanceOf(b, jObj.types.String), true, "Checking Type(<string>)");
    });

    QUnit.test("CheckType(<number>)", function () {
        var b = 123;
        QUnit.equal(jObj.instanceOf(b, jObj.types.Number), true, "Checking Type(<number>)");
    });

    QUnit.test("CheckType(<boolean>)", function () {
        var b = true;
        QUnit.equal(jObj.instanceOf(b, jObj.types.Boolean), true, "Checking Type(<boolean>)");
    });

    QUnit.test("CheckType(<undefined>)", function () {
        var b; // = undefined;        
        QUnit.equal(jObj.instanceOf(b, jObj.types.Undefined), true, "Checking Type(<undefined>)");
    });

    QUnit.test("CheckType(<object>)", function () {
        var b = {};
        QUnit.equal(jObj.instanceOf(b, jObj.types.Object), true, "Checking Type(<object>)");
    });

    QUnit.test("CheckType(<array>)", function () {
        var b = [];
        QUnit.equal(jObj.instanceOf(b, jObj.types.Array), true, "Checking Type(<array>)");
    });

    QUnit.test("CheckType(<object>)", function () {
        var b, tb;
        b = new B();
        tb = jObj.toType(b);
        QUnit.equal(tb.a, jObj.types.String, "Checking Type(<object>)");
        QUnit.equal(jObj.instanceOf(tb.b, jObj.types.Object), true, "Checking getType(<object>)");
        QUnit.equal(tb.b.hello, jObj.types.String, "Checking getType(<object>)");
    });
});

