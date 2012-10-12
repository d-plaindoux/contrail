/*global define*/

define( [ "jquery", "require" ] ,
function($, require) {

	function DestinationLink(destination,linkManager) {
		var Factory = require("../Factory");
		$.extend(this, Factory.link(linkManager));
		this.destination = destination;
	}
	
	DestinationLink.prototype.getDestination = function () {
	    return this.destination;
	};

	return DestinationLink;
});