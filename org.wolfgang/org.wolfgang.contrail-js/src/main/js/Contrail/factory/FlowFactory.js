/*global define*/

define( [ "../flow/DataFlow" ] , 
function(DataFlow, UpStreamDataFlow, DownStreamDataFlow) {
	
	var FlowFactory = {};

	FlowFactory.dataFlow = function () {
	    return new DataFlow();
	};
	
	return FlowFactory;
	
});