/*global define*/

define( [ "require" ] ,
function(require) {
	
	function ComponentLinkManager() {
        this.links = [ ];
	}
	
	ComponentLinkManager.prototype.link = function (source, destination) {
	    if (!source.acceptDestination(destination.getComponentId())) {
	        throw new Error("Source cannot accept Destination");
	    } else if (!destination.acceptSource(source.getComponentId())) {
	        throw new Error("Destination cannot accept Source");
	    } else {
	        var Factory = require("../Factory");
	        source.connectDestination(Factory.destinationLink(destination,this));
	        destination.connectSource(Factory.sourceLink(source,this));
	    }
	};

	return ComponentLinkManager;
});