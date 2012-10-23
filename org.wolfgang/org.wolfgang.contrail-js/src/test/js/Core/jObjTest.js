/*global require */

require([ "Core/jObj", "qunit" ], function(jObj, QUnit) {
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
    
    QUnit.test("Check getType(<string>)", function() {
        var b = "b";        
        QUnit.equal(jObj.toType(b), jObj.types.String , "Checking getType(<string>)");
    });
    
    QUnit.test("Check getType(<number>)", function() {
        var b = 123;        
        QUnit.equal(jObj.toType(b), jObj.types.Number , "Checking getType(<number>)");
    });
   
    QUnit.test("Check getType(<boolean>)", function() {
        var b = true;        
        QUnit.equal(jObj.toType(b), jObj.types.Boolean , "Checking getType(<boolean>)");
    });
   
    QUnit.test("Check getType(<undefined>)", function() {
        var b; // = undefined;        
        QUnit.equal(jObj.toType(b), jObj.types.Undefined , "Checking getType(<undefined>)");
    });
      
    QUnit.test("Check getType(<object>)", function() {
        var b = {};        
        QUnit.equal(typeof jObj.toType(b), jObj.types.Object , "Checking getType(<object>)");
    });
      
    QUnit.test("Check getType(<object>)", function() {
        var b, tb;
        b = new B();
        tb = jObj.toType(b);
        QUnit.equal(tb.a, jObj.types.String , "Checking getType(<object>)");
        QUnit.equal(typeof tb.b, jObj.types.Object , "Checking getType(<object>)");
        QUnit.equal(tb.b.hello, jObj.types.String , "Checking getType(<object>)");
    });
});

