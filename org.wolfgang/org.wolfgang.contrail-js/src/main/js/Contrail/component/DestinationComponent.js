/*global define*/

define( [ "jquery", "require" ] , 
function($, require) {

	function DestinationComponent(upStreamDataFlow) {
		var Factory = require("../Factory");
		$.extend(this, Factory.component());
		this.upStreamDataFlow = upStreamDataFlow;
		this.sourceLink = null;
	}
	
	DestinationComponent.prototype.getUpStreamDataFlow = function () {
		return this.upStreamDataFlow;
	};
	
	DestinationComponent.prototype.acceptSource = function(ComponentId) {
		return (this.sourceLink === null);
	};
	
	DestinationComponent.prototype.connectSource = function(sourceLink) {
		var Factory = require("../Factory");
		this.sourceLink = sourceLink;
		return Factory.componentLink(this.sourceLink.getSource(),this);
	};	
	
	
	DestinationComponent.prototype.closeDownStream = function() {
	    if (this.sourceLink !== null) {
	        this.sourceLink.getSource().closeDownStream();
	        this.sourceLink = null;
	    }
	};
	
	DestinationComponent.prototype.closeUpStream = function() {
		this.upStreamDataFlow.handleClose();
	};
	
	return DestinationComponent;
});