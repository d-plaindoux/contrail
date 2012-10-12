/*global define*/

define( [ ] ,
function() {

	function Link(linkManager) {
		this.manager = linkManager;
	}

	Link.prototype.getLinkManager = function () {
	    return this.linkManager;
	};

	return Link;
});