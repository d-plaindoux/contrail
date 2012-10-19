/*global require */

require([ "../core/jObj", "./Strict", "qunit" ], 
function(jObj, Strict, QUnit) {
    /**
     * Test Type Checking
     */
	function A() {
	    jObj.bless(this);
	}

    QUnit.test("Check Subtype a:A <? A", function() {
        var a = new A();
        Strict.assertType(a,"A");
        QUnit.equal(true,true," a is an instance of A");
    });

    QUnit.test("Check Subtype a:A <? B", function() {
        var a = new A();
        try {
            Strict.assertType(a,"B");
            QUnit.equal(true,false,"a is not an instance of B");
        } catch (e) {
            QUnit.equal(jObj.instanceOf(e,"AssertTypeError"), true, "Checking throws error to be a TypeError");
        }
    });
});

