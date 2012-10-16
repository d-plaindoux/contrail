/*global define*/
	
define( [ "require" ], 
function (require) {
	
    var Strict = {};
    
    function TypeError(message) {
        require("../core/jObj").bless(this);

        this.message = message;
    }
    
    Strict.assertType = function (obj, type) {
        var jObj = require("../core/jObj");
        
        if (jObj.getClass(type) !== "String") {
            throw new TypeError(type + " must be an instance of String");
        } else if (!jObj.instanceOf(obj,type)) {
            throw new TypeError(obj + " must be an instance of " + type);
        }
    };
    
    return Strict;
});
