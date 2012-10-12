/*global define*/

define( [ "jquery", "./component/ComponentFactory", "./link/LinkFactory", "./flow/FlowFactory" ] , 
function($, ComponentFactory, LinkFactory, flowFactory) {
	
    return $.extend({}, ComponentFactory, LinkFactory, flowFactory);
	
});