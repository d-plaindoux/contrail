/*global require */

require([ "Contrail/Factory", "Core/jObj", "qunit" ], 
function(Factory, jObj, QUnit) {
    /**
     * Test generation
     */
    QUnit.test("Check Component generation", function() {
        var c1 = Factory.component.basic.destination(), 
            c2 = Factory.component.basic.destination();        
        QUnit.notEqual(c1.getComponentId(), c2.getComponentId(), "Two fresh components must be different");
    });

    /**
     * Test source
     */
    QUnit.test("Check Component source linkage acceptation", function() {
        var c1 = Factory.component.basic.destination(),
            c2 = Factory.component.basic.source();
        QUnit.equal(c1.acceptSource(c2.getComponentId()), true, "Source must be unbound");
    });

    /**
     * Test source
     */
    QUnit.test("Check Component source linkage", function() {
        var c1 = Factory.component.basic.destination(),
            c2 = Factory.component.basic.source(),
            lm = Factory.link.manager();
        QUnit.equal(c1.acceptSource(c2.getComponentId()), true, "Source must be unbound");
        lm.link(c2,c1);
        QUnit.equal(c1.acceptSource(c2.getComponentId()), false, "Source must be setup");
    });
    
    /**
     * Test type
     */
    QUnit.test("Check Component type #1", function() {
        var c1 = Factory.component.basic.destination();
        
       QUnit.equal(jObj.instanceOf(c1,"DestinationComponent"),true, "Checking c1 instance of DestinationComponent");
    });

    QUnit.test("Check Component type #2", function() {
        var c1 = Factory.component.basic.destination();
        
       QUnit.equal(jObj.instanceOf(c1,"Component"),true, "Checking c1 instance of Component");
    });
});