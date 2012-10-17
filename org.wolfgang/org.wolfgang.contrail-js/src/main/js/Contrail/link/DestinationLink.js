/*global define*/

define( [ "require", "../core/jObj", "../utils/Strict" ] ,
function(require, jObj, Strict) {

	function DestinationLink(destination,linkManager) {
		jObj.bless(this, require("../factory/Factory").link(linkManager));
        
		this.destination = destination;
	}
	
	DestinationLink.prototype.getDestination = function () {
	    return this.destination;
	};

	return DestinationLink;
});