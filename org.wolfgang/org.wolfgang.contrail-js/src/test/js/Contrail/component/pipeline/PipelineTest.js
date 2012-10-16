/*global require */

require([ "../../Factory", "qunit" ], 
function(Factory, QUnit) {
    /**
     * Test generation
     */
    QUnit.test("Check Component generation", function() {
        var c1 = Factory.component(), 
            c2 = Factory.component();
        
        QUnit.notEqual(c1.getComponentId(), c2.getComponentId(), "Two fresh components must be different");
    });
});