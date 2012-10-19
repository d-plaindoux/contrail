/*global require */

require([ "./jObj", "qunit" ], function(jObj, QUnit) {
    /**
     * Test Type Checking
     */
    function A() {
        jObj.bless(this);
        this.a = "a";
    }

    function B() {
        jObj.bless(this, new A());
        this.b = { hello : "World!"};
    }

    QUnit.test("Check Subtype a:A <? A", function() {
        var a = new A();
        QUnit.equal(jObj.instanceOf(a,"A"),true, "Checking a:A instance of A");
    });

    QUnit.test("Check Subtype b:B <? B", function() {
        var b = new B();
        QUnit.equal(jObj.instanceOf(b,"B"),true, "Checking b:B instance of B");
    });

    QUnit.test("Check Subtype b:B <? A", function() {
        var b = new B();
        QUnit.equal(jObj.instanceOf(b,"A"),true, "Checking b:B extends A instance of A");
    });
    
    QUnit.test("Check toString", function() {
        var b = new B();
        QUnit.equal(jObj.instanceOf(b,"A"),true, "Checking b:B extends A instance of A");
    });
   
});

