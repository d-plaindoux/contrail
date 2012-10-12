/*global define*/

define( [ ] , 
function() {
	
	function ComponentLink(Source, Destination) {
        this.source = Source;
        this.destination = Destination;
	}
	
	ComponentLink.prototype.dispose = function () {
		// TODO
	};

	return ComponentLink;
});