/*global require */

require([ "./SourceComponent", "qunit" ], function(SourceComponent, QUnit) {
    /**
     * Test generation
     */
    QUnit.test("Check Component generation", function() {
        var c1 = new SourceComponent(), c2 = new SourceComponent();
        
        QUnit.notEqual(c1.getComponentId(), c2.getComponentId(), "Two fresh components must be different");
    });
    
    /**
     * Test source
     */
    QUnit.test("Check Component generation", function() {
        var c1 = new SourceComponent();        
        QUnit.equal(c1.destination, null, "Source must be null");
    });

});