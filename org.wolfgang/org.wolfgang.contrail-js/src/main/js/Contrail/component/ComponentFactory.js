/*global define*/

define( [ "./Component", "./SourceComponent", "./DestinationComponent" ] , 
function(Component, SourceComponent, DestinationComponent) {
	
	var ComponentFactory = {};

	ComponentFactory.component = function () {
	    return new Component();
	};
	
	ComponentFactory.sourceComponent = function (dataFlow) {
	    return new SourceComponent(dataFlow);
	};

	ComponentFactory.destinationComponent = function (dataFlow) {
		return new DestinationComponent(dataFlow);
	};
	
	return ComponentFactory;
	
});