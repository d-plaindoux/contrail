/*global define*/

define( [ "jquery", "./ComponentFactory", "./LinkFactory", "./FlowFactory" ] , 
function($, ComponentFactory, LinkFactory, flowFactory) {
	
    return $.extend({}, ComponentFactory, LinkFactory, flowFactory);
	
});