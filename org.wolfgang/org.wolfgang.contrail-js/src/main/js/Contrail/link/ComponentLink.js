/*global define*/

define( [ "../utils/Strict" ] , 
function(Strict) {
	
	function ComponentLink(source, destination) {
        Strict.assertType(source,"SourceComponent");
        Strict.assertType(destination,"DestinationComponent");

        this.source = source;
        this.destination = destination;
	}
	
	ComponentLink.prototype.dispose = function () {
		// TODO
	};

	return ComponentLink;
});