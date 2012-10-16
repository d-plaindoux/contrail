/*global define*/

define( [ "require", "../core/jObj", "../utils/Strict" ] , 
function(require, jObj, Strict) {

	function DestinationComponent(dataFlow) {
        Strict.assertType(dataFlow, "DataFlow");
        
		jObj.bless(this, require("../Factory").component());
		
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
        Strict.assertType(sourceLink, "SourceLink");

		this.sourceLink = sourceLink;
		return require("../Factory").componentLink(this.sourceLink.getSource(),this);
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