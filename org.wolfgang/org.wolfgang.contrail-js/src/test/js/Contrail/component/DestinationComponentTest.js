/*global require */

require([ "./DestinationComponent", "qunit" ], function(DestinationComponent, QUnit) {
    /**
     * Test generation
     */
    QUnit.test("Check Component generation", function() {
        var c1 = new DestinationComponent(), c2 = new DestinationComponent();        
        QUnit.notEqual(c1.getComponentId(), c2.getComponentId(), "Two fresh components must be different");
    });

    /**
     * Test source
     */
    QUnit.test("Check Component generation", function() {
        var c1 = new DestinationComponent();        
        QUnit.equal(c1.source, null, "Source must be null");
    });

    /**
     * Test source
     */
    QUnit.test("Check Component generation", function() {
        var c1 = new DestinationComponent();        
        QUnit.equal(c1.source, null, "Source must be null");
    });
});