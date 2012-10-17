/*global define*/

define( [ "../utils/Strict", "../component/Component", "../component/SourceComponent", "../component/DestinationComponent", "../component/pipeline/PipelineComponent" ] , 
function(Strict, Component, SourceComponent, DestinationComponent, PipelineComponent) {
	
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
	
	ComponentFactory.pipelineComponent = function () {
		return new PipelineComponent();
	};
	
	return ComponentFactory;
	
});