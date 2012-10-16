/*global define*/

define( [ "../core/jObj" ] ,
function(jObj) {

	function Link(linkManager) {
		jObj.bless(this);
		
		this.manager = linkManager;
	}

	Link.prototype.getLinkManager = function () {
	    return this.linkManager;
	};

	return Link;
});