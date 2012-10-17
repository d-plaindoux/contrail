/*global require */

require([ "../../factory/Factory", "../../core/jObj", "qunit" ], 
function(Factory, jObj, QUnit) {
    QUnit.test("Check Component generation", function() {
        var c1 = Factory.pipelineComponent(), 
            c2 = Factory.pipelineComponent();
        
        QUnit.notEqual(c1.getComponentId(), c2.getComponentId(), "Two fresh components must be different");
    });

    QUnit.test("Check Component type #1", function() {
        var c1 = Factory.pipelineComponent();
        
        QUnit.equal(jObj.instanceOf(c1,"PipelineComponent"),true, "Checking c1 instance of PipelineComponent");
    });

    QUnit.test("Check Component type #2", function() {
        var c1 = Factory.pipelineComponent();
        
        QUnit.equal(jObj.instanceOf(c1,"SourceComponent"),true, "Checking c1 instance of SourceComponent");
    });

    QUnit.test("Check Component type #3", function() {
        var c1 = Factory.pipelineComponent();
        
        QUnit.equal(jObj.instanceOf(c1,"DestinationComponent"),true, "Checking c1 instance of DestinationComponent");
    });

    QUnit.test("Check Component type #4", function() {
        var c1 = Factory.pipelineComponent();
        
        QUnit.equal(jObj.instanceOf(c1,"Component"),true, "Checking c1 instance of Component");
    });
});