/*global define*/
	
define([  ], function () {
	
    var Strict = {};
    
    function TypeError () {
        this.message = "the object has not the right type";
    }
	
    Strict.subType = function (obj,type) {
        return obj instanceof type;
    };
    
    Strict.assertSubType = function (obj,type) {
        if (!Strict.subType(obj,type)) {
            throw new TypeError();
        }
    };
    
    return Strict;
});
