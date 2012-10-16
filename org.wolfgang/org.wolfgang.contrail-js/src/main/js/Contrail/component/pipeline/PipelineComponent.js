/*global define*/

define( [ "require", "../../core/jObj" ] , 
function(require, jObj) {
	
	function PipelineComponent() {
	    var Factory = require("../Factory");
        jObj.bless(this, Factory.sourceComponent(), Factory.destinationComponent());
	}
	
	return PipelineComponent;
});