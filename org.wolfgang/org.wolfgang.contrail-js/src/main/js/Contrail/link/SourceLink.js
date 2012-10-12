/*global define*/

define( [ "jquery", "require" ] ,
function($, require) {

	function SourceLink(source,linkManager) {
		var Factory = require("../Factory");
		$.extend(this, Factory.link(linkManager));		
		this.source= source;
	}
	
	SourceLink.prototype.getSource = function () {
	    return this.source;
	};

	return SourceLink;
});