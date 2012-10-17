/*global define*/

define( [ "require", "../../core/jObj" ] , 
function(require, jObj) {
	
	function PipelineComponent() {
	    var Factory = require("../../factory/Factory");
        jObj.bless(this, Factory.sourceComponent(), Factory.destinationComponent());
	}
	
	return PipelineComponent;
});