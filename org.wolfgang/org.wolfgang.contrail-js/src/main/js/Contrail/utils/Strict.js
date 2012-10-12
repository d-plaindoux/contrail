/*global define*/
	
define( [], 
function () {
	
    var Strict = {};
    
    function TypeError () {
        this.message = "the object has not the right type";
    }
	
    Strict.getClass = function (obj) {
        if (obj && obj.constructor && obj.constructor.toString) {
            var arr = obj.constructor.toString().match(/function\s*(\w+)/);
            if (arr && arr.length === 2) {
                return arr[1];
            }
        }

        return undefined;        
    };

    Strict.subType = function (obj,type) {
        return type === Strict.getClass(obj);
    };
    
    Strict.assertType = function (obj,type) {
        if (!Strict.subType(type,"String")) {
            throw new Error(type + " must be a instance of String");
        } else if (!Strict.subType(obj,type)) {
            throw new Error(Strict.getClass(obj) + " must be a " + type);
        }
    };
    
    return Strict;
});
