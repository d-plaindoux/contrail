/*global define*/

define( [ "../utils/Utils" ] , function(Utils) {
	
	function Component() {
		this.identifier = Utils.UUID();
	}
	
	Component.prototype.type = { COMPONENT : 0x1 };

	Component.prototype.getComponentId = function() {
		return this.identifier;
	};

	return Component;
});