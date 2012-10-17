/*global define*/

define( [ "require", "../core/jObj", "../utils/Strict" ] ,
function(require, jObj, Strict) {
	
	function ComponentLinkManager() {
        jObj.bless(this);
        
        this.links = [ ];
	}
	
	ComponentLinkManager.prototype.link = function (source, destination) {
	    // Check types
		
	    if (!source.acceptDestination(destination.getComponentId())) {
	        throw new Error("Source cannot accept Destination");
	    } else if (!destination.acceptSource(source.getComponentId())) {
	        throw new Error("Destination cannot accept Source");
	    } else {
	        var Factory = require("../factory/Factory");
	        source.connectDestination(Factory.destinationLink(destination,this));
	        destination.connectSource(Factory.sourceLink(source,this));
	    }
	};

	return ComponentLinkManager;
});