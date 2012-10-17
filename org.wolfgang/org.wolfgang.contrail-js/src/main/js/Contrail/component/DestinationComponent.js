/*global define*/

define( [ "require", "../core/jObj", "../utils/Strict" ] , 
function(require, jObj, Strict) {

	function DestinationComponent() {
		jObj.bless(this, require("../factory/Factory").component());
		
		this.sourceLink = null;
	}
	
	DestinationComponent.prototype.getUpStreamDataFlow = undefined;
	
	DestinationComponent.prototype.acceptSource = function(componentId) {
		return (this.sourceLink === null);
	};
	
	DestinationComponent.prototype.connectSource = function(sourceLink) {
        Strict.assertType(sourceLink, "SourceLink");

		this.sourceLink = sourceLink;
		return require("../factory/Factory").componentLink(this.sourceLink.getSource(), this);
	};	
	
	DestinationComponent.prototype.closeDownStream = function() {
	    if (this.sourceLink !== null) {
	        this.sourceLink.getSource().closeDownStream();
	        this.sourceLink = null;
	    }
	};
	
	DestinationComponent.prototype.closeUpStream = undefined;
	
	return DestinationComponent;
});