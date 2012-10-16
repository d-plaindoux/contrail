/*global define*/
	
define( [ "require" ], 
function (require) {
	
    var Strict = {};
    
    function TypeError () {
        require("../core/jObj").bless(this);

        this.message = "the object has not the right type";
    }
    
    Strict.assertType = function (obj, type) {
        var jObj = require("../core/jObj");
        
        if (jObj.getClass(type) !== "String") {
            throw new Error(type + " must be an instance of String");
        } else if (!jObj.instanceOf(obj,type)) {
            throw new Error(obj + " must be an instance of " + type);
        }
    };
    
    return Strict;
});
