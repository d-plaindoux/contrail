/*global define*/

define( [ "jquery", "require", "../utils/Strict" ] , 
function($, require, Strict) {

	function SourceComponent(dataFlow) {
        Strict.assertType(dataFlow,"DataFlow");

		var Factory = require("../Factory");
		$.extend(this, Factory.component());
		this.downStreamDataFlow = dataFlow;
		this.destinationLink = null;
	}
	
	SourceComponent.prototype.getDownStreamDataFlow = function () {
		return this.downStreamDataFlow;
	};
	
	SourceComponent.prototype.acceptDestination = function(componentId) {
		return this.destinationLink === null;
	};
	
	SourceComponent.prototype.connectDestination = function(destinationLink) {
        Strict.assertType(destinationLink,"DestinationLink");

		var Factory = require("../Factory");
		this.destinationLink = destinationLink;
		return Factory.componentLink(this, this.destinationLink.getDestination());
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