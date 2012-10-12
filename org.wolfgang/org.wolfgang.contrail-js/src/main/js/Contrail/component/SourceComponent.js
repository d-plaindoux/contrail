/*global define*/

define( [ "jquery", "require" ] , 
function($, require) {

	function SourceComponent(downStreamDataFlow) {
		var Factory = require("../Factory");
		$.extend(this, Factory.component());
		this.downStreamDataFlow = downStreamDataFlow;
		this.destinationLink = null;
	}
	
	SourceComponent.prototype.getDownStreamDataFlow = function () {
		return this.downStreamDataFlow;
	};
	
	SourceComponent.prototype.acceptDestination = function(ComponentId) {
		return this.destinationLink === null;
	};
	
	SourceComponent.prototype.connectDestination = function(destinationLink) {
		var Factory = require("../Factory");
		this.destinationLink = destinationLink;
		return Factory.componentLink(this.destinationLink.getDestination(),this);
	};	
	
	
	SourceComponent.prototype.closeUpStream = function() {
	    if (this.destinationLink !== null) {
	        this.destinationLink.getSource().closeUpStream();
	        this.destinationLink = null;
	    }
	};
	
	SourceComponent.prototype.closeDownStream = function() {
		this.downStreamDataFlow.handleClose();
	};
	
	return SourceComponent;
});