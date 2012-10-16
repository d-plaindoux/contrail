/*global define*/

define( [ "../utils/Utils", "../core/jObj" ] , 
function(Utils, jObj) {
	
	function Component() {
        jObj.bless(this);
        
		this.identifier = Utils.UUID();
	}
	
	Component.prototype.getComponentId = function() {
		return this.identifier;
	};

	Component.prototype.closeUpStream = function() {
	    // Nothing
	};

	Component.prototype.closeDownStream = function() {
	    // Nothing
	};
	
	return Component;
});