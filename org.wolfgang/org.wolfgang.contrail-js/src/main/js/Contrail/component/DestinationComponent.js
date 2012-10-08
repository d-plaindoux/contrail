/*global define*/

define( [ "jquery", "./Component", "./ComponentLink" ] , function($, Component, ComponentLink) {

	function DestinationComponent() {
		$.extend(this, new Component());
		this.source = null;
	}
	
	DestinationComponent.prototype.getUpStreamDataFlow = undefined;
	
	DestinationComponent.prototype.acceptSource = function(ComponentId) {
		if (this.source !== null) {
			return true;
		} else {
			return false;
		}
	};
	
	DestinationComponent.prototype.connectDestination = function(SourceComponent) {
		this.source = SourceComponent;
		return new ComponentLink(this.source,this);
	};	
	
	return DestinationComponent;
});