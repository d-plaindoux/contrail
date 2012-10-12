/*global require */

require([ "../Factory", "qunit" ], 
function(Factory, QUnit) {
    /**
     * Test generation
     */
    QUnit.test("Check Component generation", function() {
        var c1 = Factory.destinationComponent(Factory.dataFlow()), 
            c2 = Factory.destinationComponent(Factory.dataFlow());        
        QUnit.notEqual(c1.getComponentId(), c2.getComponentId(), "Two fresh components must be different");
    });

    /**
     * Test source
     */
    QUnit.test("Check Component source linkage acceptation", function() {
        var c1 = Factory.destinationComponent(Factory.dataFlow()),
            c2 = Factory.sourceComponent(Factory.dataFlow());
        QUnit.equal(c1.acceptSource(c2.getComponentId()), true, "Source must be null");
    });

    /**
     * Test source
     */
    QUnit.test("Check Component source linkage", function() {
        var c1 = Factory.destinationComponent(Factory.dataFlow()),
            c2 = Factory.sourceComponent(Factory.dataFlow()),
            lm = Factory.linkManager();
        QUnit.equal(c1.acceptSource(c2.getComponentId()), true, "Source must be null");
        lm.link(c2,c1);
        QUnit.equal(c1.acceptSource(c2.getComponentId()), false, "Source must be setup");
    });
});