/*global require */

require([ "jquery", "./Strict", "qunit" ], function($, Strict, QUnit) {
    /**
     * Test Type Checking
     */
	function A() {
	}
	
	function B() {
		var a = new A();
	    $.extend(new A());
	}
     
    QUnit.test("Check Subtype a:A <? A", function() {
        var a = new A();
        QUnit.equal(Strict.subType(a,"A"),true, "Checking a:A instance of A");
    });

    QUnit.test("Check Subtype b:B <? B", function() {
        var b = new B();
        QUnit.equal(Strict.subType(b,"B"),true, "Checking b:B instance of B");
    });

    QUnit.test("Check Subtype b:B <? A", function() {
        var b = new B();
        QUnit.equal(Strict.subType(b,"A"),true, "Checking b:B extends A instance of A");
    });
});

