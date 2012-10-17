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
        var i, key;
        
        if (arguments.length > 0) {
            $.extend.apply($, arguments);            

            //
            // Special traitments for inherit
            //

            arguments[0].inherit = {};
                        
            for(i = 1; i < arguments.length; i++) {
                for(key in arguments[i].inherit) {
                    arguments[0].inherit[key] = true;
                }
                arguments[0].inherit[jObj.getClass(arguments[i])] = true;
            }
        } 
    };
    
    jObj.instanceOf = function (object, type) {
        if (jObj.getClass(object) === type) {
            return true;
        } else if (object.inherit && object.inherit.hasOwnProperty(type)) {
            return true;
        } else {
            return false;
        }
    };
    
    jObj.toString = function (object,indent) {
        var nindent, key, content = "";
    
        if (indent === undefined) {
            indent = "";
        }

        nindent = indent + "  ";
       
        if (typeof object === "object") {
            
            for(key in object) {
                content += "\n" + nindent + key + ":" + jObj.toString(object[key],nindent);
            }
            return jObj.getClass(object) + " {" + content + "\n" + indent + "}";
        } else {
            return object + ";";
        }
    };
    
    return jObj;
});
