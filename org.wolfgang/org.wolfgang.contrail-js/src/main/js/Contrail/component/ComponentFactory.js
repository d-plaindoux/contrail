/*global define*/

define( [ "./Component", "./SourceComponent", "./DestinationComponent" ] , 
function(Component, SourceComponent, DestinationComponent) {
	
	var ComponentFactory = {};

	ComponentFactory.component = function () {
	    return new Component();
	};
	
	ComponentFactory.sourceComponent = function () {
	    return new SourceComponent();
	};
	
	ComponentFactory.destinationComponent = function () {
	    return new DestinationComponent();
	};
	
	return ComponentFactory;
	
});