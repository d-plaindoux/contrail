/*global define*/

define( [ "require",  "../core/jObj", "../utils/Strict" ] , 
function(require, jObj, Strict) {

	function SourceComponent() {
		jObj.bless(this, require("../Factory").component());

		this.destinationLink = null;
	}
	
	SourceComponent.prototype.getDownStreamDataFlow = undefined;
	
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
	
	SourceComponent.prototype.closeDownStream = undefined;
	
	return SourceComponent;
});