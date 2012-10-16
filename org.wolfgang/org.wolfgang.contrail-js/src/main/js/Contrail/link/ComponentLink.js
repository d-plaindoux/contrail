/*global define*/

define( [ "../core/jObj", "../utils/Strict" ] , 
function(jObj, Strict) {
	
	function ComponentLink(source, destination) {
        Strict.assertType(source,"SourceComponent");
        Strict.assertType(destination,"DestinationComponent");

        jObj.bless(this);

        this.source = source;
        this.destination = destination;
	}
	
	ComponentLink.prototype.dispose = function () {
		// TODO
	};

	return ComponentLink;
});