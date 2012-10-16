/*global define*/

define( [ "require",  "../core/jObj", "../utils/Strict" ] , 
function(require, jObj, Strict) {

	function SourceComponent(dataFlow) {
        Strict.assertType(dataFlow,"DataFlow");

		jObj.bless(this, require("../Factory").component());

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
        Strict.assertType(destinationLink, "DestinationLink");

		this.destinationLink = destinationLink;
		return require("../Factory").componentLink(this, this.destinationLink.getDestination());
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