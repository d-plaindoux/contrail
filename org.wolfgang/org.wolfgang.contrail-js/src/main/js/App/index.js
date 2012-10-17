/*global $, require */

$(function() {
	require([ "Contrail/factory/Factory", "Contrail/core/jObj", "Contrail/core/jDom" ], function(Factory, jObj, jDom) {
		var key, pipeline;
    
		pipeline = Factory.pipelineComponent();
		
		$("#inheritance").hide();   
		
		$("#inheritance").append(jDom("ul", {}));

		if (jObj.instanceOf(pipeline, "Component")) {
			$("#inheritance > ul").append(jDom("li",{style:"bullet"}," instance of Component"));			
		}
			
		if (jObj.instanceOf(pipeline, "SourceComponent")) {
			$("#inheritance > ul").append(jDom("li",{}," instance of SourceComponent"));			
		}

		if (jObj.instanceOf(pipeline, "DestinationComponent")) {
			$("#inheritance > ul").append(jDom("li",{}," instance of DestinationComponent"));			
		}
		
		$("#inheritance").append(jDom("pre", {}, jObj.toString(pipeline)));			
		
		$("#inheritance").fadeIn("slow");    
		
	});
});