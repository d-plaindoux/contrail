/*global define*/

define( [ "../utils/Utils" ] , 
function(Utils) {
	
	function Component() {
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