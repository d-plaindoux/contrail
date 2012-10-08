/*global require */

require([ "./Component", "qunit" ], function(Component, QUnit) {
    /**
     * Test generation
     */
    QUnit.test("Check Component generation", function() {
        var c1 = new Component(), c2 = new Component();
        
        QUnit.notEqual(c1.getComponentId(), c2.getComponentId(), "Two fresh components must be different");
    });
});