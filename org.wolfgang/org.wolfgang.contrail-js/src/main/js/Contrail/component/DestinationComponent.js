/*global define*/

define( [ "jquery", "require", "../utils/Strict" ] , 
function($, require, Strict) {

	function DestinationComponent(dataFlow) {
        Strict.assertType(dataFlow,"DataFlow");
        
		var Factory = require("../Factory");
		$.extend(this, Factory.component());
		this.upStreamDataFlow = dataFlow;
		this.sourceLink = null;
	}
	
	DestinationComponent.prototype.getUpStreamDataFlow = function () {
		return this.upStreamDataFlow;
	};
	
	DestinationComponent.prototype.acceptSource = function(componentId) {
		return (this.sourceLink === null);
	};
	
	DestinationComponent.prototype.connectSource = function(sourceLink) {
        Strict.assertType(sourceLink,"SourceLink");

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