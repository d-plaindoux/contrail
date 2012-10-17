/*global require */

require([ "../factory/Factory", "../core/jObj", "qunit" ], 
function(Factory, jObj, QUnit) {
    /**
     * Test generation
     */
    QUnit.test("Check Component generation", function() {
        var c1 = Factory.sourceComponent(), 
            c2 = Factory.sourceComponent();
        
        QUnit.notEqual(c1.getComponentId(), c2.getComponentId(), "Two fresh components must be different");
    });
    
    /**
     * Test source
     */
    QUnit.test("Check Component destination linkage acceptation", function() {
        var c1 = Factory.destinationComponent(),
            c2 = Factory.sourceComponent();
        QUnit.equal(c2.acceptDestination(c1.getComponentId()), true, "Destination must be unbound");
    });

    /**
     * Test source
     */
    QUnit.test("Check Component source linkage", function() {
        var c1 = Factory.destinationComponent(),
            c2 = Factory.sourceComponent(),
            lm = Factory.linkManager();
        QUnit.equal(c2.acceptDestination(c1.getComponentId()), true, "Destination must be unbound");
        lm.link(c2,c1);
        QUnit.equal(c2.acceptDestination(c1.getComponentId()), false, "Destination must be setup");
    });
        
    /**
     * Test type
     */
    QUnit.test("Check Component type #1", function() {
        var c1 = Factory.sourceComponent();
        
       QUnit.equal(jObj.instanceOf(c1,"SourceComponent"),true, "Checking c1 instance of SourceComponent");
    });

    QUnit.test("Check Component type #2", function() {
        var c1 = Factory.sourceComponent();
        
       QUnit.equal(jObj.instanceOf(c1,"Component"),true, "Checking c1 instance of Component");
    });
});