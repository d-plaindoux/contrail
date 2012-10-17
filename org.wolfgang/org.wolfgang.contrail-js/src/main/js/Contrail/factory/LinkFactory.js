/*global define*/

define( [ "../link/ComponentLinkManager", "../link/ComponentLink", "../link/Link", "../link/SourceLink", "../link/DestinationLink" ] , 
function(ComponentLinkManager, ComponentLink, Link, SourceLink, DestinationLink) {
	
	var LinkFactory = {};

	LinkFactory.linkManager = function () {
	    return new ComponentLinkManager();
	};

	LinkFactory.componentLink = function (source,destination) {
	    return new ComponentLink(source,destination);
	};

	LinkFactory.link = function (linkManager) {		
	    return new Link(linkManager);
	};

	LinkFactory.sourceLink = function (source,linkManager) {		
	    return new SourceLink(source, linkManager);
	};

	LinkFactory.destinationLink = function (destination,linkManager) {		
	    return new DestinationLink(destination, linkManager);
	};
	
	return LinkFactory;
	
});