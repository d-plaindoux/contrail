/*global define*/

define( [ "jquery", "./Component", "./ComponentLink" ] , function($, Component, ComponentLink) {

	function SourceComponent() {
		$.extend(this, new Component());
		this.destination = null;
	}
	
	SourceComponent.prototype.getDownStreamDataFlow = undefined;
	
	SourceComponent.prototype.acceptDestination = function(ComponentId) {
		if (this.destination !== null) {
			return true;
		} else {
			return false;
		}
	};
	
	SourceComponent.prototype.connectDestination = function(DestinationComponent) {
		this.destination = DestinationComponent;
		return new ComponentLink(this,this.destination);
	};	
	
	return SourceComponent;
});