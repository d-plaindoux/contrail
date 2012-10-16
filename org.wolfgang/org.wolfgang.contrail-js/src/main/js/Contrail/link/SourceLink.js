/*global define*/

define( [ "require", "../core/jObj" ] ,
function(require, jObj) {

	function SourceLink(source,linkManager) {
	    jObj.bless(this, require("../Factory").link(linkManager));
		
		this.source= source;
	}
	
	SourceLink.prototype.getSource = function () {
	    return this.source;
	};

	return SourceLink;
});