/*global define*/
	
define( [ "require" ], 
function (require) {
	
    var Strict = {};
    
    function TypeError(message) {
        require("../core/jObj").bless(this);

        this.message = message;
    }
    
    Strict.assertType = function (object, type) {
        var jObj = require("../core/jObj");
        
        if (!jObj.instanceOf(type, "String")) {
            throw new TypeError(type + " must be an instance of String");
        } else if (!jObj.instanceOf(object,type)) {
            throw new TypeError(object + " must be an instance of " + type);
        }
    };
    
    return Strict;
});
