/*global define*/
	
define( [ "jquery" ], 
function ($) {
	
    var jObj = {};
    
    function TypeError () {
        this.message = "the object has not the right type";
    }
    
    jObj.getClass = function (object) {
        if (object && object.constructor && object.constructor.toString) {
            var arr = object.constructor.toString().match(/function\s*(\w+)/);
            if (arr && arr.length === 2) {
                return arr[1];
            }
        }

        return undefined;        
    };

    jObj.bless = function (/*arguments*/) {
        if (arguments.length > 1) {
            $.extend.apply($, arguments);
        }
 
        if (arguments.length > 0) {
            jObj.inheritance(arguments[0]);
        }
    };
    
    jObj.inheritance = function (object) {
        if (!object.ofType) {
            object.ofType = [];
        }
        
        object.ofType[jObj.getClass(object)] = true;
	};

    jObj.instanceOf = function (object, type) {
        if (!object.ofType) {
            return false;
        } else {
            return object.ofType.hasOwnProperty(type);
        }
    };
    
    return jObj;
});
